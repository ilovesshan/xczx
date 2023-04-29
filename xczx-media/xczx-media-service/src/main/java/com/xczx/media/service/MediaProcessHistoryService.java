package com.xczx.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xczx.media.model.po.MediaProcessHistory;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/29
 * @description:
 */
public interface MediaProcessHistoryService extends IService<MediaProcessHistory> {
    void insertById(MediaProcessHistory mediaProcessHistory);

}
