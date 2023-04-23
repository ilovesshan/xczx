package com.xczx.base.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/22
 * @description:
 */

@Data
@ToString()
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = -1083828076042745069L;

    //当前页码
    private long page;

    //每页记录数
    private long pageSize;

    //总记录数
    private long counts;

    // 数据列表
    private List<T> items;
}
