package com.xinghaol.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author xinghaol
 * @version 1.0
 * @date 2020/11/2 10:44 上午
 * @description nio中两个channel之间的操作
 */
public class NioTransferCopy implements FileCopyRunner {
    @Override
    public void copyFile(File source, File target) {
        FileChannel fIn = null;
        FileChannel fOut = null;

        try {
            fIn = new FileInputStream(source).getChannel();
            fOut = new FileInputStream(target).getChannel();

            // 直接在两个通道之间进行操作。transferTo不会保证把所有的字节拷贝过去。可以通过返回值，看每次拷贝了多少
            long transferTo = 0L;
            long size = fIn.size();
            while (transferTo != size) {
                transferTo += fIn.transferTo(0, size, fOut);
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
        return "NioTransferCopy";
    }
}
