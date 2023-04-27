package com.xczx.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.j256.simplemagic.ContentInfo;
import com.j256.simplemagic.ContentInfoUtil;
import com.xczx.base.exception.XczxException;
import com.xczx.base.model.dto.PageParams;
import com.xczx.base.model.vo.PageResult;
import com.xczx.media.mapper.MediaFilesMapper;
import com.xczx.media.model.dto.FileUploadDto;
import com.xczx.media.model.dto.QueryMediaParamsDto;
import com.xczx.media.model.po.MediaFiles;
import com.xczx.media.service.MediaFileService;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author Mr.M
 * @version 1.0
 * @description TODO
 * @date 2022/9/10 8:58
 */

@Slf4j
@Service
public class MediaFileServiceImpl implements MediaFileService {

    @Resource
    private MediaFilesMapper mediaFilesMapper;

    @Resource
    private MinioClient minioClient;


    @Value("${minio.bucket.files}")
    private String files;

    @Value("${minio.bucket.videofiles}")
    private String videofiles;


    @Override
    public PageResult<MediaFiles> queryMediaFiels(Long companyId, PageParams pageParams, QueryMediaParamsDto queryMediaParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<MediaFiles> queryWrapper = new LambdaQueryWrapper<>();

        //分页对象
        Page<MediaFiles> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<MediaFiles> pageResult = mediaFilesMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<MediaFiles> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        return new PageResult<>(pageParams.getPageNo(), pageParams.getPageSize(), total, list);
    }


    @Override
    public MediaFiles upload(Long companyId, String localFilePath, FileUploadDto fileUploadDto) {
        // 文件扩展名
        String extName = fileUploadDto.getFilename().substring(fileUploadDto.getFilename().lastIndexOf("."));
        // 文件MD5值
        String fileMd5 = getFileMd5(new File(localFilePath));
        // 最终文件路径+文件名称
        String finalFilepath = getDefaultFolderPath() + fileMd5 + extName;
        // 将文件上传到MinIO服务器上
        boolean uploadFileToMinioSuccess = uploadFileToMinio(files, finalFilepath, localFilePath, getMimeType(extName));
        if (!uploadFileToMinioSuccess) {
            throw new XczxException("文件上传时发生异常");
        }

        // 将文件信息写入到数据库
        return addMediaFilesToDb(companyId, fileMd5, fileUploadDto, files, finalFilepath);
    }


    /**
     * 上传文件到Minio服务器
     *
     * @param buket          桶
     * @param targetFileName Minio服务器中保存的文件名称(包含路径)
     * @param localFileName  本地文件名称(包含路径)
     * @param contentType    文件类型
     * @return 是否上传成功
     */
    private boolean uploadFileToMinio(String buket, String targetFileName, String localFileName, String contentType) {
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder().bucket(buket).object(targetFileName).filename(localFileName).contentType(contentType).build();
            minioClient.uploadObject(uploadObjectArgs);
            log.debug("[Minio文件系统]文件上传成功-buket：{} - targetFileName:{} - contentType:{}", buket, targetFileName, contentType);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("[Minio文件系统]文件上传失败-buket：{} - targetFileName:{} - contentType:{} - error：{}", buket, targetFileName, contentType, e.getMessage());
        }
        return false;
    }


    /**
     * 根据文件扩展名获取MimeType类型
     *
     * @param extension 文件扩展名
     * @return MimeType类型
     */
    private String getMimeType(String extension) {
        if (extension == null) extension = "";
        //根据扩展名取出mimeType
        ContentInfo extensionMatch = ContentInfoUtil.findExtensionMatch(extension);
        //通用mimeType，字节流
        String mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        if (extensionMatch != null) {
            mimeType = extensionMatch.getMimeType();
        }
        return mimeType;
    }

    /**
     * 获取文件默认存储目录路径 年/月/日
     *
     * @return 2023/02/12/
     */
    private String getDefaultFolderPath() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date()).replace("-", "/") + "/";
    }


    /**
     * 获取文件的MD5值
     *
     * @param file 文件
     * @return MD5
     */
    private String getFileMd5(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return DigestUtils.md5Hex(fileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 保存文件到数据库
     *
     * @param companyId     机构ID
     * @param fileMd5       文件MD5值
     * @param fileuploadDto 文件相关对象
     * @param bucket        桶
     * @param objectName    Minio服务器中文件地址
     * @return MediaFiles
     */
    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, FileUploadDto fileuploadDto, String bucket, String objectName) {
        //从数据库查询文件
        MediaFiles mediaFiles = mediaFilesMapper.selectById(fileMd5);
        if (mediaFiles == null) {
            mediaFiles = new MediaFiles();
            //拷贝基本信息
            BeanUtils.copyProperties(fileuploadDto, mediaFiles);
            mediaFiles.setId(fileMd5);
            mediaFiles.setFileId(fileMd5);
            mediaFiles.setCompanyId(companyId);
            mediaFiles.setUrl("/" + bucket + "/" + objectName);
            mediaFiles.setBucket(bucket);
            mediaFiles.setFilePath(objectName);
            mediaFiles.setCreateDate(LocalDateTime.now());
            mediaFiles.setAuditStatus("002003");
            mediaFiles.setStatus("1");
            //保存文件信息到文件表
            int insert = mediaFilesMapper.insert(mediaFiles);
            if (insert <= 0) {
                log.error("保存文件信息到数据库失败,{}", mediaFiles);
                throw new XczxException("保存文件信息失败");
            }
            log.debug("保存文件信息到数据库成功,{}", mediaFiles);
        }
        return mediaFiles;
    }
}
