package com.xczx.content.service;

import com.xczx.content.model.vo.CourseCategoryVo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/23
 * @description:
 */
public interface CourseCategoryService {
    List<CourseCategoryVo> queryCourseCategory(String pid);
}
