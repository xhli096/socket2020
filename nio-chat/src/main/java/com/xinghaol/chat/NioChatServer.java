package com.xinghaol.chat;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 * @author xinghaol
 * @version 1.0
 * @date 2020/11/2 3:00 下午
 * @description description
 */
public class NioChatServer {
    private static final int DEFAULT_PORT = 8888;
    private static final String EXIT = "exit";
    private static final int BUFFER = 1024;

    private ServerSocketChannel serverSocketChannel;
    private Selector selector;
    private ByteBuffer readBuffer = ByteBuffer.allocate(BUFFER);
    private ByteBuffer writeBuffer = ByteBuffer.allocate(BUFFER);

    /**
     * 默认的编码方式
     */
    private Charset charset = StandardCharsets.UTF_8;
    /**
     * 允许用户自定义一个端口
     */
    private int port;

    public NioChatServer(int port) {
        this.port = port;
    }

    public NioChatServer() {
        this(DEFAULT_PORT);
    }

    private void start() {
        try {
            // ServerSocketChannel.open()创建的serverSocketChannel，处于一个阻塞方式的调用
            serverSocketChannel = ServerSocketChannel.open();
            // 设置为false，即设置为非阻塞的方式。NIO的方式要求为非阻塞方式
            serverSocketChannel.configureBlocking(false);
            // 监听server socket的特定端口
            serverSocketChannel.socket().bind(new InetSocketAddress(port));

            // nio中selector的处理
            selector = Selector.open();
            // 将服务器端监听客户端连接的channel，注册到selector上，监听accept事件，类似于bio中的accept函数
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("启动服务器，监听端口：" + port);

            // 如果没有监听到事件的发生，那么select函数会一直阻塞在这里。需要不断的监听，所以放到while循环中
            while (true) {
                selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                // 处理这些触发事件
                selectionKeys.forEach(selectionKey -> {
                    try {
                        handles(selectionKey);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

                // 必须自己手动的清除，不会自动清空。下一次的时间会累加到上一次时间的set中
                selectionKeys.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(serverSocketChannel);
            close(selector);
        }
    }

    private void handles(SelectionKey selectionKey) throws IOException {
        // ACCEPT事件-和客户端建立了连接
        if (selectionKey.isAcceptable()) {
            ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
            SocketChannel client = server.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
            System.out.println("客户端[" + client.socket().getPort() + "]已连接");
        }
        // READ事件-在客户端的channel中有了可读的消息，即客户端发送了消息
        if (selectionKey.isReadable()) {
            SocketChannel client = (SocketChannel) selectionKey.channel();
            String forwardMessage = receive(client);

            // 如果接受的消息为空，则当前的连接可能出现了一些异常。不在监听这个连接的请求。
            if (forwardMessage.isEmpty()) {
                // 取消，selector不在监听这个channel的
                selectionKey.cancel();
                // 通知selector
                selector.wakeup();
            } else {
                // 转发数据
                forwardMessage(selector, forwardMessage, client);

                if (readyToExit(forwardMessage)) {
                    selectionKey.cancel();
                    selector.wakeup();
                    System.out.println("客户端[" + client.socket().getPort() + "]已断开连接");
                }
            }
        }
    }

    private void forwardMessage(Selector selector, String message, SocketChannel client) {
        // selector.keys()会返回所有注册到selector上的集合。认为是当前所有在聊天室中的用户
        Set<SelectionKey> keys = selector.keys();
        keys.forEach(key -> {
            Channel connectedClient = key.channel();
            if (!(connectedClient instanceof ServerSocketChannel) && key.isValid() && !client.equals(connectedClient)) {
                writeBuffer.clear();
                writeBuffer.put(charset.encode("客户端[" + client.socket().getPort() + "]:" + message));
                writeBuffer.flip();

                // 从writeBuffer中读数据，并写入到channel中去，所以上面需要flip从写状态转成读状态
                while (writeBuffer.hasRemaining()) {
                    try {
                        ((SocketChannel) connectedClient).write(writeBuffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private String receive(SocketChannel channel) throws IOException {
        readBuffer.clear();
        // TODO ?
        while (channel.read(readBuffer) > 0) {
        }
        // 由写模式 -》 读模式
        readBuffer.flip();

        return String.valueOf(charset.decode(readBuffer));
    }

    public boolean readyToExit(String message) {
        return EXIT.equals(message);
    }

    private void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        NioChatServer nioChatServer = new NioChatServer(7777);
        nioChatServer.start();
    }
}
