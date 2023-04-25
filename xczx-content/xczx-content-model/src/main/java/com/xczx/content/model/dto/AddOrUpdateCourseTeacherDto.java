package com.xczx.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/23
 * @description:
 */
@Data
@ApiModel(value = "AddOrUpdateCourseTeacherDto", description = "新增课程师资信息")
public class AddOrUpdateCourseTeacherDto {

    @ApiModelProperty(value = "教师ID")
    private Long id;

    @ApiModelProperty(value = "课程ID")
    private Long courseId;

    @NotEmpty(message = "教师名称不能为空")
    @ApiModelProperty(value = "教师名称", required = true)
    private String teacherName;

    @NotEmpty(message = "教师职位不能为空")
    @ApiModelProperty(value = "教师职位", required = true)
    private String position;

    @NotEmpty(message = "教师简介不能为空")
    @Size(message = "教师简介内容过少", min = 5)
    @ApiModelProperty(value = "适用人群", required = true)
    private String introduction;

    @ApiModelProperty(value = "教师图片")
    private String photograph;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createDate;
}
