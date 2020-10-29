package com.xinghaol.socket;

import java.io.*;
import java.net.Socket;

/**
 * @author xinghaol
 * @version 1.0
 * @date 2020/10/29 10:29 上午
 * @description description
 */
public class Client {
    public static void main(String[] args) {
        final String DEFAULT_SERVER_HOST = "127.0.0.1";
        final int DEFAULT_SERVER_PORT = 8888;
        final String EXIT = "exit";
        Socket socket = null;
        BufferedWriter bufferedWriter = null;

        try {
            // 创建socket
            socket = new Socket(DEFAULT_SERVER_HOST, DEFAULT_SERVER_PORT);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // 等待用户输入信息
            BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                String input = consoleReader.readLine();
                // 发送消息给服务器
                bufferedWriter.write(input + "\n");
                bufferedWriter.flush();

                if (EXIT.equals(input)) {
                    break;
                }

                // 读取服务器返回的消息
                String msg = bufferedReader.readLine().trim();
                System.out.println("服务器返回消息：[" + msg + "]");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭writer的同时会关闭socket
            if (null != bufferedWriter) {
                try {
                    bufferedWriter.close();
                    System.out.println("关闭socket");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
