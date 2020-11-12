package com.xinghaol.chat;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author xinghaol
 * @version 1.0
 * @date 2020/11/4 10:35 下午
 * @description description
 */
public class AioChatClient {
    private static final String LOCALHOST = "localhost";
    private static final int DEFAULT_PORT = 8888;

    private AsynchronousSocketChannel clientChannel;

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
                System.out.println("关闭" + closeable);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        try {
            // 创建channel
            clientChannel = AsynchronousSocketChannel.open();

            Future<Void> future = clientChannel.connect(new InetSocketAddress(LOCALHOST, DEFAULT_PORT));
            // future.get是阻塞式操作
            future.get();

            // 等待用户控制台输入
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String input = consoleReader.readLine();
                byte[] inputBytes = input.trim().getBytes();
                ByteBuffer buffer = ByteBuffer.wrap(inputBytes);

                Future<Integer> writeFuture = clientChannel.write(buffer);
                // 阻塞等待
                writeFuture.get();

                buffer.flip();
                Future<Integer> readFuture = clientChannel.read(buffer);
                readFuture.get();
                String echo = new String(buffer.array());
                System.out.println("服务器返回值：" + echo);
                buffer.clear();
            }
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        AioChatClient client = new AioChatClient();
        client.start();
    }
}
