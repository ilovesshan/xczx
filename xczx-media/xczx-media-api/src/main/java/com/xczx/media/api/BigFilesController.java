package com.xczx.media.api;

import com.xczx.base.model.vo.RestResponse;
import com.xczx.media.model.dto.FileUploadDto;
import com.xczx.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@Api(value = "大文件上传接口", tags = "大文件上传接口")
@RestController
public class BigFilesController {

    @Resource
    private MediaFileService mediaFileService;

    @ApiOperation(value = "文件上传前检查文件")
    @PostMapping("/upload/checkfile")
    public RestResponse<Boolean> checkFile(@RequestParam("fileMd5") String fileMd5) throws Exception {
        return mediaFileService.checkFile(fileMd5);
    }


    @ApiOperation(value = "分块文件上传前的检测")
    @PostMapping("/upload/checkchunk")
    public RestResponse<Boolean> checkChunk(@RequestParam("fileMd5") String fileMd5,
                                            @RequestParam("chunk") int chunk) throws Exception {
        return mediaFileService.checkChunk(fileMd5, chunk);
    }


    @ApiOperation(value = "上传分块文件")
    @PostMapping("/upload/uploadchunk")
    public RestResponse uploadChunk(@RequestParam("file") MultipartFile file,
                                    @RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("chunk") int chunk) throws Exception {

        return mediaFileService.uploadChunk(file, fileMd5, chunk);
    }


    @ApiOperation(value = "合并文件")
    @PostMapping("/upload/mergechunks")
    public RestResponse mergeChunks(@RequestParam("fileMd5") String fileMd5,
                                    @RequestParam("fileName") String fileName,
                                    @RequestParam("chunkTotal") int chunkTotal) throws Exception {
        Long companyId = 1001101L;
        FileUploadDto fileUploadDto = new FileUploadDto();
        fileUploadDto.setFileType("001002");
        fileUploadDto.setTags("课程视频");
        fileUploadDto.setRemark("");
        fileUploadDto.setFilename(fileName);

        return mediaFileService.mergeChunks(companyId, fileUploadDto, fileMd5, chunkTotal);

    }

}