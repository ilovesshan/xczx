package com.xczx.media.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xczx.media.mapper.MediaProcessHistoryMapper;
import com.xczx.media.model.po.MediaProcessHistory;
import com.xczx.media.service.MediaProcessHistoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/29
 * @description:
 */

@Service
public class MediaProcessHistoryServiceImpl extends ServiceImpl<MediaProcessHistoryMapper, MediaProcessHistory> implements MediaProcessHistoryService {

    @Resource
    private MediaProcessHistoryMapper mediaProcessHistoryMapper;

    @Override
    public void insertById(MediaProcessHistory mediaProcessHistory) {
        mediaProcessHistoryMapper.insertById(mediaProcessHistory);
    }
}
