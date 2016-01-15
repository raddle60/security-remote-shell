package com.raddle.shell.encrypt;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.RSAPrivateKeyStructure;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class RsaUtils {
    private static BouncyCastleProvider bouncyCastleProvider;
    static {
        bouncyCastleProvider = new BouncyCastleProvider();
    }

    public static KeyPair generateKey() {
        KeyPairGenerator keyPairGen;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(1024);
            KeyPair keyPair = keyPairGen.generateKeyPair();
            return keyPair;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * openssl genrsa -out rsa_1024_priv.pem 1024
     * @param privatekeyString
     * @return
     */
    public static PrivateKey getPrivateKeyFromASN1Base64String(String privatekeyString) {
        try {
            RSAPrivateKeyStructure asn1PrivKey = new RSAPrivateKeyStructure((ASN1Sequence) ASN1Object.fromByteArray(Base64.decodeBase64(privatekeyString)));
            RSAPrivateKeySpec rsaPrivKeySpec = new RSAPrivateKeySpec(asn1PrivKey.getModulus(), asn1PrivKey.getPrivateExponent());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey priKey = keyFactory.generatePrivate(rsaPrivKeySpec);
            return priKey;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * openssl rsa -pubout -in rsa_1024_priv.pem -out rsa_1024_pub.pem
     * @param publickeyString
     * @return
     */
    public static PublicKey getPublicKeyFromX509Base64String(String publickeyString) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(publickeyString));
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static byte[] encryptEcbPkcs1(PublicKey publicKey, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", bouncyCastleProvider);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static byte[] decryptEcbPkcs1(PrivateKey privateKey, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", bouncyCastleProvider);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static byte[] signMD5withRSA(PrivateKey privateKey, byte[] data) {
        try {
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initSign(privateKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static boolean verifyMD5withRSA(PublicKey publicKey, byte[] data, byte[] sign) {
        try {
            Signature signature = Signature.getInstance("MD5withRSA");
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(sign);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
