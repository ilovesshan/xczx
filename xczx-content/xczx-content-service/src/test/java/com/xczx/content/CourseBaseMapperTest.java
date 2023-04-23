package com.xczx.content;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xczx.content.mapper.CourseBaseMapper;
import com.xczx.content.model.dto.QueryCourseParamsDto;
import com.xczx.content.model.po.CourseBase;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/23
 * @description:
 */

@SpringBootTest
public class CourseBaseMapperTest {

    @Resource
    private CourseBaseMapper courseBaseMapper;

    @Test
    void testSelectByID() {
        CourseBase courseBase = courseBaseMapper.selectById(1L);
        System.out.println("courseBase = " + courseBase);
    }


    @Test
    void testSelectByPageAndCondition() {
        QueryCourseParamsDto queryCourseParamsDto = new QueryCourseParamsDto("", "Java", "");

        // 设置分页条件
        Page<CourseBase> page = new Page<>(2, 3);

        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        // 设置查询条件
        queryWrapper.like(StringUtils.hasText(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName());
        queryWrapper.eq(StringUtils.hasText(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());
        queryWrapper.eq(StringUtils.hasText(queryCourseParamsDto.getPublishStatus()), CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());
        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(page, queryWrapper);

        List<CourseBase> records = courseBasePage.getRecords();
        System.out.println("records = " + records);
    }
}
