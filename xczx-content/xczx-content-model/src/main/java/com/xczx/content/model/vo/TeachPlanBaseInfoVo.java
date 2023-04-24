package com.xczx.content.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/24
 * @description:
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class TeachPlanBaseInfoVo extends TeachPlanVo implements Serializable {
    private static final long serialVersionUID = 3001370400553760332L;

    private TeachPlanMediaVo teachplanMedia;
    private List<TeachPlanBaseInfoVo> teachPlanTreeNodes;

}
