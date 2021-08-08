package com.antherd.smcrypto.sm2;

import java.io.Serializable;
import java.util.Map;

public class Point extends Keypair implements Serializable {

    private Map<String, Object> k;
    private Map<String, Object> x1;

    public Point() {
    }

    public Point(Map<String, Object> k, Map<String, Object> x1) {
        this.k = k;
        this.x1 = x1;
    }

    public Point(String privateKey, String publicKey, Map<String, Object> k, Map<String, Object> x1) {
        super(privateKey, publicKey);
        this.k = k;
        this.x1 = x1;
    }

    public Map<String, Object> getK() {
        return k;
    }

    public void setK(Map<String, Object> k) {
        this.k = k;
    }

    public Map<String, Object> getX1() {
        return x1;
    }

    public void setX1(Map<String, Object> x1) {
        this.x1 = x1;
    }

    @Override
    public String toString() {
        return "Point{" +
                "privateKey=" + privateKey +
                ", publicKey=" + publicKey +
                ", k=" + k +
                ", x1=" + x1 +
                '}';
    }
}
