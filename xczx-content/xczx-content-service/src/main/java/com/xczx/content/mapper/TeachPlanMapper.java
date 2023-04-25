package com.xczx.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xczx.content.model.po.Teachplan;
import com.xczx.content.model.vo.TeachPlanBaseInfoVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 课程计划 Mapper 接口
 * </p>
 *
 * @author ilovesshan
 */
public interface TeachPlanMapper extends BaseMapper<Teachplan> {

    List<TeachPlanBaseInfoVo> selectPlanById(String courseId);

    Teachplan selectPrevTeachPlanById(@Param("orderBy") Integer orderBy, @Param("parentId") Long parentId);

    Teachplan selectNextTeachPlanById(@Param("orderBy") Integer orderBy, @Param("parentId") Long parentId);
}
