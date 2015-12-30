package com.raddle.shell.engine.impl;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.UUID;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.raddle.shell.engine.ShellResponse;

public class JsShellEngineTest {
    private static JsShellEngine engine = new JsShellEngine();

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Test
    public void testExecuteShell() throws UnsupportedEncodingException {
        engine.init("src/test/resources/commander");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        engine.executeShell(UUID.randomUUID().toString(), "demoCmd", null, new RemoteOutputResponse(bos, "utf-8"));
        ByteBuffer wrap = ByteBuffer.wrap(bos.toByteArray());
        while (wrap.hasRemaining()) {
            int toRead = wrap.getInt();
            byte[] bytes = new byte[toRead];
            wrap.get(bytes);
            ByteBuffer content = ByteBuffer.wrap(bytes);
            byte type = content.get();
            byte[] data = new byte[toRead - 2];
            content.get(data);
            byte last = content.get();
            Assert.assertEquals(0, last);
            if (type == ShellResponse.TYPE_MESSAGE) {
                System.out.println(new String(data, "utf-8"));
            } else if (type == ShellResponse.TYPE_EXIT_CODE) {
                ByteBuffer code = ByteBuffer.wrap(data);
                int int1 = code.getInt();
                System.out.println(int1);
                Assert.assertEquals(0, int1);
            } else {
                Assert.fail("不支持的类型" + type);
            }
        }
    }

}
