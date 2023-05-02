package com.xczx.search.controller;

import com.xczx.base.model.dto.PageParams;
import com.xczx.search.model.dto.SearchCourseParamDto;
import com.xczx.search.model.dto.SearchPageResultDto;
import com.xczx.search.model.po.CourseIndex;
import com.xczx.search.service.CourseSearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Mr.M
 * @version 1.0
 * @description 课程搜索接口
 * @date 2022/9/24 22:31
 */
@Api(value = "课程搜索接口", tags = "课程搜索接口")
@RestController
@RequestMapping("/course")
public class CourseSearchController {

    @Resource
   private CourseSearchService courseSearchService;


    @ApiOperation("课程搜索列表")
    @GetMapping("/list")
    public SearchPageResultDto<CourseIndex> list(PageParams pageParams, SearchCourseParamDto searchCourseParamDto) {
        return courseSearchService.queryCoursePubIndex(pageParams, searchCourseParamDto);

    }
}
