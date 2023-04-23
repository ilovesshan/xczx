package com.xczx.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xczx.base.model.dto.PageParams;
import com.xczx.base.model.vo.PageResult;
import com.xczx.content.mapper.CourseBaseMapper;
import com.xczx.content.model.dto.QueryCourseParamsDto;
import com.xczx.content.model.po.CourseBase;
import com.xczx.content.service.CourseBaseService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/23
 * @description:
 */

@Service
public class CourseBaseServiceImpl implements CourseBaseService {
    @Resource
    private CourseBaseMapper courseBaseMapper;

    @Override
    public PageResult<CourseBase> selectByConditionWithPage(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {

        // 构建查询条件
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        String courseName = queryCourseParamsDto.getCourseName();
        String auditStatus = queryCourseParamsDto.getAuditStatus();
        String publishStatus = queryCourseParamsDto.getPublishStatus();
        queryWrapper.like(StringUtils.hasText(courseName), CourseBase::getName, courseName);
        queryWrapper.eq(StringUtils.hasText(auditStatus), CourseBase::getAuditStatus, auditStatus);
        queryWrapper.eq(StringUtils.hasText(publishStatus), CourseBase::getStatus, publishStatus);

        // 查询数据
        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(page, queryWrapper);

        // 封装结果
        return new PageResult<>(
                pageParams.getPageNo(),
                pageParams.getPageSize(),
                courseBasePage.getTotal(),
                courseBasePage.getRecords()
        );
    }
}
