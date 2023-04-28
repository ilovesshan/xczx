package com.xczx.media.service;


import com.xczx.base.model.dto.PageParams;
import com.xczx.base.model.vo.PageResult;
import com.xczx.base.model.vo.RestResponse;
import com.xczx.media.model.dto.FileUploadDto;
import com.xczx.media.model.dto.QueryMediaParamsDto;
import com.xczx.media.model.po.MediaFiles;
import org.springframework.web.multipart.MultipartFile;

/**
 * @description 媒资文件管理业务类
 * @author Mr.M
 * @date 2022/9/10 8:55
 * @version 1.0
 */
public interface MediaFileService {

 /**
  * @description 媒资文件查询方法
  * @param pageParams 分页参数
  * @param queryMediaParamsDto 查询条件
  * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
  * @author Mr.M
  * @date 2022/9/10 8:57
 */
  PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    MediaFiles upload(Long companyId, String localFilePath, FileUploadDto fileUploadDto);

    RestResponse<Boolean> checkFile(String fileMd5);

    RestResponse<Boolean> checkChunk(String fileMd5, int chunk);

    RestResponse uploadChunk(MultipartFile file, String fileMd5, int chunk);

    RestResponse mergeChunks(Long companyId, FileUploadDto fileName, String fileMd5, int chunkTotal);
}
