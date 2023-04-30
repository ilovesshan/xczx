package com.xczx.content.api;

import com.xczx.content.model.vo.CoursePreviewVo;
import com.xczx.content.service.CoursePublishService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

@Controller
public class CoursePublishController {

    @Resource
    private CoursePublishService coursePublishService;

    @GetMapping("/coursepreview/{id}")
    public ModelAndView coursePreview(@PathVariable("id") String id) {
        //获取课程预览信息
        CoursePreviewVo coursePreviewInfo = coursePublishService.getCoursePreviewInfo(id);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("model", coursePreviewInfo);
        modelAndView.setViewName("course_template");
        return modelAndView;
    }
}