package com.xczx.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xczx.base.exception.XczxException;
import com.xczx.content.mapper.CourseBaseMapper;
import com.xczx.content.mapper.CoursePublishPreMapper;
import com.xczx.content.model.po.*;
import com.xczx.content.model.vo.CourseBaseInfo;
import com.xczx.content.model.vo.CoursePreviewVo;
import com.xczx.content.model.vo.TeachPlanBaseInfoVo;
import com.xczx.content.service.*;
import com.xczx.messagesdk.model.po.MqMessage;
import com.xczx.messagesdk.service.MqMessageService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/30
 * @description:
 */

@Service
public class CoursePublishPreServiceImpl extends ServiceImpl<CoursePublishPreMapper, CoursePublishPre> implements CoursePublishPreService {

    @Resource
    private CourseBaseMapper courseBaseMapper;

    @Resource
    private CourseBaseService courseBaseService;

    @Resource
    private TeachPlanService teachPlanService;

    @Resource
    private CourseMarketService courseMarketService;

    @Resource
    private CourseTeacherService courseTeacherService;

    @Resource
    private CoursePublishPreMapper coursePublishPreMapper;

    @Resource
    private CoursePublishService coursePublishService;

    @Resource
    private MqMessageService mqMessageService;


    @Override
    public CoursePreviewVo getCoursePreviewInfo(String courseId) {
        // 查询课程基本信息、营销信息
        CourseBaseInfo courseBaseInfo = courseBaseService.selectCourseBaseInfoById(Long.valueOf(courseId));

        //课程计划信息
        List<TeachPlanBaseInfoVo> baseInfoVos = teachPlanService.selectCoursePlanTree(courseId);

        CoursePreviewVo coursePreviewDto = new CoursePreviewVo();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(baseInfoVos);
        return coursePreviewDto;
    }


    @Override
    @Transactional
    public void commitAudit(long companyId, Long courseId) {
        // 查询课程基本信息
        CourseBaseInfo courseBaseInfo = courseBaseService.selectCourseBaseInfoById(courseId);

        // 校验课程信息，不满足则不能发布
        if (courseBaseInfo == null) {
            throw new XczxException("未找到对应课程");
        }

        if ("202003".equals(courseBaseInfo.getStatus())) {
            throw new XczxException("当前为等待审核状态，审核完成可以再次提交");
        }

        if (companyId != courseBaseInfo.getCompanyId()) {
            throw new XczxException("不允许提交其它机构的课程");
        }
        if (StringUtils.isEmpty(courseBaseInfo.getPic())) {
            throw new XczxException("提交失败，请上传课程图片");
        }

        // 查询课程管联信息（课程营销信息、课程计划信息、课程教师信息）
        CourseMarket courseMarket = courseMarketService.getById(courseId);
        List<TeachPlanBaseInfoVo> teachPlanBaseInfoVos = teachPlanService.selectCoursePlanTree(String.valueOf(courseId));
        List<CourseTeacher> teachers = courseTeacherService.selectCourseTeacherListByCourseId(String.valueOf(courseId));

        // 将以上信息合并成一个对象，插入到课程预发布表（存在就更新，不存在则新增）
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        BeanUtils.copyProperties(courseMarket, coursePublishPre);
        BeanUtils.copyProperties(courseBaseInfo, coursePublishPre);

        coursePublishPre.setMarket(JSON.toJSONString(courseMarket));
        coursePublishPre.setTeachplan(JSON.toJSONString(teachPlanBaseInfoVos));
        coursePublishPre.setTeachers(JSON.toJSONString(teachers));
        coursePublishPre.setCreateDate(LocalDateTime.now());
        // 更新课程审核状态(已提交)
        coursePublishPre.setStatus("202003");

        CoursePublishPre coursePublish = coursePublishPreMapper.selectById(courseBaseInfo.getId());
        if (coursePublish != null) {
            // 更新
            coursePublishPreMapper.updateById(coursePublishPre);
        } else {
            // 新增
            coursePublishPreMapper.insert(coursePublishPre);
        }

        // 更新课程基本信息表的课程审核状态
        CourseBase courseBase1 = courseBaseMapper.selectById(courseId);
        courseBase1.setStatus("202003");
        courseBaseMapper.updateById(courseBase1);
    }

    @Override
    @Transactional
    public void coursePublish(Long companyId, Long courseId) {
        //查询课程预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            throw new XczxException("请先提交课程审核，审核通过才可以发布");
        }

        //本机构只允许提交本机构的课程
        if (!coursePublishPre.getCompanyId().equals(companyId)) {
            throw new XczxException("不允许提交其它机构的课程");
        }

        //课程审核状态
        String auditStatus = coursePublishPre.getStatus();
        //审核通过方可发布
        if (!"202004".equals(auditStatus)) {
            throw new XczxException("操作失败，课程审核通过方可发布");
        }

        //保存课程发布信息
        saveCoursePublish(courseId);

        //保存消息表
        saveCoursePublishMessage(courseId);

        //删除课程预发布表对应记录
        coursePublishPreMapper.deleteById(courseId);
    }

    private void saveCoursePublish(Long courseId) {
        //整合课程发布信息
        //查询课程预发布表
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            throw new XczxException("课程预发布数据为空");
        }

        CoursePublish coursePublish = new CoursePublish();

        //拷贝到课程发布对象
        BeanUtils.copyProperties(coursePublishPre, coursePublish);
        coursePublish.setStatus("203002");
        CoursePublish coursePublishUpdate = coursePublishService.getById(courseId);
        if (coursePublishUpdate == null) {
            coursePublishService.save(coursePublish);
        } else {
            coursePublishService.updateById(coursePublish);
        }
        //更新课程基本表的发布状态
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setStatus("203002");
        courseBaseMapper.updateById(courseBase);
    }


    /**
     * @param courseId 课程id
     * @return void
     * @description 保存消息表记录
     * @author Mr.M
     * @date 2022/9/20 16:32
     */
    private void saveCoursePublishMessage(Long courseId) {
        MqMessage mqMessage = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if (mqMessage == null) {
            throw new XczxException("操作失败，系统异常");
        }
    }
}
