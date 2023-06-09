package com.xczx.feign.media.fallbackFactory;

import com.xczx.base.model.vo.RestResponse;
import com.xczx.feign.media.client.MediaFileClient;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/5/1
 * @description:
 */

@Slf4j
@Component
public class MediaFileClientFallbackFactory implements FallbackFactory<MediaFileClient> {
    @Override
    public MediaFileClient create(Throwable throwable) {
        return new MediaFileClient() {
            @Override
            public String upload(MultipartFile multipartFile, String objectName) {
                log.error("课程静态页面文件上传到MinIO中发送熔断异常,objectName = {},throwable = {}", objectName, throwable.getMessage(), throwable);
                return null;
            }

            @Override
            public RestResponse<String> getPlayUrlByMediaId(String mediaId) {
                log.error("获取文件预览地址失败,mediaId = {},throwable = {}", mediaId, throwable.getMessage(), throwable);
                return null;
            }
        };
    }
}
