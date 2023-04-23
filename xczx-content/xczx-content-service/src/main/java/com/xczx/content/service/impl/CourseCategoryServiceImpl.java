package com.xczx.content.service.impl;

import com.xczx.content.mapper.CourseCategoryMapper;
import com.xczx.content.model.vo.CourseCategoryVo;
import com.xczx.content.service.CourseCategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/23
 * @description:
 */

@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Resource
    private CourseCategoryMapper courseCategoryMapper;

    @Override
    public List<CourseCategoryVo> queryCourseCategory(String pid) {
        List<CourseCategoryVo> responseCourseCategoryList = new ArrayList<>();

        // 全部的分类列表
        List<CourseCategoryVo> courseCategoryList = courseCategoryMapper.selectAll();

        // 将全部分类列表封装成MAP， 方便通过ID获取对象
        Map<String, CourseCategoryVo> courseCategoryMap = courseCategoryList.stream()
                .filter(courseCategory -> !pid.equals(courseCategory.getId()))
                .collect(Collectors.toMap(CourseCategoryVo::getId, value -> value, (rk1, rk2) -> rk1));

        courseCategoryList.stream()
                .filter(courseCategory -> !pid.equals(courseCategory.getId()))
                .forEach(courseCategory -> {
                    // 找到顶层父节点放入到responseCourseCategoryList中
                    if (pid.equals(courseCategory.getParentid())) {
                        responseCourseCategoryList.add(courseCategory);
                    }
                    // 找当前节点父节点
                    CourseCategoryVo parentNode = courseCategoryMap.get(courseCategory.getParentid());
                    if (parentNode != null) {
                        List<CourseCategoryVo> childrenTreeNodes = parentNode.getChildrenTreeNodes();
                        // 如果childrenTreeNodes为空就创建一个ArrayList, 再放入子节点
                        if (childrenTreeNodes == null) {
                            parentNode.setChildrenTreeNodes(new ArrayList<>());
                        }
                        parentNode.getChildrenTreeNodes().add(courseCategory);
                    }
                });
        return responseCourseCategoryList;
    }
}
