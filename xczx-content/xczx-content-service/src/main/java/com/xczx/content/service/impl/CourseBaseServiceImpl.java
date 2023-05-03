package com.xczx.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xczx.base.exception.XczxException;
import com.xczx.base.model.dto.PageParams;
import com.xczx.base.model.vo.PageResult;
import com.xczx.content.mapper.CourseBaseMapper;
import com.xczx.content.model.dto.AddCourseDto;
import com.xczx.content.model.dto.QueryCourseParamsDto;
import com.xczx.content.model.dto.UpdateCourseDto;
import com.xczx.content.model.po.CourseBase;
import com.xczx.content.model.po.CourseMarket;
import com.xczx.content.model.vo.CourseBaseInfo;
import com.xczx.content.service.CourseBaseService;
import com.xczx.content.service.CourseCategoryService;
import com.xczx.content.service.CourseMarketService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Objects;

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

    @Resource
    private CourseMarketService courseMarketService;

    @Resource
    private CourseCategoryService courseCategoryService;

    @Override
    public PageResult<CourseBase> selectByConditionWithPage(String companyId, PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        // 构建查询条件
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        String courseName = queryCourseParamsDto.getCourseName();
        String auditStatus = queryCourseParamsDto.getAuditStatus();
        String publishStatus = queryCourseParamsDto.getPublishStatus();

        queryWrapper.like(StringUtils.hasText(courseName), CourseBase::getName, courseName);
        queryWrapper.eq(StringUtils.hasText(auditStatus), CourseBase::getAuditStatus, auditStatus);
        queryWrapper.eq(StringUtils.hasText(publishStatus), CourseBase::getStatus, publishStatus);
        queryWrapper.eq(StringUtils.hasText(companyId), CourseBase::getCompanyId, companyId);

        // 查询数据
        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(page, queryWrapper);

        // 封装结果
        return new PageResult<>(pageParams.getPageNo(), pageParams.getPageSize(), courseBasePage.getTotal(), courseBasePage.getRecords());
    }

    @Override
    @Transactional
    public CourseBaseInfo createBaseCourse(Long companyId, AddCourseDto addCourseDto) {
        // 参数校验
        if (addCourseDto.getPrice() < 0 || addCourseDto.getOriginalPrice() < 0) {
            throw new XczxException("价格必须大于0");
        }
        // 将数据插入到课程基本信息表
        CourseBase courseBase = new CourseBase();
        BeanUtils.copyProperties(addCourseDto, courseBase);
        // 设置机构信息
        courseBase.setCompanyId(companyId);
        courseBase.setCompanyName("");
        //设置审核状态
        courseBase.setAuditStatus("202002");
        //设置发布状态
        courseBase.setStatus("203001");
        //添加时间
        courseBase.setCreateDate(LocalDateTime.now());

        int affectRows = courseBaseMapper.insert(courseBase);
        if (affectRows <= 0) {
            throw new XczxException("新增课程基本信息失败");
        }

        // 将数据插入到课程基本营销信息表
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(addCourseDto, courseMarket);
        courseMarket.setId(courseBase.getId());
        affectRows = courseMarketService.saveCourseMarket(courseMarket);
        if (affectRows <= 0) {
            throw new XczxException("新增课程营销信息失败");
        }

        return selectCourseBaseInfoById(courseBase.getId());
    }

    @Override
    public CourseBaseInfo selectCourseBaseInfoById(Long courseId) {

        CourseBase base = courseBaseMapper.selectById(courseId);
        CourseMarket market = courseMarketService.getById(courseId);

        CourseBaseInfo courseBaseInfo = new CourseBaseInfo();

        if (base != null) {
            String stName = courseCategoryService.selectNameById(base.getSt());
            String mtName = courseCategoryService.selectNameById(base.getMt());
            courseBaseInfo.setStName(stName);
            courseBaseInfo.setMtName(mtName);
            BeanUtils.copyProperties(base, courseBaseInfo);
        }
        if (market != null) {
            BeanUtils.copyProperties(market, courseBaseInfo);
        }
        return courseBaseInfo;
    }

    @Override
    @Transactional
    public CourseBaseInfo updateBaseCourse(Long companyId, UpdateCourseDto updateCourseDto) {
        // 更新课程基本信息表
        CourseBase courseBase = new CourseBase();
        BeanUtils.copyProperties(updateCourseDto, courseBase);
        int affectRows = courseBaseMapper.updateById(courseBase);
        if (affectRows <= 0) {
            throw new XczxException("课程基本信息更新失败");
        }

        // 更新课程营销信息表
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(updateCourseDto, courseMarket);
        boolean updateSuccess = courseMarketService.updateById(courseMarket);
        if (!updateSuccess) {
            throw new XczxException("课程营销信息更新失败");
        }

        // 查询更新之后的课程信息
        return selectCourseBaseInfoById(updateCourseDto.getId());
    }


    @Override
    public void deleteBaseCourse(Long companyId, String courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (!Objects.equals(courseBase.getCompanyId(), companyId)) {
            throw new XczxException("只能删除本机构课程信息");
        }
        if (!"202002".equals(courseBase.getAuditStatus())) {
            throw new XczxException("只能删除未提交的课程");
        }
        int affectRows = courseBaseMapper.deleteById(courseId);
        if (affectRows <= 0) {
            throw new XczxException("删除失败");
        }
    }
}