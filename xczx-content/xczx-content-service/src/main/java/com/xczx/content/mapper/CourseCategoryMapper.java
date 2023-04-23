package com.xczx.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xczx.content.model.vo.CourseCategoryVo;

import java.util.List;

/**
 * <p>
 * 课程基本信息 Mapper 接口
 * </p>
 *
 * @author ilovesshan
 */
public interface CourseCategoryMapper extends BaseMapper<CourseCategoryVo> {

    List<CourseCategoryVo> selectAll();
}
