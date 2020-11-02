package com.xinghaol.file;

import java.io.File;

/**
 * @author xinghaol
 * @version 1.0
 * @date 2020/11/2 11:24 上午
 * @description description
 */
public class Demo {
    private static final int ROUNDS = 5;

    private static void benchMark(FileCopyRunner fileCopyRunner, File source, File target) {
        long elapsed = 0L;

        for (int i = 0; i < ROUNDS; i++) {
            long startTime = System.currentTimeMillis();
            fileCopyRunner.copyFile(source, target);
            elapsed += System.currentTimeMillis() - startTime;
            target.delete();
        }
        System.out.println(fileCopyRunner + " : " + elapsed / ROUNDS);
    }

    public static void main(String[] args) {
        // 测试即可
    }
}
