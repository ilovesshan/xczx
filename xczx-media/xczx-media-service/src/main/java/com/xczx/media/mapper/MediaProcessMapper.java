package com.xczx.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xczx.media.model.po.MediaProcess;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface MediaProcessMapper extends BaseMapper<MediaProcess> {

    List<MediaProcess> selectMediaProcess(@Param("processIndex") int processIndex, @Param("processCount") int processCount, @Param("taskCount") int taskCount);

    int startMediaProcess(@Param("id") long id);
}
