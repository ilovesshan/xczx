package com.xczx.base.model.dto;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/22
 * @description:
 */

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString()
@NoArgsConstructor
@AllArgsConstructor
public class PageParams {
    //当前页码
    @ApiModelProperty("当前页码")
    private Long pageNo = 1L;

    //每页记录数默认值
    @ApiModelProperty("每页条数")
    private Long pageSize = 10L;
}
