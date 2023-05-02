package com.xczx.content.service.jobhandler;

import com.xczx.base.exception.XczxException;
import com.xczx.content.model.po.CoursePublish;
import com.xczx.content.model.vo.CoursePreviewVo;
import com.xczx.content.service.CoursePublishPreService;
import com.xczx.content.service.CoursePublishService;
import com.xczx.feign.config.MultipartSupportConfig;
import com.xczx.feign.medis.client.MediaFileClient;
import com.xczx.feign.search.client.SearchServiceClient;
import com.xczx.feign.search.model.CourseIndex;
import com.xczx.messagesdk.model.po.MqMessage;
import com.xczx.messagesdk.service.MessageProcessAbstract;
import com.xczx.messagesdk.service.MqMessageService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * XxlJob开发示例（Bean模式）
 * <p>
 * 开发步骤：
 * 1、任务开发：在Spring Bean实例中，开发Job方法；
 * 2、注解配置：为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 3、执行日志：需要通过 "XxlJobHelper.log" 打印执行日志；
 * 4、任务结果：默认任务结果为 "成功" 状态，不需要主动设置；如有诉求，比如设置任务结果为失败，可以通过 "XxlJobHelper.handleFail/handleSuccess" 自主设置任务结果；
 */

@Slf4j
@Component
public class CoursePublishTask extends MessageProcessAbstract {

    @Resource
    private CoursePublishPreService coursePublishPreService;

    @Resource
    private MediaFileClient mediaFileClient;

    @Resource
    private SearchServiceClient searchServiceClient;

    @Resource
    private CoursePublishService coursePublishService;


    /**
     * 处理课程发布的广播任务
     */
    @XxlJob("coursePublishJobHandler")
    public void coursePublishJobHandler() throws Exception {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("分片参数：当前分片序号 = {}, 总分片数 = {}", shardIndex, shardTotal);


        // 调用父类处理核心业逻辑的方法
        process(shardIndex, shardTotal, "course_publish", 30, 30);
    }


    @Override
    public boolean execute(MqMessage mqMessage) {
        // 获取任务ID和课程ID
        Long taskId = mqMessage.getId();
        String courseId = mqMessage.getBusinessKey1();

        // 处理课程播放页面静态化（上传到MinIO）
        generateCourseHtml(taskId, courseId, mqMessage);

        // 将课程索引同步到到ES中
        saveCourseIndexToEs(taskId, courseId, mqMessage);

        // 将课信息缓存到Redis中
        saveCourseCacheToRedis(taskId, courseId, mqMessage);
        return true;
    }

    private void generateCourseHtml(Long taskId, String courseId, MqMessage mqMessage) {
        log.debug("开始执行：处理课程播放页面静态化（上传到MinIO）任务... - taskId = {},courseId = {}", taskId, courseId);
        // 保证任务幂等性
        MqMessageService mqMessageService = this.getMqMessageService();
        int stageOne = mqMessageService.getStageOne(taskId);
        if (stageOne > 0) {
            // 第一阶段任务已经完成
            log.debug("第一阶段任务已经完成");
            return;
        }
        // 根据课程ID生成成静态HTML模板
        File staticTemplateHtml = createStaticTemplateHtml(courseId);
        if (staticTemplateHtml == null) {
            return;
        }
        // 将静态HTML模板上传到MinIO中
        String uploadResult = uploadStaticTemplateHtmlToMinIO(courseId, staticTemplateHtml);
        if (uploadResult == null) {
            return;
        }
        //保存第一阶段状态
        mqMessageService.completedStageOne(taskId);
    }


    private void saveCourseIndexToEs(Long taskId, String courseId, MqMessage mqMessage) {
        log.debug("开始执行：将课程索引同步到到ES中任务... - taskId = {},courseId = {}", taskId, courseId);
        // 保证任务幂等性
        MqMessageService mqMessageService = this.getMqMessageService();
        int stageTwo = mqMessageService.getStageTwo(taskId);
        if (stageTwo > 0) {
            // 第二阶段任务已经完成
            log.debug("第二阶段任务已经完成");
            return;
        }

        //取出课程发布信息
        CoursePublish coursePublish = coursePublishService.getById(courseId);
        //拷贝至课程索引对象
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish, courseIndex);
        //远程调用搜索服务api添加课程信息到索引
        Boolean add = searchServiceClient.add(courseIndex);
        if (!add) {
            throw new XczxException("添加索引失败");
        }
        //保存第二阶段状态
        mqMessageService.completedStageTwo(taskId);
    }

    private void saveCourseCacheToRedis(Long taskId, String courseId, MqMessage mqMessage) {
        log.debug("开始执行：将课信息缓存到Redis中任务... - taskId = {},courseId = {}", taskId, courseId);
    }

    // 根据课程ID生成成静态HTML模板
    private File createStaticTemplateHtml(String courseId) {
        File file = null;
        try {
            Configuration configuration = new Configuration(Configuration.getVersion());
            String classpath = this.getClass().getResource("/").getPath();
            configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
            configuration.setDefaultEncoding("utf-8");
            Template template = configuration.getTemplate("course_template.ftl");
            CoursePreviewVo coursePreviewInfo = coursePublishPreService.getCoursePreviewInfo(courseId);
            Map<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewInfo);
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            System.out.println(content);
            InputStream inputStream = IOUtils.toInputStream(content);
            file = File.createTempFile("courseTemplate", ".html");
            IOUtils.copy(inputStream, new FileOutputStream(file));
        } catch (Exception e) {
            log.error(" 生成静态HTML模板发生异常：courseId = {}, error = {}", courseId, e.getMessage());
            e.printStackTrace();
        }
        return file;
    }

    // 将静态HTML模板上传到MinIO中
    private String uploadStaticTemplateHtmlToMinIO(String courseId, File file) {
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        String uploadResult = mediaFileClient.upload(multipartFile, "course/" + courseId + ".html");
        if (uploadResult == null) {
            log.error("课程静态页面文件上传到MinIO失败");
        }
        return uploadResult;
    }
}
