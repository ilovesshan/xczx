package com.xczx.content.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xczx.content.model.po.CoursePublishPre;
import com.xczx.content.model.vo.CoursePreviewVo;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/30
 * @description:
 */
public interface CoursePublishPreService extends IService<CoursePublishPre> {

    CoursePreviewVo getCoursePreviewInfo(String courseId);

    void commitAudit(long companyId, Long courseId);

    void coursePublish(Long companyId, Long courseId);
}
