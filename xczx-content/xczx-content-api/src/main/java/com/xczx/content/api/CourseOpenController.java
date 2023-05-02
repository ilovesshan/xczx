package com.xczx.content.api;

import com.xczx.content.model.vo.CoursePreviewVo;
import com.xczx.content.service.CoursePublishPreService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/30
 * @description:
 */

@Api(value = "课程公开查询接口", tags = "课程公开查询接口")
@RestController
@RequestMapping("/open")
public class CourseOpenController {

    @Autowired
    private CoursePublishPreService coursePublishPreService;

    @GetMapping("/course/whole/{courseId}")
    public CoursePreviewVo getPreviewInfo(@PathVariable("courseId") String courseId) {
        //获取课程预览信息
        return coursePublishPreService.getCoursePreviewInfo(courseId);
    }
}