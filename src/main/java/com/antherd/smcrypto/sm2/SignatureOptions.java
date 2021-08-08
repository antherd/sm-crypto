package com.antherd.smcrypto.sm2;

import java.util.Queue;

public class SignatureOptions {

    Queue<Point> pointPool; // 椭圆曲线点
    boolean der; // der编解码
    boolean hash; // sm3杂凑
    String publicKey; // 公钥
    String userId; // userId（长度小于 8192）默认值为 1234567812345678

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
