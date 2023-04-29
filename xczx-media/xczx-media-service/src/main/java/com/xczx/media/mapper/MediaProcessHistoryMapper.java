package com.xczx.media.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xczx.media.model.po.MediaProcessHistory;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface MediaProcessHistoryMapper extends BaseMapper<MediaProcessHistory> {

    int insertById(@Param("media") MediaProcessHistory mediaProcessHistory);

}
