package com.xczx.content.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/23
 * @description:
 */
@Data
@ApiModel(value = "UpdateCourseDto", description = "更新课程基本信息")
public class UpdateCourseDto extends AddCourseDto {
    @NotNull(message = "课程ID不能为空")
    @ApiModelProperty(value = "课程ID", required = true)
    private Long id;
}
