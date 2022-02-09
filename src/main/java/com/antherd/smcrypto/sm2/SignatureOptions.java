package com.antherd.smcrypto.sm2;

import java.util.Queue;

/**
 * @author geyiwei
 */
public class SignatureOptions {

    /**
     *  椭圆曲线点
     */
    Queue<Point> pointPool;

    /**
     * der编解码
     */
    boolean der;

    /**
     *  sm3杂凑
     */
    boolean hash;

    /**
     * 公钥
     */
    String publicKey;

    /**
     * userId（长度小于 8192）默认值为 1234567812345678
     */
    String userId;

    public SignatureOptions() {
    }

    public SignatureOptions(Queue<Point> pointPool, boolean der, boolean hash, String publicKey, String userId) {
        this.pointPool = pointPool;
        this.der = der;
        this.hash = hash;
        this.publicKey = publicKey;
        this.userId = userId;
    }

    public Queue<Point> getPointPool() {
        return pointPool;
    }

    public void setPointPool(Queue<Point> pointPool) {
        this.pointPool = pointPool;
    }

    public boolean isDer() {
        return der;
    }

    public void setDer(boolean der) {
        this.der = der;
    }

    public boolean isHash() {
        return hash;
    }

    public void setHash(boolean hash) {
        this.hash = hash;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
