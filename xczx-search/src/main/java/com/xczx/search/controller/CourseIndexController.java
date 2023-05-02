package com.xczx.search.controller;

import com.xczx.base.exception.XczxException;
import com.xczx.search.model.po.CourseIndex;
import com.xczx.search.service.IndexService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Mr.M
 * @version 1.0
 * @description 课程索引接口
 * @date 2022/9/24 22:31
 */
@Api(value = "课程信息索引接口", tags = "课程信息索引接口")
@RestController
@RequestMapping("/index")
public class CourseIndexController {

    @Value("${elasticsearch.course.index}")
    private String courseIndexStore;

    @Resource
    private IndexService indexService;

    @ApiOperation("添加课程索引")
    @PostMapping("course")
    public Boolean add(@RequestBody CourseIndex courseIndex) {

        Long id = courseIndex.getId();
        if (id == null) {
            throw new XczxException("课程id为空");
        }
        Boolean result = indexService.addCourseIndex(courseIndexStore, String.valueOf(id), courseIndex);
        if (!result) {
            throw new XczxException("添加课程索引失败");
        }
        return result;

    }
}
