package com.xczx.content.api;

import com.xczx.base.model.dto.PageParams;
import com.xczx.base.model.vo.PageResult;
import com.xczx.content.model.dto.AddCourseDto;
import com.xczx.content.model.dto.QueryCourseParamsDto;
import com.xczx.content.model.po.CourseBase;
import com.xczx.content.model.vo.CourseBaseInfo;
import com.xczx.content.service.CourseBaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    @ApiOperation("课程查询接口")
    @PostMapping("/course/list")
    public PageResult<CourseBase> list(PageParams pageParams, @RequestBody QueryCourseParamsDto queryCourseParamsDto) {
        return courseBaseService.selectByConditionWithPage(pageParams, queryCourseParamsDto);
    }

    @ApiOperation("课程基础信息新增接口")
    @PostMapping("/course")
    public CourseBaseInfo createBaseCourse(@RequestBody AddCourseDto addCourseDto) {
        return courseBaseService.createBaseCourse(1001101L, addCourseDto);
    }
}
