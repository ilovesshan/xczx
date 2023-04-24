package com.xczx.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xczx.content.mapper.TeachPlanMapper;
import com.xczx.content.model.po.Teachplan;
import com.xczx.content.model.vo.TeachPlanBaseInfoVo;
import com.xczx.content.model.vo.TeachPlanVo;
import com.xczx.content.service.TeachPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
}
