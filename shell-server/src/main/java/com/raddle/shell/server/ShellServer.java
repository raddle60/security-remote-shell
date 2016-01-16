package com.raddle.shell.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.raddle.shell.encrypt.DESedeUtils;
import com.raddle.shell.encrypt.RsaUtils;

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
                    try {
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
                                    short clientIdentityLength = byteArray.getShort();
                                    if (clientIdentityLength > byteArray.remaining()) {
                                        // 消息格式不对
                                        socket.close();
                                        break;
                                    }
                                    byte[] clientIdentityBytes = new byte[clientIdentityLength];
                                    byteArray.get(clientIdentityBytes);
                                    String clientId = new String(clientIdentityBytes);
                                    short clientSignLength = byteArray.getShort();
                                    if (clientSignLength > byteArray.remaining()) {
                                        // 消息格式不对
                                        socket.close();
                                        break;
                                    }
                                    byte[] clientSignBytes = new byte[clientSignLength];
                                    byteArray.get(clientSignBytes);
                                    // TODO 客户端公钥验证签名
                                    int dataLength = byteArray.getInt();
                                    if (dataLength != byteArray.remaining()) {
                                        // 消息格式不对
                                        socket.close();
                                        break;
                                    }
                                    byte[] sessionId = new byte[8];
                                    byteArray.get(sessionId);
                                    if (Arrays.equals(sessionId, new byte[8])) {
                                        // 全是0，未登录
                                        // TODO 用服务端私钥解密
                                        byte[] encryptedData = byteArray.array();
                                        String json = new String(RsaUtils.decryptEcbPkcs1(null, encryptedData), "utf-8");
                                        try {
                                            JSON.parseObject(json);
                                        } catch (Exception e) {
                                            // 数据格式不正确
                                            socket.close();
                                            break;
                                        }
                                    } else {
                                        // 用session对应的密钥签名和加密
                                        // md5签名
                                        byte[] md5Sign = new byte[16];
                                        byteArray.get(md5Sign);
                                        byte[] des3Key = new byte[24];
                                        byte[] encryptedData = byteArray.array();
                                        byte[] dataMd5Sign = DigestUtils.md5(ArrayUtils.addAll(encryptedData, des3Key));
                                        if (!Arrays.equals(md5Sign, dataMd5Sign)) {
                                            // 签名不正确
                                            socket.close();
                                            break;
                                        }
                                        String json = new String(DESedeUtils.decodeECBPKCS5(des3Key, encryptedData), "utf-8");
                                        try {
                                            JSON.parseObject(json);
                                        } catch (Exception e) {
                                            // 数据格式不正确
                                            socket.close();
                                            break;
                                        }
                                    }
                                } else {
                                    byteArray.reset();
                                    byteArray.compact();
                                    // 未收完
                                    continue;
                                }
                            }
                        }
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }, "shellClient-" + socket.getRemoteSocketAddress()).start();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
