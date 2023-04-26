package com.xczx.media;

import io.minio.*;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/26
 * @description:
 */

public class MinioTest {

    private MinioClient minioClient = null;

    @BeforeEach
    public void before() {
        minioClient = MinioClient.builder()
                .endpoint("http://192.168.1.131:9000")
                .credentials("minioadmin", "minioadmin")
                .build();
    }

    @Test
    void testUpload() {
        try {
            UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                    .bucket("test")
                    .object("home/images/wallhaven-mp3zmk.png")
                    .filename("C:\\Users\\26659\\Pictures\\uToolsWallpapers\\wallhaven-mp3zmk.png")
                    .contentType(MediaType.IMAGE_PNG_VALUE)
                    .build();
            minioClient.uploadObject(uploadObjectArgs);
            System.out.println("上传成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("上传失败");
        }
    }


    @Test
    void testDownload() {
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket("test")
                    .object("home/images/wallhaven-mp3zmk.png")
                    .build();
            FilterInputStream inputStream = minioClient.getObject(getObjectArgs);
            OutputStream outputStream = new FileOutputStream("C:\\Users\\26659\\Desktop\\c.png");
            IOUtils.copy(inputStream, outputStream);
            System.out.println("下载成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("下载失败");
        }
    }


    @Test
    void testDelete() {
        try {
            RemoveObjectArgs uploadObjectArgs = RemoveObjectArgs.builder()
                    .bucket("test")
                    .object("home/images/wallhaven-mp3zmk.png")
                    .build();
            minioClient.removeObject(uploadObjectArgs);
            System.out.println("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("删除失败");
        }
    }
}
