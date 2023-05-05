package com.xczx.learning.service;

import com.xczx.base.model.vo.RestResponse;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/5/5
 * @description:
 */
public interface LearningService {
    RestResponse<String> getVideoInfo(String userId, Long courseId, Long teachplanId, String mediaId);
}
