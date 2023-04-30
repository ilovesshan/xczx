package com.xczx.content.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CoursePreviewVo implements Serializable {
    private static final long serialVersionUID = 3383637504399029812L;

    //课程基本信息,课程营销信息
    CourseBaseInfo courseBase;

    //课程计划信息
    List<TeachPlanBaseInfoVo> teachplans;

    //师资信息暂时不加...

}
