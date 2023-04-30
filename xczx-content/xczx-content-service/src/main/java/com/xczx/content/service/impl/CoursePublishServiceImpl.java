package com.xczx.content.service.impl;

import com.xczx.content.mapper.CourseBaseMapper;
import com.xczx.content.model.po.CourseBase;
import com.xczx.content.model.po.CourseMarket;
import com.xczx.content.model.vo.CourseBaseInfo;
import com.xczx.content.model.vo.CoursePreviewVo;
import com.xczx.content.model.vo.TeachPlanBaseInfoVo;
import com.xczx.content.service.CourseMarketService;
import com.xczx.content.service.CoursePublishService;
import com.xczx.content.service.TeachPlanService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/30
 * @description:
 */

@Service
public class CoursePublishServiceImpl implements CoursePublishService {

    @Resource
    private CourseBaseMapper courseBaseMapper;

    @Resource
    private TeachPlanService teachPlanService;

    @Resource
    private CourseMarketService courseMarketService;

    @Override
    public CoursePreviewVo getCoursePreviewInfo(String courseId) {

        // 查询课程基本信息、营销信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        CourseMarket courseMarket = courseMarketService.getById(courseId);
        CourseBaseInfo courseBaseInfo = new CourseBaseInfo();
        if (courseBase != null) {
            BeanUtils.copyProperties(courseBase, courseBaseInfo);
        }
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfo);
        }

        //课程计划信息
        List<TeachPlanBaseInfoVo> baseInfoVos = teachPlanService.selectCoursePlanTree(courseId);

        CoursePreviewVo coursePreviewDto = new CoursePreviewVo();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(baseInfoVos);
        return coursePreviewDto;
    }
}
