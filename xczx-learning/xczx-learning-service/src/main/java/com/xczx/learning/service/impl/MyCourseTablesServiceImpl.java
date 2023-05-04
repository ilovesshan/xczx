package com.xczx.learning.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xczx.base.exception.XczxException;
import com.xczx.feign.content.client.CoursePublishClient;
import com.xczx.feign.content.model.CoursePublish;
import com.xczx.learning.mapper.XcChooseCourseMapper;
import com.xczx.learning.mapper.XcCourseTablesMapper;
import com.xczx.learning.model.dto.XcChooseCourseDto;
import com.xczx.learning.model.dto.XcCourseTablesDto;
import com.xczx.learning.model.po.XcChooseCourse;
import com.xczx.learning.model.po.XcCourseTables;
import com.xczx.learning.service.MyCourseTablesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/5/3
 * @description:
 */

@Slf4j
@Service
public class MyCourseTablesServiceImpl implements MyCourseTablesService {

    @Resource
    private CoursePublishClient coursePublishClient;

    @Resource
    private XcChooseCourseMapper xcChooseCourseMapper;

    @Resource
    private XcCourseTablesMapper xcCourseTablesMapper;


    @Override
    @Transactional
    public XcChooseCourseDto addChooseCourse(String userId, Long courseId) {
        // 通过远程服务查询课程发布信息(是否收费)
        CoursePublish coursepublish = coursePublishClient.getCoursePublish(courseId);
        XcChooseCourse xcChooseCourse = null;
        // 免费
        if ("201000".equals(coursepublish.getCharge())) {
            xcChooseCourse = addFreeCourse(courseId, userId, coursepublish);
        } else {
            // 收费
            xcChooseCourse = addChargeCourse(courseId, userId, coursepublish);
        }

        // 获取学生学习资格信息
        XcCourseTablesDto xcCourseTablesDto = getLearningStatus(userId, courseId);

        XcChooseCourseDto xcChooseCourseDto = new XcChooseCourseDto();
        BeanUtils.copyProperties(xcChooseCourse, xcChooseCourseDto);
        xcChooseCourseDto.setLearnStatus(xcCourseTablesDto.getLearnStatus());
        return xcChooseCourseDto;
    }

    @Override
    // [{"code":"702001","desc":"正常学习"},
    // {"code":"702002","desc":"没有选课或选课后没有支付"},
    // {"code":"702003","desc":"已过期需要申请续期或重新支付"}]
    public XcCourseTablesDto getLearningStatus(String userId, Long courseId) {
        XcCourseTablesDto xcCourseTablesDto = new XcCourseTablesDto();

        LambdaQueryWrapper<XcCourseTables> queryWrapper =
                new LambdaQueryWrapper<XcCourseTables>()
                        .eq(XcCourseTables::getUserId, userId)
                        .eq(XcCourseTables::getCourseId, courseId);
        XcCourseTables xcCourseTables = xcCourseTablesMapper.selectOne(queryWrapper);

        // {"code":"702002","desc":"没有选课或选课后没有支付"}
        if (xcCourseTables == null) {
            xcCourseTablesDto.setLearnStatus("702002");
            return xcCourseTablesDto;
        }
        BeanUtils.copyProperties(xcCourseTables, xcCourseTablesDto);
        // 查看课程学习期限是否过期
        if (xcCourseTables.getValidtimeEnd().isBefore(LocalDateTime.now())) {
            // {"code":"702003","desc":"已过期需要申请续期或重新支付"}
            xcCourseTablesDto.setLearnStatus("702003");
            return xcCourseTablesDto;
        } else {
            // {"code":"702001","desc":"正常学习"}
            xcCourseTablesDto.setLearnStatus("702001");
            return xcCourseTablesDto;
        }
    }

    @Override
    @Transactional
    public boolean saveChooseCourseStatus(String chooseCourseId) {

        XcChooseCourse xcChooseCourse = xcChooseCourseMapper.selectById(chooseCourseId);
        if (xcChooseCourse == null) {
            log.error("添加课程到课程表时，未从选课表中插入该{}ID对应的记录", chooseCourseId);
            return false;
        }

        if ("701001".equals(xcChooseCourse.getStatus())) {
            log.error("添加课程到课程表时，查询到{}ID对应的记录已完成支付", chooseCourseId);
            return false;
        }

        xcChooseCourse.setStatus("701001");
        int affectRows = xcChooseCourseMapper.updateById(xcChooseCourse);
        if (affectRows <= 0) {
            log.error("添加课程到课程表时，更新选课表状态为选课成功时失败，ID = {}", chooseCourseId);
            throw new XczxException("更新选课表状态失败");
        }
        XcCourseTables xcCourseTables = new XcCourseTables();
        BeanUtils.copyProperties(xcChooseCourse, xcCourseTables);
        xcCourseTables.setChooseCourseId(Long.valueOf(chooseCourseId));
        affectRows = xcCourseTablesMapper.insert(xcCourseTables);
        if (affectRows <= 0) {
            log.error("添加课程到课程表失败，ID = {}", chooseCourseId);
            throw new XczxException("添加课程到课程表失败");
        }
        return true;
    }

    // 添加收费课程逻辑
    private XcChooseCourse addChargeCourse(Long courseId, String userId, CoursePublish coursepublish) {
        // 1、添加记录到选课表(状态待支付，如果存在就不再添加了)
        // 判断条件：类型是收费并且状态选课是待支付
        LambdaQueryWrapper<XcChooseCourse> lambdaQueryWrapper =
                new LambdaQueryWrapper<XcChooseCourse>()
                        .eq(XcChooseCourse::getCourseId, courseId)
                        // 状态选课 待支付
                        .eq(XcChooseCourse::getStatus, "701002")
                        // 选课类型 收费
                        .eq(XcChooseCourse::getOrderType, "700002");
        List<XcChooseCourse> xcChooseCourses = xcChooseCourseMapper.selectList(lambdaQueryWrapper);
        // 证明该课程已经选过了，直接返回
        if (xcChooseCourses.size() > 0) {
            return xcChooseCourses.get(0);
        }
        XcChooseCourse xcChooseCourse = new XcChooseCourse();
        xcChooseCourse.setCourseId(courseId);
        xcChooseCourse.setCourseName(coursepublish.getName());
        xcChooseCourse.setUserId(userId);
        xcChooseCourse.setCompanyId(coursepublish.getCompanyId());
        xcChooseCourse.setOrderType("700002");
        xcChooseCourse.setCreateDate(LocalDateTime.now());
        xcChooseCourse.setCoursePrice(coursepublish.getPrice());
        xcChooseCourse.setValidDays(coursepublish.getValidDays());
        xcChooseCourse.setValidtimeStart(LocalDateTime.now());
        xcChooseCourse.setStatus("701002");
        xcChooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(coursepublish.getValidDays()));
        int affectRows = xcChooseCourseMapper.insert(xcChooseCourse);
        if (affectRows <= 0) {
            throw new XczxException("添加课程失败");
        }
        return xcChooseCourse;
    }


    // 添加免费课程逻辑
    public XcChooseCourse addFreeCourse(Long courseId, String userId, CoursePublish coursepublish) {
        // 1、添加记录到选课表(如果存在就不再添加了)
        // 判断条件：类型是免费并且状态选课是成功
        LambdaQueryWrapper<XcChooseCourse> lambdaQueryWrapper =
                new LambdaQueryWrapper<XcChooseCourse>()
                        .eq(XcChooseCourse::getCourseId, courseId)
                        // 状态选课 成功
                        .eq(XcChooseCourse::getStatus, "701001")
                        // 选课类型 免费
                        .eq(XcChooseCourse::getOrderType, "700001");
        List<XcChooseCourse> xcChooseCourses = xcChooseCourseMapper.selectList(lambdaQueryWrapper);
        // 证明该课程已经选过了，直接返回
        if (xcChooseCourses.size() > 0) {
            return xcChooseCourses.get(0);
        }
        XcChooseCourse xcChooseCourse = new XcChooseCourse();
        xcChooseCourse.setCourseId(courseId);
        xcChooseCourse.setCourseName(coursepublish.getName());
        xcChooseCourse.setUserId(userId);
        xcChooseCourse.setCompanyId(coursepublish.getCompanyId());
        xcChooseCourse.setOrderType("700001");
        xcChooseCourse.setCreateDate(LocalDateTime.now());
        xcChooseCourse.setCoursePrice(coursepublish.getPrice());
        xcChooseCourse.setValidDays(coursepublish.getValidDays());
        xcChooseCourse.setValidtimeStart(LocalDateTime.now());
        xcChooseCourse.setStatus("701001");
        xcChooseCourse.setValidtimeEnd(LocalDateTime.now().plusDays(coursepublish.getValidDays()));
        int affectRows = xcChooseCourseMapper.insert(xcChooseCourse);
        if (affectRows > 0) {
            // 2、添加记录到课程表
            XcCourseTables xcCourseTables = new XcCourseTables();
            BeanUtils.copyProperties(xcChooseCourse, xcCourseTables);
            xcCourseTables.setChooseCourseId(xcChooseCourse.getId());
            affectRows += xcCourseTablesMapper.insert(xcCourseTables);
        }
        if (affectRows != 2) {
            throw new XczxException("添加课程失败");
        }
        return xcChooseCourse;
    }
}
