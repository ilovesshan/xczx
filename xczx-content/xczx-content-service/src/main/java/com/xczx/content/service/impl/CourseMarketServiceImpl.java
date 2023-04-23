package com.xczx.content.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xczx.content.mapper.CourseMarketMapper;
import com.xczx.content.model.po.CourseMarket;
import com.xczx.content.service.CourseMarketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/23
 * @description:
 */

@Slf4j
@Service
public class CourseMarketServiceImpl extends ServiceImpl<CourseMarketMapper, CourseMarket> implements CourseMarketService {

    @Resource
    private CourseMarketMapper courseMarketMapper;

    @Override
    public int saveCourseMarket(CourseMarket courseMarket) {
        return courseMarketMapper.insert(courseMarket);
    }

}
