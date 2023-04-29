package com.xczx.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xczx.media.model.po.MediaProcess;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/29
 * @description:
 */
public interface MediaProcessService extends IService<MediaProcess> {

    /**
     * 查询视频待处理任务列表
     *
     * @param processIndex 处理器索引
     * @param processCount 处理器数量
     * @param taskCount    任务数量
     * @return
     */
    List<MediaProcess> selectMediaProcess(int processIndex, int processCount, int taskCount);


    /**
     * 处理更新任务结果（可能成功、也有可能失败）
     *
     * @param processId    任务ID
     * @param fileId       文件ID
     * @param status       任务状态
     * @param url          更新后的URL
     * @param errorMessage 更新失败错误信息
     */
    void updateMediaProcess(long processId, String fileId, long status, String url, String errorMessage);


    /**
     * 开启任务(根据ID获取任务)
     *
     * @param processId 任务ID
     * @return 是否获取成功
     */
    boolean startMediaProcess(long processId);
}
