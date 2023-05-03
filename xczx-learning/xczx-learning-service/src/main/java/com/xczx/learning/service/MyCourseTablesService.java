package com.xczx.learning.service;

import com.xczx.learning.model.dto.XcChooseCourseDto;
import com.xczx.learning.model.dto.XcCourseTablesDto;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/5/3
 * @description:
 */
public interface MyCourseTablesService {
    XcChooseCourseDto addChooseCourse(String userId, Long courseId);

     XcCourseTablesDto getLearningStatus(String userId, Long courseId);
}
