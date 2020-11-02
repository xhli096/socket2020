package com.xinghaol.file;

import java.io.*;

/**
 * @author xinghaol
 * @version 1.0
 * @date 2020/11/1 12:27 上午
 * @description 传统方式拷贝，带有缓冲区
 */
public class BufferedStreamCopy implements FileCopyRunner {
    @Override
    public void copyFile(File source, File target) {
        InputStream fIn = null;
        OutputStream fOut = null;

        try {
            fIn = new BufferedInputStream(new FileInputStream(source));
            fOut = new BufferedOutputStream(new FileOutputStream(target));

            // 每次读取缓冲区大小的字节
            byte[] buffer = new byte[1024];

            int result;
            while ((result = fIn.read(buffer)) != -1) {
                // 写result个字节数
                fOut.write(buffer, 0, result);
                fOut.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fIn) {
                try {
                    fIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != fOut) {
                try {
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String toString() {
        return "BufferedStreamCopy";
    }
}
