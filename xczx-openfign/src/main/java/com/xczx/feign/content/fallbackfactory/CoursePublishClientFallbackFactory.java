package com.xczx.feign.content.fallbackfactory;

import com.xczx.feign.content.client.CoursePublishClient;
import com.xczx.feign.content.model.CoursePublish;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/5/2
 * @description:
 */
@Slf4j
@Component
public class CoursePublishClientFallbackFactory implements FallbackFactory<CoursePublishClient> {
    @Override
    public CoursePublishClient create(Throwable throwable) {
        return new CoursePublishClient() {
            @Override
            public CoursePublish getCoursePublish(Long courseId) {
                log.debug("调用内容管理服务，获取课程发布信息熔断异常:{}", throwable.getMessage());
                return null;
            }
        };
    }
}
