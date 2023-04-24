package com.xczx.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xczx.content.model.dto.InsertOrUpdateCoursePlanDto;
import com.xczx.content.model.po.Teachplan;
import com.xczx.content.model.vo.TeachPlanBaseInfoVo;

import java.util.List;

/**
 * <p>
 * 课程计划 服务类
 * </p>
 *
 * @author ilovesshan
 * @since 2023-04-22
 */
public interface TeachPlanService extends IService<Teachplan> {

    List<TeachPlanBaseInfoVo> selectCoursePlanTree(String courseId);

    void insertOrUpdateCoursePlan(InsertOrUpdateCoursePlanDto insertOrUpdateCoursePlanDto);
}
