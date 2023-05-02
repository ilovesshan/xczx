package com.xczx.content.client;

import com.xczx.content.client.fallbackFactory.SearchServiceClientFallbackFactory;
import com.xczx.content.model.po.CourseIndex;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "search",fallbackFactory = SearchServiceClientFallbackFactory.class)
public interface SearchServiceClient {

 @PostMapping("/search/index/course")
  Boolean add(@RequestBody CourseIndex courseIndex);
}