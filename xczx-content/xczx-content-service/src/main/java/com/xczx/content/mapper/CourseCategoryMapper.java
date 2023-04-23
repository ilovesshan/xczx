package com.xczx.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xczx.content.model.vo.CourseCategoryVo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/23
 * @description:
 */

public interface CourseCategoryMapper extends BaseMapper<CourseCategoryVo> {

    List<CourseCategoryVo> selectAll();

    String selectNameById(String id);
}
