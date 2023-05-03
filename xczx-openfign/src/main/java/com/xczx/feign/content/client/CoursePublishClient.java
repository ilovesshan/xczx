package com.xczx.feign.content.client;

import com.xczx.feign.content.fallbackfactory.CoursePublishClientFallbackFactory;
import com.xczx.feign.content.model.CoursePublish;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/5/2
 * @description:
 */

@FeignClient(name = "content-api", fallbackFactory = CoursePublishClientFallbackFactory.class)
public interface CoursePublishClient {

    @ResponseBody
    @GetMapping("/content/r/coursepublish/{courseId}")
    CoursePublish getCoursePublish(@PathVariable("courseId") Long courseId);
}
