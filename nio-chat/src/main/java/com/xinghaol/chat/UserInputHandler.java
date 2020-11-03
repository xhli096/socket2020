package com.xinghaol.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author xinghaol
 * @version 1.0
 * @date 2020/10/30 1:04 下午
 * @description 用户输入信息handler
 */
public class UserInputHandler implements Runnable {
    private BioChatClient chatClient;

    public UserInputHandler(BioChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public void run() {
        try {
            // 等待用户输入消息
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                String message = reader.readLine();
                // 向服务器端发送消息
                chatClient.send(message);

                // 检查用户是否退出
                if (chatClient.readyToExit(message)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
