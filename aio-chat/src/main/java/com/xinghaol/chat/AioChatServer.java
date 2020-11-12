package com.xinghaol.chat;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xinghaol
 * @version 1.0
 * @date 2020/11/4 10:35 下午
 * @description description
 */
public class AioChatServer {
    private static final String LOCALHOST = "localhost";
    private static final int DEFAULT_PORT = 8888;
    private AsynchronousServerSocketChannel serverSocketChannel;

    private void close(Closeable closable) {
        if (closable != null) {
            try {
                closable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        try {
            // 绑定监听端口
            // 底层对象 -> AsynchronousChannelGroup，和serverSocketChannel绑定在一起
            serverSocketChannel = AsynchronousServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress(LOCALHOST, DEFAULT_PORT));
            System.out.println("启动服务器，监听端口：" + DEFAULT_PORT);

            // 为了等待 知道有客户端发来请求的时候，server不能马上结束
            while (true) {
                serverSocketChannel.accept(null, new AcceptHandler());
                // 可以阻塞在这里，不会马上返回。
                System.in.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 实际的处理类
     */
    private class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Object> {
        @Override
        public void completed(AsynchronousSocketChannel result, Object attachment) {
            // 只要serverSocketChannel没有关闭，则一直要监听客户端的连接
            if (serverSocketChannel.isOpen()) {
                serverSocketChannel.accept(null, this);
            }

            // 进行读写的调用
            AsynchronousSocketChannel clientChannel = result;
            if (clientChannel != null && clientChannel.isOpen()) {
                // clientHandler 用于处理客户端的请求
                ClientHandler clientHandler = new ClientHandler(clientChannel);

                ByteBuffer buffer = ByteBuffer.allocate(1024);

                // 构造一个有意义的attachment
                Map<String, Object> info = new HashMap<>(16);
                info.put("type", "Read");
                info.put("buffer", buffer);

                // attachment作为一个有效的参数传递进read方法中去
                clientChannel.read(buffer, info, clientHandler);
            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
        }
    }

    /**
     * 用于读取客户端的buffer中的内容，泛型中第一个参数为Integer，即从buffer中读取到了多少个byte的数据
     */
    private class ClientHandler implements CompletionHandler<Integer, Object> {
        private AsynchronousSocketChannel clientChannel;

        public ClientHandler(AsynchronousSocketChannel clientChannel) {
            this.clientChannel = clientChannel;
        }

        @Override
        public void completed(Integer result, Object attachment) {
            Map<String, Object> info = (Map<String, Object>) attachment;
            // 读操作，从buffer中读取数据，再写回发送
            if ("Read".equals(info.get("type"))) {
                ByteBuffer buffer = (ByteBuffer) info.get("buffer");
                // 更改为读模式
                buffer.flip();
                info.put("type", "Write");
                clientChannel.write(buffer, info, this);
            } else if ("Write".equals(info.get("type"))) {
                ByteBuffer buffer = (ByteBuffer) info.get("buffer");
                // 构造一个有意义的attachment
                info.put("type", "Read");

                // attachment作为一个有效的参数传递进read方法中去
                clientChannel.read(buffer, info, this);
            }
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            // 处理读取后的错误
        }
    }

    public static void main(String[] args) {
        AioChatServer aioChatServer = new AioChatServer();
        aioChatServer.start();
    }
}
