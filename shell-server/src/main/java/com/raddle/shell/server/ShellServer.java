package com.raddle.shell.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShellServer {
    private static final Logger logger = LoggerFactory.getLogger(ShellServer.class);
    private static int port = 10822;

    public static void main(String[] args) {
        try {
            final ServerSocket server1 = new ServerSocket(port);
            logger.info("listening on " + port);
            final Socket socket = server1.accept();
            logger.info("accepted " + socket);
            new Thread(new Runnable() {

                @Override
                public void run() {
                    byte[] buffer = new byte[1024 * 4];
                    ByteBuffer byteArray = ByteBuffer.allocate(buffer.length);
                    InputStream input = socket.getInputStream();
                    int n = 0;
                    while (-1 != (n = input.read(buffer))) {
                        if (n > 0) {
                            byteArray.put(buffer, 0, n);
                            byteArray.flip();
                            byteArray.mark();
                            int length = byteArray.getInt();
                            if (byteArray.remaining() == length) {
                                // 版本号
                                byte version = byteArray.get();
                                if (version != 1) {
                                    // 消息格式不对
                                    socket.close();
                                    break;
                                }
                                short sessionLength = byteArray.getShort();
                                if (sessionLength > 0) {
                                    // 已登录
                                    byte[] encryptSessionId = new byte[sessionLength];
                                    byteArray.get(encryptSessionId);
                                } else {
                                    // 未登录
                                    // 服务端公钥加密
                                    // 客户端私钥签名
                                }
                                String json = new String(byteArray.array(), "utf-8");
                                // 标记位
                                int mask = byteArray.getInt();
                                // 消息类型
                                int type = byteArray.get();
                                // bson串
                            } else if (byteArray.remaining() > length) {
                                // 短连接，字节长度不对
                                socket.close();
                                break;
                            } else {
                                byteArray.reset();
                                byteArray.compact();
                                // 未收完
                                continue;
                            }
                        }
                    }
                }
            }, "shellClient-" + socket.getRemoteSocketAddress()).start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
