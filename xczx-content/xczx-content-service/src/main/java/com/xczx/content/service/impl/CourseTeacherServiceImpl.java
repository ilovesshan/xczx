package com.xczx.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xczx.base.exception.XczxException;
import com.xczx.content.mapper.CourseTeacherMapper;
import com.xczx.content.model.dto.AddOrUpdateCourseTeacherDto;
import com.xczx.content.model.po.CourseTeacher;
import com.xczx.content.model.vo.CourseBaseInfo;
import com.xczx.content.service.CourseBaseService;
import com.xczx.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 课程-教师关系表 服务实现类
 * </p>
 *
 * @author ilovesshan
 */
@Slf4j
@Service
public class CourseTeacherServiceImpl extends ServiceImpl<CourseTeacherMapper, CourseTeacher> implements CourseTeacherService {

    @Resource
    private CourseTeacherMapper courseTeacherMapper;

    @Resource
    private CourseBaseService courseBaseService;

    @Override
    public List<CourseTeacher> selectCourseTeacherListByCourseId(String courseId) {
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(CourseTeacher::getCourseId, courseId);
        return courseTeacherMapper.selectList(queryWrapper);
    }


    @Override
    public void addOrUpdateCourseTeacher(Long companyId, AddOrUpdateCourseTeacherDto addOrUpdateCourseTeacherDto) {
        CourseBaseInfo courseBaseInfo = courseBaseService.selectCourseBaseInfoById(addOrUpdateCourseTeacherDto.getCourseId());
        if (!Objects.equals(courseBaseInfo.getCompanyId(), companyId)) {
            throw new XczxException("只能操作本机构课程的师资信息");
        }
        CourseTeacher courseTeacher = new CourseTeacher();
        BeanUtils.copyProperties(addOrUpdateCourseTeacherDto, courseTeacher);

        if (addOrUpdateCourseTeacherDto.getId() == null) {
            // 新增
            int affectRows = courseTeacherMapper.insert(courseTeacher);
            if (affectRows <= 0) {
                throw new XczxException("新增失败");
            }
        } else {
            // 更新
            int affectRows = courseTeacherMapper.updateById(courseTeacher);
            if (affectRows <= 0) {
                throw new XczxException("更新失败");
            }
        }
    }

    @Override
    public void deleteCourseTeacher(Long companyId, String courseId, String teacherId) {
        CourseBaseInfo courseBaseInfo = courseBaseService.selectCourseBaseInfoById(Long.valueOf(courseId));
        if (!Objects.equals(courseBaseInfo.getCompanyId(), companyId)) {
            throw new XczxException("只能操作本机构课程的师资信息");
        }
        LambdaQueryWrapper<CourseTeacher> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(CourseTeacher::getCourseId, courseId)
                .eq(CourseTeacher::getId, teacherId);

        int affectRows = courseTeacherMapper.delete(queryWrapper);
        if (affectRows <= 0) {
            throw new XczxException("删除失败");
        }
    }
}
