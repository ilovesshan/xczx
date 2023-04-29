package com.xczx.media.service.jobhandler;

import com.xczx.base.utils.Mp4VideoUtil;
import com.xczx.media.model.po.MediaProcess;
import com.xczx.media.service.MediaFileService;
import com.xczx.media.service.MediaProcessService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
public class VideoTask {

    @Value("${minio.bucket.videofiles}")
    private String videofiles;

    @Value("${ffmpeg.ffmpeg-exe-path}")
    private String ffmpegExePath;


    @Resource
    private MediaFileService mediaFileService;

    @Resource
    private MediaProcessService mediaProcessService;


    /**
     * 处理视频转换的广播任务
     */
    @XxlJob("videoJobHandler")
    public void videoJobHandler() throws Exception {
        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        log.debug("分片参数：当前分片序号 = {}, 总分片数 = {}", shardIndex, shardTotal);

        // 获取CPU核数
        int processors = Runtime.getRuntime().availableProcessors();
        // 查询任务
        List<MediaProcess> mediaProcessList = mediaProcessService.selectMediaProcess(shardIndex, shardTotal, processors);
        int mediaProcessSize = mediaProcessList.size();
        if (mediaProcessSize <= 0) {
            return;
        }
        // 创建线程池根据实际的任务个数
        ExecutorService service = Executors.newFixedThreadPool(mediaProcessSize);
        // 遍历任务列表

        CountDownLatch countDownLatch = new CountDownLatch(mediaProcessSize);
        mediaProcessList.forEach(mediaProcess -> {
            // 开启多线程并行执行任务
            service.submit(() -> {
                try {
                    Long processId = mediaProcess.getId();
                    String fileId = mediaProcess.getFileId();

                    // 开启任务(抢占任务)
                    boolean process = mediaProcessService.startMediaProcess(processId);
                    if (!process) {
                        log.error("任务抢占失败，taskID：{}", processId);
                        mediaProcessService.updateMediaProcess(processId, fileId, 3, null, "任务抢占失败");
                        return;
                    }

                    // 下载MinIO中的文件到本地
                    String objectName = mediaProcess.getFilePath();
                    File downloadFile = mediaFileService.downloadFileFromMinIO(videofiles, objectName);
                    if (downloadFile == null) {
                        log.error("下载MinIO中的文件到本地失败，taskID：{}, bucket： {}, objectName：{}", processId, videofiles, objectName);
                        mediaProcessService.updateMediaProcess(processId, fileId, 3, null, "下载MinIO中的文件到本地失败");
                        return;
                    }

                    // 进行文件类型转换
                    String resultText = "";
                    File tempFile = null;
                    try {
                        //源avi视频的路径
                        String originAviFilePath = downloadFile.getAbsolutePath();
                        // 创建临时文件，用于存储转换之后的文件
                        tempFile = File.createTempFile("tmp", ".mp4");
                        //创建工具类对象
                        Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegExePath, originAviFilePath, "tmp.avi", tempFile.getAbsolutePath());
                        //开始视频转换，成功将返回success
                        resultText = videoUtil.generateMp4();
                    } catch (IOException e) {
                        log.error("ffmpeg文件类型转换失败，taskID：{}, bucket： {}, objectName：{} - error：{}", processId, videofiles, objectName, e.getMessage());
                        e.printStackTrace();
                    }
                    if (!"success".equals(resultText)) {
                        mediaProcessService.updateMediaProcess(processId, fileId, 3, null, "ffmpeg文件类型转换失败");
                        return;
                    }
                    // 将转换后的文件上传到MInIO中
                    String pathByMd5 = getFilePathByMd5(mediaProcess.getFileId(), ".mp4");
                    boolean uploadFileToMinioResult = mediaFileService.uploadFileToMinio(
                            videofiles,
                            pathByMd5,
                            tempFile.getAbsolutePath(),
                            "video/x-msvideo"
                    );
                    if (!uploadFileToMinioResult) {
                        log.error("文件转换后上传到MinIO失败，taskID：{}, bucket： {}, objectName：{}", processId, videofiles, objectName);
                        mediaProcessService.updateMediaProcess(processId, fileId, 3, null, "文件转换后上传到MinIO失败");
                        return;
                    }
                    // 上传成功,更新任务执行结果
                    mediaProcessService.updateMediaProcess(processId, fileId, 2, "/" + videofiles + "/" + pathByMd5, "");
                } finally {
                    countDownLatch.countDown();
                }
            });
        });
        // 兜底策略，超过30分钟还没执行结束则结束任务
        countDownLatch.await(30, TimeUnit.SECONDS);


        // 分片参数
//        int shardIndex = XxlJobHelper.getShardIndex();//执行器的序号，从0开始
//        int shardTotal = XxlJobHelper.getShardTotal();//执行器总数
//
//        //确定cpu的核心数
//        int processors = Runtime.getRuntime().availableProcessors();
//        //查询待处理的任务
//        List<MediaProcess> mediaProcessList = mediaProcessService.selectMediaProcess(shardIndex, shardTotal, processors);
//
//        //任务数量
//        int size = mediaProcessList.size();
//        log.debug("取到视频处理任务数:" + size);
//        if (size <= 0) {
//            return;
//        }
//        //创建一个线程池
//        ExecutorService executorService = Executors.newFixedThreadPool(size);
//        //使用的计数器
//        CountDownLatch countDownLatch = new CountDownLatch(size);
//        mediaProcessList.forEach(mediaProcess -> {
//            //将任务加入线程池
//            executorService.execute(() -> {
//                try {
//                    //任务id
//                    Long taskId = mediaProcess.getId();
//                    //文件id就是md5
//                    String fileId = mediaProcess.getFileId();
//                    //开启任务
//                    boolean b = mediaProcessService.startMediaProcess(taskId);
//                    if (!b) {
//                        log.debug("抢占任务失败,任务id:{}", taskId);
//                        return;
//                    }
//                    //桶
//                    String bucket = mediaProcess.getBucket();
//                    //objectName
//                    String objectName = mediaProcess.getFilePath();
//
//                    //下载minio视频到本地
//                    File file = mediaFileService.downloadFileFromMinIO(bucket, objectName);
//                    if (file == null) {
//                        log.debug("下载视频出错,任务id:{},bucket:{},objectName:{}", taskId, bucket, objectName);
//                        //保存任务处理失败的结果
//                        mediaProcessService.updateMediaProcess(taskId, fileId, 3, null, "下载视频到本地失败");
//                        return;
//                    }
//
//                    //源avi视频的路径
//                    String video_path = file.getAbsolutePath();
//                    //转换后mp4文件的名称
//                    String mp4_name = fileId + ".mp4";
//                    //转换后mp4文件的路径
//                    //先创建一个临时文件，作为转换后的文件
//                    File mp4File = null;
//                    try {
//                        mp4File = File.createTempFile("minio", ".mp4");
//                    } catch (IOException e) {
//                        log.debug("创建临时文件异常,{}", e.getMessage());
//                        //保存任务处理失败的结果
//                        mediaProcessService.updateMediaProcess(taskId, fileId, 3, null, "创建临时文件异常");
//                        return;
//                    }
//                    String mp4_path = mp4File.getAbsolutePath();
//                    //创建工具类对象
//                    Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegExePath, video_path, mp4_name, mp4_path);
//                    //开始视频转换，成功将返回success,失败返回失败原因
//                    String result = videoUtil.generateMp4();
//                    if (!result.equals("success")) {
//
//                        log.debug("视频转码失败,原因:{},bucket:{},objectName:{},", result, bucket, objectName);
//                        mediaProcessService.updateMediaProcess(taskId, fileId, 3, null, "视频转码失败");
//                        return;
//
//                    }
//                    //上传到minio
//                    boolean b1 = mediaFileService.uploadFileToMinio(videofiles, objectName, mp4File.getAbsolutePath(), "video/mp4");
//                    if (!b1) {
//                        log.debug("上传mp4到minio失败,taskid:{}", taskId);
//                        mediaProcessService.updateMediaProcess(taskId, fileId, 3, null, "上传mp4到minio失败");
//                        return;
//                    }
//
//                    //mp4文件的url
//                    String url = getFilePathByMd5(fileId, ".mp4");
//
//                    //更新任务状态为成功
//                    mediaProcessService.updateMediaProcess(taskId, fileId, 2, url, "创建临时文件异常");
//                } finally {
//                    //计算器减去1
//                    countDownLatch.countDown();
//                }
//            });
//        });
//
//        //阻塞,指定最大限制的等待时间，阻塞最多等待一定的时间后就解除阻塞
//        countDownLatch.await(30, TimeUnit.MINUTES);
    }

    private String getFilePathByMd5(String fileMd5, String fileExt) {
        return fileMd5.charAt(0) + "/" + fileMd5.charAt(1) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }
}
