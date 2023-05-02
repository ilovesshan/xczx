package com.xczx.media.api;

import com.xczx.base.model.dto.PageParams;
import com.xczx.base.model.vo.PageResult;
import com.xczx.media.model.dto.FileUploadDto;
import com.xczx.media.model.dto.QueryMediaParamsDto;
import com.xczx.media.model.po.MediaFiles;
import com.xczx.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * @author Mr.M
 * @version 1.0
 * @description 媒资文件管理接口
 * @date 2022/9/6 11:29
 */
@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
public class MediaFilesController {

    @Autowired
    MediaFileService mediaFileService;

    @ApiOperation("媒资列表查询接口")
    @PostMapping("/files")
    public PageResult<MediaFiles> list(PageParams pageParams, @RequestBody QueryMediaParamsDto queryMediaParamsDto) {
        Long companyId = 1001101L;
        return mediaFileService.queryMediaFiels(companyId, pageParams, queryMediaParamsDto);
    }

    @ApiOperation("上传图片/静态文件接口")
    @PostMapping(value = "/upload/coursefile")
    public MediaFiles upload(@RequestParam("filedata") MultipartFile multipartFile, @RequestParam(value = "objectName", required = false) String objectName) throws IOException {
        Long companyId = 1001101L;
        File tempFile = File.createTempFile("minio", "temp");
        multipartFile.transferTo(tempFile);
        String localFilePath = tempFile.getAbsolutePath();

        FileUploadDto fileUploadDto = new FileUploadDto();
        fileUploadDto.setFileSize(multipartFile.getSize());
        fileUploadDto.setFilename(multipartFile.getOriginalFilename());
        fileUploadDto.setFileType("001001");


        return mediaFileService.upload(companyId, localFilePath, fileUploadDto, objectName);
    }
}
