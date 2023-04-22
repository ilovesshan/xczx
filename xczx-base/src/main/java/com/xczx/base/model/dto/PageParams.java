package com.xczx.base.model.dto;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/22
 * @description:
 */

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
    private Long pageNo = 1L;

    //每页记录数默认值
    private Long pageSize =10L;
}
