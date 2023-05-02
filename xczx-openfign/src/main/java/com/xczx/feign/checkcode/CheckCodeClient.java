package com.xczx.feign.checkcode;

import com.xczx.feign.checkcode.fallbackfactory.CheckCodeClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/5/2
 * @description:
 */
@FeignClient(name = "checkcode", fallbackFactory = CheckCodeClientFallbackFactory.class)
public interface CheckCodeClient {

    @PostMapping(value = "/checkcode/verify")
    Boolean verify(@RequestParam("key") String key, @RequestParam("code") String code);
}
