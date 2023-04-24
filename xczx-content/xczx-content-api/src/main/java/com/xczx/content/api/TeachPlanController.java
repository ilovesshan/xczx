package com.xczx.content.api;

import com.xczx.content.model.vo.TeachPlanBaseInfoVo;
import com.xczx.content.service.TeachPlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/22
 * @description:
 */

@Api(value = "课程计划模块接口", tags = "课程计划模块接口")
@RestController
public class TeachPlanController {

    @Resource
    private TeachPlanService teachPlanService;

    @ApiOperation("查询课程计划树")
    @GetMapping("/teachplan/{courseId}/tree-nodes")
    public List<TeachPlanBaseInfoVo> selectCoursePlanTree(@PathVariable("courseId") String courseId) {
        return  teachPlanService.selectCoursePlanTree(courseId);
    }
}
