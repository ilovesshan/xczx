package com.xczx.content.api;

import com.xczx.content.model.dto.AddOrUpdateCourseTeacherDto;
import com.xczx.content.model.po.CourseTeacher;
import com.xczx.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/22
 * @description:
 */

@Api(value = "课程师资模块接口", tags = "课程师资模块接口")
@RestController
public class CourseTeacherController {

    @Resource
    private CourseTeacherService courseTeacherService;

    @ApiOperation("课程师资列表查询")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> selectCourseTeacherList(@PathVariable("courseId") String courseId) {
        return courseTeacherService.selectCourseTeacherListByCourseId(courseId);
    }

    @ApiOperation("新增/编辑课程师资")
    @PostMapping("/courseTeacher")
    public void addOrUpdateCourseTeacher(@Validated @RequestBody AddOrUpdateCourseTeacherDto addOrUpdateCourseTeacherDto) {
        courseTeacherService.addOrUpdateCourseTeacher(1001101L, addOrUpdateCourseTeacherDto);
    }

    @ApiOperation("删除课程师资")
    @DeleteMapping("courseTeacher/course/{courseId}/{TeacherId}")
    public void deleteCourseTeacher(@PathVariable("courseId") String courseId, @PathVariable("TeacherId") String TeacherId) {
        courseTeacherService.deleteCourseTeacher(1001101L, courseId, TeacherId);
    }
}
