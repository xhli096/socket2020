package com.xinghaol.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author xinghaol
 * @version 1.0
 * @date 2020/11/2 10:41 上午
 * @description Nio 下的 使用通道的方式，带有buffer
 */
public class NioBufferCopy implements FileCopyRunner {
    @Override
    public void copyFile(File source, File target) {
        FileChannel fIn = null;
        FileChannel fOut = null;

        try {
            fIn = new FileInputStream(source).getChannel();
            fOut = new FileInputStream(target).getChannel();

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            // 最多会读取1024个字节
            while (fIn.read(buffer) != -1) {
                // 由写模式，转换到读模式。保证接下来从buffer中读取数据时，读取到之前写入buffer的数据
                buffer.flip();
                // 下面的循环保证，buffer中所有可读的数据都可以写进到fout里面去
                while (buffer.hasRemaining()) {
                    fOut.write(buffer);
                }
            }
            // 将读模式 -> 写模式
            buffer.clear();
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
        return "NioBufferCopy";
    }
}
