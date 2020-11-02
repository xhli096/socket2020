package com.xinghaol.file;

import java.io.*;

/**
 * @author xinghaol
 * @version 1.0
 * @date 2020/11/1 12:20 上午
 * @description 使用最传统的方法，没有缓冲来拷贝文件
 */
public class NoBufferStreamCopy implements FileCopyRunner {
    @Override
    public void copyFile(File source, File target) {
        InputStream fIn = null;
        OutputStream fOut = null;

        try {
            fIn = new FileInputStream(source);
            fOut = new FileOutputStream(target);

            int readResult;
            while ((readResult = fIn.read()) != -1) {
                fOut.write(readResult);
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
        return "NoBufferStreamCopy";
    }
}
