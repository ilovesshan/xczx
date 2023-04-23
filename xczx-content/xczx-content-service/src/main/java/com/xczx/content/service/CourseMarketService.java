package com.xczx.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xczx.content.model.po.CourseMarket;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/23
 * @description:
 */

public interface CourseMarketService extends IService<CourseMarket> {

    int saveCourseMarket(CourseMarket courseMarket);
}
