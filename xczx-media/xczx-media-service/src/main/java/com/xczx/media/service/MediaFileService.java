package com.xczx.media.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.xczx.base.model.dto.PageParams;
import com.xczx.base.model.vo.PageResult;
import com.xczx.base.model.vo.RestResponse;
import com.xczx.media.model.dto.FileUploadDto;
import com.xczx.media.model.dto.QueryMediaParamsDto;
import com.xczx.media.model.po.MediaFiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理业务类
 * @date 2022/9/10 8:55
 */
public interface MediaFileService extends IService<MediaFiles> {

    /**
     * @param pageParams          分页参数
     * @param queryMediaParamsDto 查询条件
     * @return com.xuecheng.base.model.PageResult<com.xuecheng.media.model.po.MediaFiles>
     * @description 媒资文件查询方法
     * @author Mr.M
     * @date 2022/9/10 8:57
     */
    PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto);

    MediaFiles upload(Long companyId, String localFilePath, FileUploadDto fileUploadDto, String objectName);

    RestResponse<Boolean> checkFile(String fileMd5);

    RestResponse<Boolean> checkChunk(String fileMd5, int chunk);

    RestResponse uploadChunk(MultipartFile file, String fileMd5, int chunk);

    RestResponse mergeChunks(Long companyId, FileUploadDto fileName, String fileMd5, int chunkTotal);

    MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, FileUploadDto fileuploadDto, String bucket, String objectName);

    void saveTaskToMediaProcess(MediaFiles mediaFiles);

    File downloadFileFromMinIO(String bucket, String objectName);

    boolean uploadFileToMinio(String buket, String targetFileName, String localFileName, String contentType);
}
