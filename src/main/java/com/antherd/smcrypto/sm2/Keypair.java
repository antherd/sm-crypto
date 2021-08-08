package com.antherd.smcrypto.sm2;

import java.io.Serializable;

/**
 * 公私钥对
 */
public class Keypair implements Serializable {

    protected String privateKey;
    protected String publicKey;

    public Keypair() {
    }

    public Keypair(String privateKey, String publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @Override
    public String toString() {
        return "Keypair{" +
                "privateKey='" + privateKey + '\'' +
                ", publicKey='" + publicKey + '\'' +
                '}';
    }
}
