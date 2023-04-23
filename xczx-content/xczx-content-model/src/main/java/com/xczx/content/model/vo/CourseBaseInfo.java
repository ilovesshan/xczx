package com.xczx.content.model.vo;

import com.xczx.content.model.po.CourseBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class CourseBaseInfo extends CourseBase implements Serializable {
    private static final long serialVersionUID = 3383637504399029812L;

    /**
     * 大分类中文描述
     */
    private String mtName;

    /**
     * 小分类中文描述
     */
    private String stName;

    /**
     * 主键，课程id
     */
    private Long id;

    /**
     * 收费规则，对应数据字典
     */
    private String charge;

    /**
     * 现价
     */
    private Float price;

    /**
     * 原价
     */
    private Float originalPrice;

    /**
     * 咨询qq
     */
    private String qq;

    /**
     * 微信
     */
    private String wechat;

    /**
     * 电话
     */
    private String phone;

    /**
     * 有效期天数
     */
    private Integer validDays;

}
