package com.raddle.shell.engine.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import com.raddle.shell.engine.ShellResponse;

/**
 * description: 输出格式：4字节（后续长度，包括type，数据，结尾）+1字节Type+后续数据+1字节0
 * @author xurong
 * time : 2015年12月30日 下午7:38:42
 */
public class RemoteOutputResponse implements ShellResponse {
    private OutputStream outputStream;
    private String charset;

    public RemoteOutputResponse(OutputStream outputStream, String charset) {
        this.outputStream = outputStream;
        this.charset = charset;
    }

    @Override
    public void writeResponse(byte type, byte[] bytes) {
        int length = 1 + bytes.length + 1;
        ByteBuffer b = ByteBuffer.allocate(4 + length);
        b.putInt(length).put(type).put(bytes).put((byte) 0);
        try {
            outputStream.write(b.array());
        } catch (IOException e) {
            throw new RuntimeException("write data error ," + e.getMessage(), e);
        }
    }

    @Override
    public void writeMessage(String message) {
        if (message != null) {
            try {
                writeResponse(ShellResponse.TYPE_MESSAGE, message.getBytes(charset));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    @Override
    public void writeExitCode(int code) {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(code);
        writeResponse(ShellResponse.TYPE_EXIT_CODE, b.array());
    }

    @Override
    public void close() {
        try {
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("close out put error ," + e.getMessage(), e);
        }
    }

}
