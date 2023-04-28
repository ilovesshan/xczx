package com.xczx.media;

import io.minio.ComposeObjectArgs;
import io.minio.ComposeSource;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/26
 * @description:
 */

public class BifFileWithMinioTest {
    private MinioClient minioClient = null;

    @BeforeEach
    public void before() {
        minioClient = MinioClient.builder()
                .endpoint("http://192.168.1.131:9000")
                .credentials("minioadmin", "minioadmin")
                .build();
    }

    @Test
    void testChunkUpload() throws Exception {
        File chunkFile = new File("C:\\Users\\26659\\Desktop\\test\\chunk");
        File[] listFiles = chunkFile.listFiles();
        if (listFiles != null) {
            List<File> files = Arrays.asList(listFiles);
            Collections.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
                }
            });
            for (int i = 0; i < files.size(); i++) {
                UploadObjectArgs uploadObjectArgs = UploadObjectArgs.builder()
                        .bucket("test")
                        .object("home/chunk/" + i)
                        .filename(files.get(i).getAbsolutePath())
                        .contentType(MediaType.IMAGE_PNG_VALUE)
                        .build();
                minioClient.uploadObject(uploadObjectArgs);
                System.out.println("分片：" + files.get(i).getAbsolutePath() + "上传成功");
            }
        }
    }

    @Test
    void testChunkMerge() throws Exception {
        List<ComposeSource> composeSourceList = new ArrayList<>();
        for (int i = 0; i < 19; i++) {
            ComposeSource composeSource = ComposeSource.builder()
                    .bucket("test")
                    .object("home/chunk/" + i)
                    .build();
            composeSourceList.add(composeSource);
        }

        ComposeObjectArgs composeObjectArgs = ComposeObjectArgs.builder()
                .bucket("test")
                .sources(composeSourceList)
                .object("home/merge.zip")
                .build();
        minioClient.composeObject(composeObjectArgs);
        System.out.println("合并成功...");

    }


    @Test
    void testChunk() throws IOException {
        File sourceFile = new File("C:\\Users\\26659\\Desktop\\test\\a.zip");
        File chunkFile = new File("C:\\Users\\26659\\Desktop\\test\\chunk");
        // 分块大小 5M
        int chunkSize = 1024 * 1024 * 5;
        // 分块个数
        int chunkNum = (int) Math.ceil(sourceFile.length() * 1.0 / chunkSize);

        RandomAccessFile rafR = new RandomAccessFile(sourceFile, "r");
        // 向每个块写入数据
        for (int i = 0; i < chunkNum; i++) {
            // 创建一个块
            RandomAccessFile rafW = new RandomAccessFile(chunkFile + File.separator + i, "rw");
            byte[] bytes = new byte[1024];
            int len = -1;
            while ((len = rafR.read(bytes)) != -1) {
                rafW.write(bytes, 0, len);
                // 当前分块文件已经写满了
                if (rafW.length() == chunkSize) {
                    break;
                }
            }
            rafW.close();
        }
        rafR.close();
        System.out.println("文件分块成功...");
    }


    @Test
    void testMerge() throws IOException {
        File sourceFile = new File("C:\\Users\\26659\\Desktop\\test\\a.zip");
        File mergeFile = new File("C:\\Users\\26659\\Desktop\\test\\a_merge.zip");
        File chunkFile = new File("C:\\Users\\26659\\Desktop\\test\\chunk");

        List<File> chunkFileList = Arrays.asList(chunkFile.listFiles());

        Collections.sort(chunkFileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });

        RandomAccessFile rafW = new RandomAccessFile(mergeFile, "rw");
        for (File file : chunkFileList) {
            RandomAccessFile rafR = new RandomAccessFile(file, "r");
            byte[] bytes = new byte[1024];
            int len = -1;
            while ((len = rafR.read(bytes)) != -1) {
                rafW.write(bytes, 0, len);
            }
            rafR.close();
        }
        rafW.close();

        // 校验原始文件和合并后的文件MD5值
        String sourceFileMd5 = DigestUtils.md5Hex(new FileInputStream(sourceFile));
        String mergeFileMd5 = DigestUtils.md5Hex(new FileInputStream(mergeFile));
        if (sourceFileMd5.equals(mergeFileMd5)) {
            System.out.println("文件合并成功...");
        } else {
            System.out.println("文件合并失败...");
        }
    }
}
