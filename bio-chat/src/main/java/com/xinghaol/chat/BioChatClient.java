package com.xinghaol.chat;

import java.io.*;
import java.net.Socket;

/**
 * @author xinghaol
 * @version 1.0
 * @date 2020/10/29 7:55 下午
 * @description description
 */
public class BioChatClient {
    private static final String DEFAULT_SERVER_HOST = "127.0.0.1";
    private static final String EXIT = "exit";
    private static final Integer DEFAULT_SERVER_PORT = 8888;

    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;

    /**
     * 发送信息至服务器
     *
     * @param message
     * @throws IOException
     */
    public void send(String message) throws IOException {
        if (!socket.isOutputShutdown()) {
            writer.write(message + "\n");
            writer.flush();
        }
    }

    /**
     * 从服务器接收消息
     *
     * @return
     * @throws IOException
     */
    public String receive() throws IOException {
        String message = null;

        if (!socket.isInputShutdown()) {
            message = reader.readLine();
        }

        return message;
    }

    /**
     * 验证用户是否准备退出
     *
     * @param message
     * @return
     */
    public boolean readyToExit(String message) {
        return EXIT.equals(message);
    }

    private void close() {
        // 关闭最外层的writer，里面的一些io流，和它占用的也可以被释放掉
        if (writer != null) {
            try {
                writer.close();
                System.out.println("关闭客户端socket");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void start() {
        try {
            // 创建socket
            socket = new Socket(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);

            // 创建IO流
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // 处理用户的输入，另起一个线程
            new Thread(new UserInputHandler(this)).start();

            // 读取服务器转发的消息
            String message = null;
            while ((message = receive()) != null) {
                System.out.println("接收到消息：[" + message + "]");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    public static void main(String[] args) {
        BioChatClient bioChatClient = new BioChatClient();
        bioChatClient.start();
    }
}
