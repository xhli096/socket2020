package com.xinghaol.chat;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xinghaol
 * @version 1.0
 * @date 2020/10/29 7:52 下午
 * @description description
 */
public class BioChatServer {
    private static final int DEFAULT_PORT = 8888;
    private static final String EXIT = "exit";

    /**
     * key-端口,对应的value为向这个socket写数据的writer
     */
    private Map<Integer, Writer> connectedClients;
    private ServerSocket serverSocket;

    public BioChatServer() {
        connectedClients = new HashMap<>();
    }

    public BioChatServer(Map<Integer, Writer> connectedClients) {
        this.connectedClients = connectedClients;
    }

    public void start() {
        // 绑定监听端口
        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("启动服务器，监听端口：" + DEFAULT_PORT + "....");

            while (true) {
                // 等待客户端连接
                Socket socket = serverSocket.accept();
                // 创建chat handler线程
                new Thread(new ChatHandler(this, socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    /**
     * 添加一个client; 线程安全性，先简单添加synchronized保证
     *
     * @param socket
     * @throws IOException
     */
    public synchronized void addClient(Socket socket) throws IOException {
        if (socket == null) {
            return;
        }

        Integer port = socket.getPort();
        if (!connectedClients.containsKey(port)) {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            connectedClients.put(port, bufferedWriter);
            System.out.println("客户端[" + port + "]已经连接到服务器");
        }
    }

    /**
     * 移除client
     *
     * @param socket
     * @throws IOException
     */
    public synchronized void removeClient(Socket socket) throws IOException {
        if (socket == null) {
            return;
        }
        Integer port = socket.getPort();
        if (!connectedClients.containsKey(port)) {
            return;
        }
        Writer writer = connectedClients.get(port);
        writer.close();
        connectedClients.remove(port);
        System.out.println("客户端[" + port + "]已断开链接");
    }

    /**
     * 转发消息至其他的在线客户端
     *
     * @param socket
     * @param message
     * @throws IOException
     */
    public synchronized void forwardMessage(Socket socket, String message) throws IOException {
        if (socket == null || message == null || "".equals(message)) {
            return;
        }
        Integer port = socket.getPort();
        for (Integer id : connectedClients.keySet()) {
            if (!id.equals(port)) {
                Writer writer = connectedClients.get(id);
                writer.write(message);
                writer.flush();
            }
        }
    }

    public boolean readyToExit(String message) {
        return EXIT.equals(message);
    }

    /**
     * 关闭serversocket
     */
    private synchronized void close() {
        if (null != serverSocket) {
            try {
                serverSocket.close();
                System.out.println("关闭serverSocket");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        BioChatServer bioChatServer = new BioChatServer();
        bioChatServer.start();
    }
}
