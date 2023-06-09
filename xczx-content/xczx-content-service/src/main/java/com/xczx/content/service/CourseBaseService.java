package com.xczx.content.service;

import com.xczx.base.model.dto.PageParams;
import com.xczx.base.model.vo.PageResult;
import com.xczx.content.model.dto.AddCourseDto;
import com.xczx.content.model.dto.QueryCourseParamsDto;
import com.xczx.content.model.dto.UpdateCourseDto;
import com.xczx.content.model.po.CourseBase;
import com.xczx.content.model.vo.CourseBaseInfo;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/23
 * @description:
 */

public interface CourseBaseService {
    PageResult<CourseBase> selectByConditionWithPage(String companyId, PageParams pageParams, QueryCourseParamsDto dto);

    CourseBaseInfo createBaseCourse(Long companyId, AddCourseDto addCourseDto);

    CourseBaseInfo selectCourseBaseInfoById(Long courseId);

    CourseBaseInfo updateBaseCourse(Long companyId, UpdateCourseDto updateCourseDto);

    void deleteBaseCourse(Long companyId,String courseId);
}
