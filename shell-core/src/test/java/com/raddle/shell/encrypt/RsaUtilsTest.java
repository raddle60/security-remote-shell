package com.raddle.shell.encrypt;

import java.security.KeyPair;

import org.junit.Assert;
import org.junit.Test;

public class RsaUtilsTest {

    @Test
    public void testGenerateKey() {
        KeyPair keyPair = RsaUtils.generateKey();
        String data = "123456";
        byte[] encryptEcbPkcs5 = RsaUtils.encryptEcbPkcs1(keyPair.getPublic(), data.getBytes());
        byte[] decryptEcbPkcs5 = RsaUtils.decryptEcbPkcs1(keyPair.getPrivate(), encryptEcbPkcs5);
        Assert.assertEquals(data, new String(decryptEcbPkcs5));
        byte[] signMD5withRSA = RsaUtils.signMD5withRSA(keyPair.getPrivate(), data.getBytes());
        Assert.assertTrue(RsaUtils.verifyMD5withRSA(keyPair.getPublic(), data.getBytes(), signMD5withRSA));
    }

}
