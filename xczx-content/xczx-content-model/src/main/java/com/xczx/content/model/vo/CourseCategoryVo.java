package com.xczx.content.model.vo;

import com.xczx.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/23
 * @description:
 */

@Data
public class CourseCategoryVo extends CourseCategory implements Serializable {
    private static final long serialVersionUID = -3376439845834201527L;

    private List<CourseCategoryVo> childrenTreeNodes;
}
