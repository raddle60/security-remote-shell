package com.raddle.shell.session;

import org.apache.commons.codec.binary.Hex;

/**
 * description: 
 * @author raddle60
 * time : 2016年1月5日 下午7:46:54
 */
public class ClientLogin {
    /**
     * 客户号
     */
    private String clientId;
    /**
     * 随机数
     */
    private String random;
    /**
     * 时间戳
     */
    private long timestamp;
    /**
     * 3des key
     */
    private byte[] DESedeKey;
    /**
     * 签名串
     */
    private byte[] sign;

    public String getSignString() {
        return clientId + "&" + random + "&" + timestamp + Hex.encodeHexString(DESedeKey).toUpperCase();
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRandom() {
        return random;
    }

    public void setRandom(String random) {
        this.random = random;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public byte[] getSign() {
        return sign;
    }

    public void setSign(byte[] sign) {
        this.sign = sign;
    }

    public byte[] getDESedeKey() {
        return DESedeKey;
    }

    public void setDESedeKey(byte[] dESedeKey) {
        DESedeKey = dESedeKey;
    }

}
