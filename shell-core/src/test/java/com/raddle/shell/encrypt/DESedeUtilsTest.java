package com.raddle.shell.encrypt;

import org.junit.Assert;
import org.junit.Test;

public class DESedeUtilsTest {

    @Test
    public void testencodeECBPKCS5() {
        String data = "123456";
        byte[] key = "123456781234567812345678".getBytes();
        byte[] encodeECBPKCS5 = DESedeUtils.encodeECBPKCS5(key, data.getBytes());
        Assert.assertEquals(data, new String(DESedeUtils.decodeECBPKCS5(key, encodeECBPKCS5)));
        byte[] encodeCBCPKCS5 = DESedeUtils.encodeCBCPKCS5(key, new byte[8], data.getBytes());
        Assert.assertEquals(data, new String(DESedeUtils.decodeCBCPKCS5(key, new byte[8], encodeCBCPKCS5)));
    }
}
