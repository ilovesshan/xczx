package com.xczx.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xczx.base.exception.XczxException;
import com.xczx.content.mapper.TeachPlanMapper;
import com.xczx.content.model.dto.BindingCourseMediaDto;
import com.xczx.content.model.dto.InsertOrUpdateCoursePlanDto;
import com.xczx.content.model.po.Teachplan;
import com.xczx.content.model.po.TeachplanMedia;
import com.xczx.content.model.vo.TeachPlanBaseInfoVo;
import com.xczx.content.model.vo.TeachPlanVo;
import com.xczx.content.service.TeachPlanMediaService;
import com.xczx.content.service.TeachPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程计划 服务实现类
 * </p>
 *
 * @author ilovesshan
 */
@Slf4j
@Service
public class TeachPlanServiceImpl extends ServiceImpl<TeachPlanMapper, Teachplan> implements TeachPlanService {

    @Resource
    private TeachPlanMapper teachPlanMapper;

    @Resource
    private TeachPlanMediaService teachPlanMediaService;

    @Override
    public List<TeachPlanBaseInfoVo> selectCoursePlanTree(String courseId) {
        List<TeachPlanBaseInfoVo> selectedTeachPlanBaseInfo = teachPlanMapper.selectPlanById(courseId);

        List<TeachPlanBaseInfoVo> responseTeachPlanBaseInfo = new ArrayList<>();

        Map<Long, TeachPlanBaseInfoVo> teachPlanBaseInfoVoMap = selectedTeachPlanBaseInfo.stream()
                .collect(Collectors.toMap(TeachPlanVo::getId, key -> key, (k1, k2) -> k1));

        selectedTeachPlanBaseInfo.forEach(teachPlanBaseInfoVo -> {
            if (teachPlanBaseInfoVo.getParentid() == 0) {
                if (teachPlanBaseInfoVo.getTeachPlanTreeNodes() == null) {
                    teachPlanBaseInfoVo.setTeachPlanTreeNodes(new ArrayList<>());
                }
                responseTeachPlanBaseInfo.add(teachPlanBaseInfoVo);
            }

            TeachPlanBaseInfoVo parentNode = teachPlanBaseInfoVoMap.get(teachPlanBaseInfoVo.getParentid());
            if (parentNode != null) {
                parentNode.getTeachPlanTreeNodes().add(teachPlanBaseInfoVo);
            }
        });

        return responseTeachPlanBaseInfo;
    }

    @Override
    public void insertOrUpdateCoursePlan(InsertOrUpdateCoursePlanDto insertOrUpdateCoursePlanDto) {
        Long planId = insertOrUpdateCoursePlanDto.getId();
        if (planId == null) {
            // 新增
            Teachplan teachplan = new Teachplan();
            BeanUtils.copyProperties(insertOrUpdateCoursePlanDto, teachplan);

            // 设置排序规则值 查询同级目录下最大的排序号
            LambdaQueryWrapper<Teachplan> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(Teachplan::getParentid, teachplan.getParentid());
            lambdaQueryWrapper.eq(Teachplan::getCourseId, teachplan.getCourseId());
            Integer orderBy = teachPlanMapper.selectCount(lambdaQueryWrapper);
            teachplan.setOrderby(orderBy + 1);
            int affectRows = teachPlanMapper.insert(teachplan);
            if (affectRows <= 0) {
                throw new XczxException("课程计划信息新增失败");
            }
        } else {
            // 更新
            Teachplan selectedTeachPlan = teachPlanMapper.selectById(planId);
            BeanUtils.copyProperties(insertOrUpdateCoursePlanDto, selectedTeachPlan);
            int affectRows = teachPlanMapper.updateById(selectedTeachPlan);
            if (affectRows <= 0) {
                throw new XczxException("课程计划信息更新失败");
            }
        }
    }

    @Override
    public void deleteCoursePlan(String planId) {
        Teachplan selectedTeachPlan = teachPlanMapper.selectById(planId);

        if (selectedTeachPlan == null) {
            throw new XczxException("课程计划不存在，无效的课程计划ID");
        }

        if (selectedTeachPlan.getParentid() == 0) {
            // 删除父节点
            LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Teachplan::getParentid, selectedTeachPlan.getId());
            Integer currentNodeChildCount = teachPlanMapper.selectCount(queryWrapper);
            if (currentNodeChildCount > 0) {
                throw new XczxException("课程计划信息还有子级信息，无法操作");
            }
            deleteCourseById(planId);
        } else {
            // 删除子节点
            deleteCourseById(planId);

            // 删除关联的媒资信息
            LambdaQueryWrapper<TeachplanMedia> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TeachplanMedia::getTeachplanId, selectedTeachPlan.getId());
            teachPlanMediaService.remove(queryWrapper);
        }
    }


    @Override
    @Transactional
    public void moveCoursePlan(String moveType, String planId) {
        Teachplan currentTeachPlan = teachPlanMapper.selectById(planId);
        Teachplan targetTeachPlan = null;

        if ("moveup".equals(moveType)) {
            // 向上移动
            targetTeachPlan = teachPlanMapper.selectPrevTeachPlanById(currentTeachPlan.getOrderby(), currentTeachPlan.getParentid());
            if (targetTeachPlan == null) {
                throw new XczxException("该计划处于最顶层，无法移动");
            }
        } else if ("movedown".equals(moveType)) {
            // 向下移动
            targetTeachPlan = teachPlanMapper.selectNextTeachPlanById(currentTeachPlan.getOrderby(), currentTeachPlan.getParentid());
            if (targetTeachPlan == null) {
                throw new XczxException("该计划处于最底层，无法移动");
            }
        }

        // 更新内存数据
        int temp = currentTeachPlan.getOrderby();
        currentTeachPlan.setOrderby(targetTeachPlan.getOrderby());
        targetTeachPlan.setOrderby(temp);

        // 更新数据库数据
        int affectRows = teachPlanMapper.updateById(currentTeachPlan);
        affectRows += teachPlanMapper.updateById(targetTeachPlan);
        if (affectRows != 2) {
            throw new XczxException("移动失败，请稍后再试");
        }
    }


    private void deleteCourseById(String planId) {
        int affectRows = teachPlanMapper.deleteById(planId);
        if (affectRows <= 0) {
            throw new XczxException("课程计划信息删除失败");
        }
    }


    @Override
    @Transactional
    public void bindingCourseMedia(BindingCourseMediaDto bindingCourseMediaDto) {
        // 先删除之前的关联的媒资信息
        long teachplanId = bindingCourseMediaDto.getTeachplanId();
        teachPlanMediaService.remove(new LambdaQueryWrapper<TeachplanMedia>().eq(TeachplanMedia::getTeachplanId, teachplanId));

        // 绑定最新的媒资信息
        Teachplan teachplan = teachPlanMapper.selectById(teachplanId);
        TeachplanMedia teachplanMedia = new TeachplanMedia();
        teachplanMedia.setMediaId(bindingCourseMediaDto.getMediaId());
        teachplanMedia.setTeachplanId(teachplanId);
        teachplanMedia.setCourseId(teachplan.getCourseId());
        teachplanMedia.setMediaFilename(bindingCourseMediaDto.getFileName());
        teachPlanMediaService.save(teachplanMedia);
    }
}
