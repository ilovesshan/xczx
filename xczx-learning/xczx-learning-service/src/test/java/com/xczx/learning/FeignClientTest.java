package com.xczx.learning;

import com.xczx.feign.content.client.CoursePublishClient;
import com.xczx.feign.content.model.CoursePublish;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2023/2/22 20:14
 */
@SpringBootTest
public class FeignClientTest {

    @Resource
    private CoursePublishClient coursePublishClient;


    @Test
    public void testContentServiceClient() {
        CoursePublish coursepublish = coursePublishClient.getCoursePublish(140L);
        System.out.println("coursepublish = " + coursepublish);
    }
}