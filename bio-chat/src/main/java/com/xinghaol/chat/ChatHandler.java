package com.xinghaol.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * @author xinghaol
 * @version 1.0
 * @date 2020/10/30 10:40 上午
 * @description 处理与客户端连接
 */
public class ChatHandler implements Runnable {
    /**
     * 用于处理 server中的map
     */
    private BioChatServer server;

    private Socket socket;

    public ChatHandler(BioChatServer server, Socket socket) {
        this.server = server;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            // 存储新上线用户
            server.addClient(socket);

            // 读取用户的消息
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message = null;
            while ((message = reader.readLine()) != null) {
                String fwdMessage = "客户端[" + socket.getPort() + "]发送消息:" + message + "\n";
                System.out.print(fwdMessage);

                // 将消息转发为其他在线的其他用户
                server.forwardMessage(socket, fwdMessage);

                // 检查用户是否退出
                if (server.readyToExit(message)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 把自己从map中移除
            try {
                server.removeClient(socket);
                System.out.println("客户端[" + socket.getPort() + "]断开连接");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
