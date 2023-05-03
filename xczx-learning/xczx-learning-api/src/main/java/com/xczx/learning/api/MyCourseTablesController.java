package com.xczx.learning.api;

import com.xczx.base.exception.XczxException;
import com.xczx.learning.model.dto.XcChooseCourseDto;
import com.xczx.learning.model.dto.XcCourseTablesDto;
import com.xczx.learning.service.MyCourseTablesService;
import com.xczx.learning.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Mr.M
 * @version 1.0
 * @description 我的课程表接口
 * @date 2022/10/25 9:40
 */

@Api(value = "我的课程表接口", tags = "我的课程表接口")
@Slf4j
@RestController
public class MyCourseTablesController {


    @Resource
    private MyCourseTablesService myCourseTablesService;

    @ApiOperation("添加选课")
    @PostMapping("/choosecourse/{courseId}")
    public XcChooseCourseDto addChooseCourse(@PathVariable("courseId") Long courseId) {
        String userId = SecurityUtil.getUser().getId();
        return myCourseTablesService.addChooseCourse(userId, courseId);
    }

    @ApiOperation("查询学习资格")
    @PostMapping("/choosecourse/learnstatus/{courseId}")
    public XcCourseTablesDto getLearnstatus(@PathVariable("courseId") Long courseId) {
        //登录用户
        SecurityUtil.XcUser user = SecurityUtil.getUser();
        if (user == null) {
            throw new XczxException("请登录后继续选课");
        }
        String userId = user.getId();
        return myCourseTablesService.getLearningStatus(userId, courseId);
    }
}
