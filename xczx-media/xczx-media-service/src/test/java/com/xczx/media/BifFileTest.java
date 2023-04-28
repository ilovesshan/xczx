package com.xczx.media;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/4/26
 * @description:
 */

public class BifFileTest {

    @Test
    void testChunk() throws IOException {
        File sourceFile = new File("C:\\Users\\26659\\Desktop\\test\\a.zip");
        File chunkFile = new File("C:\\Users\\26659\\Desktop\\test\\chunk");
        // 分块大小 5M
        int chunkSize = 1024 * 1024 * 6;
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
