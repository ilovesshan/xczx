package com.xczx.media.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xczx.media.mapper.MediaProcessMapper;
import com.xczx.media.model.po.MediaFiles;
import com.xczx.media.model.po.MediaProcess;
import com.xczx.media.model.po.MediaProcessHistory;
import com.xczx.media.service.MediaFileService;
import com.xczx.media.service.MediaProcessHistoryService;
import com.xczx.media.service.MediaProcessService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/29
 * @description:
 */

@Service
public class MediaProcessServiceImpl extends ServiceImpl<MediaProcessMapper, MediaProcess> implements MediaProcessService {

    @Resource
    private MediaProcessMapper mediaProcessMapper;

    @Resource
    private MediaFileService mediaFileService;

    @Resource
    private MediaProcessHistoryService mediaProcessHistoryService;

    @Override
    public List<MediaProcess> selectMediaProcess(int processIndex, int processCount, int taskCount) {
        return mediaProcessMapper.selectMediaProcess(processIndex, processCount, taskCount);
    }

    @Override
    public void updateMediaProcess(long processId, String fileId, long status, String url, String errorMessage) {
        MediaProcess mediaProcess = mediaProcessMapper.selectById(processId);
        if (mediaProcess == null) {
            return;
        }

        // 任务处理失败
        if (3 == status) {
            // 1、更新状态
            mediaProcess.setStatus("3");
            // 2、更新失败次数
            mediaProcess.setFailCount(mediaProcess.getFailCount() + 1);
            // 3、更新errorMessage
            mediaProcess.setErrormsg(errorMessage);
            mediaProcessMapper.updateById(mediaProcess);
            return;
        }

        // 任务处理成功

        // 1、更新media_files表中文的URL(文件名称已经由.avi变成.mp4了)
        MediaFiles mediaFiles = mediaFileService.getById(fileId);
        mediaFiles.setUrl(url);
        mediaFileService.updateById(mediaFiles);


        // 2.1、更新状态
        mediaProcess.setStatus("1");
        // 2.2、更新URL
        mediaProcess.setUrl(url);

        // 3、删除当前这条数据(media_files_process表中)
        mediaProcessMapper.deleteById(processId);

        //TODO: BUG插入数据不生效(暂时未解决)~~~~~~~~~~~~~
        // 4、向media_files_history插入当前条数据
        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
        BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
        mediaProcessHistory.setFinishDate(LocalDateTime.now());
        mediaProcessHistory.setFailCount(0);
        mediaProcessHistoryService.save(mediaProcessHistory);

//        //要更新的任务
//        MediaProcess mediaProcess = mediaProcessMapper.selectById(processId);
//        if (mediaProcess == null) {
//            return;
//        }
//        //如果任务执行失败
//        if (3 == status) {
//            //更新MediaProcess表的状态
//            mediaProcess.setStatus("3");
//            mediaProcess.setFailCount(mediaProcess.getFailCount() + 1);
//            //失败次数加1
//            mediaProcess.setErrormsg(errorMessage);
//            mediaProcessMapper.updateById(mediaProcess);
//            //更高效的更新方式
////            mediaProcessMapper.update()
//            //todo:将上边的更新方式更改为效的更新方式
//            return;
//
//        }
//
//
//        //======如果任务执行成功======
//        //文件表记录
//        MediaFiles mediaFiles = mediaFileService.getById(fileId);
//        //更新media_file表中的url
//        mediaFiles.setUrl(url);
//        mediaFileService.updateById(mediaFiles);
//
//        //更新MediaProcess表的状态
//        mediaProcess.setStatus("2");
//        mediaProcess.setFinishDate(LocalDateTime.now());
//        mediaProcess.setUrl(url);
//        mediaProcessMapper.updateById(mediaProcess);
//
//        //将MediaProcess表记录插入到MediaProcessHistory表
//        MediaProcessHistory mediaProcessHistory = new MediaProcessHistory();
//        BeanUtils.copyProperties(mediaProcess, mediaProcessHistory);
//        mediaProcessHistoryService.save(mediaProcessHistory);
//
//        //从MediaProcess删除当前任务
//        mediaProcessMapper.deleteById(processId);
    }


    @Override
    public boolean startMediaProcess(long processId) {
        return mediaProcessMapper.startMediaProcess(processId) > 0;
    }
}
