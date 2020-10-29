package com.xinghaol.socket;

import com.google.common.base.Strings;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author xinghaol
 * @version 1.0
 * @date 2020/10/29 10:29 上午
 * @description description
 */
public class Server {
    public static void main(String[] args) {
        final int DEFAULT_PORT = 8888;
        ServerSocket serverSocket = null;

        // 绑定监听端口
        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("启动服务器，监听端口：" + DEFAULT_PORT);

            while (true) {
                // 等待客户端连接
                Socket socket = serverSocket.accept();
                System.out.println("客户端[" + socket.getInetAddress().getHostAddress() + "." + socket.getPort() + "]已连接");

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                // 读取客户端发送的消息
                String msg = bufferedReader.readLine().trim();
                if (!Strings.isNullOrEmpty(msg)) {
                    System.out.println("客户端[" + socket.getInetAddress().getHostAddress() + "." + socket.getPort() + "]：" + msg);

                    // 回复客户发送的消息
                    bufferedWriter.write("服务器端返回消息：[" + msg + "]\n");
                    bufferedWriter.flush();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                    System.out.println("关闭serverSocket");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
