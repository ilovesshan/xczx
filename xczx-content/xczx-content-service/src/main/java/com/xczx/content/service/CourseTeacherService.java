package com.xczx.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xczx.content.model.dto.AddOrUpdateCourseTeacherDto;
import com.xczx.content.model.po.CourseTeacher;

import java.util.List;

/**
 * <p>
 * 课程-教师关系表 服务类
 * </p>
 *
 * @author ilovesshan
 * @since 2023-04-22
 */
public interface CourseTeacherService extends IService<CourseTeacher> {
    List<CourseTeacher> selectCourseTeacherListByCourseId(String courseId);

    void addOrUpdateCourseTeacher(Long companyId, AddOrUpdateCourseTeacherDto addOrUpdateCourseTeacherDto);

    void deleteCourseTeacher(Long companyId, String courseId, String teacherId);
}
