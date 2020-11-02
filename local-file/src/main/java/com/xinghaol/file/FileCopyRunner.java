package com.xinghaol.file;

import java.io.File;

/**
 * @author xinghaol
 * @version 1.0
 * @date 2020/11/1 12:18 上午
 * @description description
 */
public interface FileCopyRunner {
    /**
     * 将源文件拷贝到目标文件
     *
     * @param source
     * @param target
     */
    void copyFile(File source, File target);
}
