package com.xczx.learning.service.impl;

import com.xczx.base.model.vo.RestResponse;
import com.xczx.feign.content.client.CoursePublishClient;
import com.xczx.feign.content.model.CoursePublish;
import com.xczx.feign.media.client.MediaFileClient;
import com.xczx.learning.model.dto.XcCourseTablesDto;
import com.xczx.learning.service.LearningService;
import com.xczx.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/5/5
 * @description:
 */


@Slf4j
@Service
public class LearningServiceImpl implements LearningService {

    @Resource
    private MyCourseTablesService myCourseTablesService;

    @Resource
    private MediaFileClient mediaFileClient;

    @Resource
    private CoursePublishClient coursePublishClient;

    @Override
    public RestResponse<String> getVideoInfo(String userId, Long courseId, Long teachplanId, String mediaId) {

        // 查询课程信息
        CoursePublish coursePublish = coursePublishClient.getCoursePublish(courseId);
        if (coursePublish == null) {
            log.error("课程信息不存在, userId = {}, courseId = {}", userId, courseId);
            return RestResponse.validfail("课程信息不存在");
        }

        if (StringUtils.isEmpty(userId)) {
            // 登录过了
            XcCourseTablesDto courseTables = myCourseTablesService.getLearningStatus(userId, courseId);
            String learnStatus = courseTables.getLearnStatus();
            if ("702002".equals(learnStatus)) {
                // 没有选课或选课后没有支付
                log.error("没有选课或选课后没有支付, userId = {}, courseId = {}", userId, courseId);
                return RestResponse.validfail("没有选课或选课后没有支付");
            } else if ("702003".equals(learnStatus)) {
                // 已过期需要申请续期或重新支付
                log.error("已过期需要申请续期或重新支付, userId = {}, courseId = {}", userId, courseId);
                return RestResponse.validfail("已过期需要申请续期或重新支付");
            } else {
                // 正常学习, 远程调用媒资服务查询视频播放地址
                return getStringRestResponse(mediaId, userId);
            }
        }

        //未登录或未选课判断是否收费
        String charge = coursePublish.getCharge();
        if (charge.equals("201000")) {//免费可以正常学习
            // 免费学习，远程调用媒资服务查询视频播放地址
            return getStringRestResponse(mediaId, userId);
        }
        return RestResponse.validfail("请购买课程后继续学习");

    }

    @NotNull
    private RestResponse<String> getStringRestResponse(String mediaId, String userId) {
        RestResponse<String> mediaPlayUrl = mediaFileClient.getPlayUrlByMediaId(mediaId);
        if (mediaPlayUrl == null) {
            log.error("获取文件预览地址失败, userId = {}, mediaId = {}", userId, mediaId);
            return RestResponse.validfail("获取文件预览地址失败");
        }
        return mediaPlayUrl;
    }
}
