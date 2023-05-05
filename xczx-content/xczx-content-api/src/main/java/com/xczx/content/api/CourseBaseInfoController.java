package com.xczx.content.api;

import com.xczx.base.model.dto.PageParams;
import com.xczx.base.model.vo.PageResult;
import com.xczx.config.SecurityUtil;
import com.xczx.content.model.dto.AddCourseDto;
import com.xczx.content.model.dto.QueryCourseParamsDto;
import com.xczx.content.model.dto.UpdateCourseDto;
import com.xczx.content.model.po.CourseBase;
import com.xczx.content.model.vo.CourseBaseInfo;
import com.xczx.content.service.CourseBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/22
 * @description:
 */

@Api(value = "课程模块接口", tags = "课程模块接口")
@RestController
public class CourseBaseInfoController {

    @Resource
    private CourseBaseService courseBaseService;

    @ApiOperation("课程列表查询")
    @PostMapping("/course/list")
    @PreAuthorize("hasAuthority('xc_teachmanager_course_list')")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody QueryCourseParamsDto queryCourseParamsDto) {
        String companyId = SecurityUtil.getUser().getCompanyId();
        return courseBaseService.selectByConditionWithPage(companyId, pageParams, queryCourseParamsDto);
    }

    @ApiOperation("新增课程基础信息")
    @PostMapping("/course")
    public CourseBaseInfo createBaseCourse(@Validated @RequestBody AddCourseDto addCourseDto) {
        return courseBaseService.createBaseCourse(1001101L, addCourseDto);
    }

    @ApiOperation("课程详情查询")
    @GetMapping("/course/{id}")
    public CourseBaseInfo selectById(@PathVariable("id") Long id) {
        return courseBaseService.selectCourseBaseInfoById(id);
    }

    @ApiOperation("更新课程基础信息")
    @PutMapping("/course")
    public CourseBaseInfo updateBaseCourse(@Validated @RequestBody UpdateCourseDto updateCourseDto) {
        return courseBaseService.updateBaseCourse(1001101L, updateCourseDto);
    }

    @ApiOperation("删除课程基础信息")
    @DeleteMapping("/course/{courseId}")
    public void deleteBaseCourse(@PathVariable("courseId") String courseId) {
        courseBaseService.deleteBaseCourse(1001101L, courseId);
    }
}
