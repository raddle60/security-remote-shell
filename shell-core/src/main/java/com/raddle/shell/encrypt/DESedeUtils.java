package com.raddle.shell.encrypt;

import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DESedeUtils {
    private static SecretKeyFactory keyfactory;
    static {
        try {
            keyfactory = SecretKeyFactory.getInstance("DESede");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static byte[] encodeECBPKCS5(byte[] key, byte[] data) {
        try {
            Key deskey = null;
            DESedeKeySpec spec = new DESedeKeySpec(key);
            deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, deskey);
            byte[] bOut = cipher.doFinal(data);
            return bOut;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static byte[] decodeECBPKCS5(byte[] key, byte[] encryptData) {
        try {
            Key deskey = null;
            DESedeKeySpec spec = new DESedeKeySpec(key);
            deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, deskey);
            byte[] bOut = cipher.doFinal(encryptData);
            return bOut;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static byte[] encodeCBCPKCS5(byte[] key, byte[] keyiv, byte[] data) {
        try {
            Key deskey = null;
            DESedeKeySpec spec = new DESedeKeySpec(key);
            deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(keyiv);
            cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
            byte[] bOut = cipher.doFinal(data);
            return bOut;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static byte[] decodeCBCPKCS5(byte[] key, byte[] keyiv, byte[] encryptData) {
        try {
            Key deskey = null;
            DESedeKeySpec spec = new DESedeKeySpec(key);
            deskey = keyfactory.generateSecret(spec);
            Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
            IvParameterSpec ips = new IvParameterSpec(keyiv);
            cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
            byte[] bOut = cipher.doFinal(encryptData);
            return bOut;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
