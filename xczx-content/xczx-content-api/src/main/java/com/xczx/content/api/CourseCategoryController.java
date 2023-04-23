package com.xczx.content.api;

import com.xczx.content.model.vo.CourseCategoryVo;
import com.xczx.content.service.CourseCategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
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

@Api(value = "课程模块接口", tags = "课程模块接口")
@RestController
public class CourseCategoryController {

    @Resource
    private CourseCategoryService courseCategoryService;

    @ApiOperation("课程分类查询接口")
    @GetMapping("/course-category/tree-nodes")
    public List<CourseCategoryVo> queryCourseCategory() {
        return courseCategoryService.queryCourseCategory("1");
    }
}
