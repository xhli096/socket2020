package com.xinghaol.chat;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author xinghaol
 * @version 1.0
 * @date 2020/11/2 3:00 下午
 * @description description
 */
public class NioChatClient {
    private static final String DEFAULT_SERVER_HOST = "127.0.0.1";
    private static final int DEFAULT_SERVER_PORT = 8888;
    private static final String EXIT = "exit";

    private String host;
    private int port;
    private SocketChannel client;
    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
    private Selector selector;
    private Charset charset = StandardCharsets.UTF_8;


    public NioChatClient() {
        this(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);
    }

    public NioChatClient(String host, int port) {
        this.host = host;
        this.port = port;
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

}
