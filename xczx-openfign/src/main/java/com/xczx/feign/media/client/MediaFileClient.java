package com.xczx.feign.media.client;

import com.xczx.feign.config.MultipartSupportConfig;
import com.xczx.feign.media.fallbackFactory.MediaFileClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/5/1
 * @description:
 */

@FeignClient(value = "media-api", configuration = MultipartSupportConfig.class , fallbackFactory = MediaFileClientFallbackFactory.class)
public interface MediaFileClient {
    @PostMapping(value = "/media/upload/coursefile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String upload(@RequestPart("filedata") MultipartFile multipartFile, @RequestParam(value = "objectName", required = false) String objectName);
}
