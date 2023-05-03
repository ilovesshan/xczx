
package com.xczx.content;

import com.xczx.feign.config.MultipartSupportConfig;
import com.xczx.feign.media.client.MediaFileClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/23
 * @description:
 */

@SpringBootTest
public class CourseStaticPageUploadTest {

    @Resource
    private MediaFileClient mediaFileClient;

    @Test
    public void uploadTest() {
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(new File("C:\\Users\\26659\\Desktop\\test\\test.html"));
        String uploadResult = mediaFileClient.upload(multipartFile, "course/139.html");
        if (uploadResult == null) {
            System.out.println("课程静态页面文件上传到MinIO失败");
        } else {
            System.out.println("课程静态页面文件上传到MinIO成功");
        }
    }
}
