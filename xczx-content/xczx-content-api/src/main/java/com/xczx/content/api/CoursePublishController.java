package com.xczx.content.api;

import com.xczx.feign.content.model.CoursePublish;
import com.xczx.content.model.vo.CoursePreviewVo;
import com.xczx.content.service.CoursePublishPreService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

@Api("课程管理模块")
@Controller
public class CoursePublishController {

    @Resource
    private CoursePublishPreService coursePublishPreService;

    @ApiOperation("课程预览")
    @GetMapping("/coursepreview/{id}")
    public ModelAndView coursePreview(@PathVariable("id") String id) {
        //获取课程预览信息
        CoursePreviewVo coursePreviewInfo = coursePublishPreService.getCoursePreviewInfo(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("model", coursePreviewInfo);
        modelAndView.setViewName("course_template");
        return modelAndView;
    }

    @ApiOperation("课程提交审核")
    @ResponseBody
    @PostMapping("/courseaudit/commit/{courseId}")
    public void commitAudit(@PathVariable("courseId") Long courseId) {
        coursePublishPreService.commitAudit(1001101L, courseId);
    }

    @ApiOperation("课程发布")
    @ResponseBody
    @PostMapping("/coursepublish/{courseId}")
    public void coursePublish(@PathVariable("courseId") Long courseId) {
        coursePublishPreService.coursePublish(1001101L, courseId);
    }

    @ApiOperation("查询课程发布信息")
    @ResponseBody
    @GetMapping("/r/coursepublish/{courseId}")
    public CoursePublish getCoursePublish(@PathVariable("courseId") Long courseId) {
        return coursePublishPreService.getCoursePublish(courseId);
    }
}