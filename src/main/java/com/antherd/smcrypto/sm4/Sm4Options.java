package com.antherd.smcrypto.sm4;

/**
 * @author geyiwei
 */
public class Sm4Options {

    String padding;
    String mode;
    String iv;

    public Sm4Options() {
    }

    public Sm4Options(String padding, String mode, String iv) {
        this.padding = padding;
        this.mode = mode;
        this.iv = iv;
    }

    public String getPadding() {
        return padding;
    }

    public void setPadding(String padding) {
        this.padding = padding;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getIv() {
        return iv;
    }

    public void setIv(String iv) {
        this.iv = iv;
    }

    @Override
    public String toString() {
        return "Sm4Options{" +
                "padding='" + padding + '\'' +
                ", mode='" + mode + '\'' +
                ", iv='" + iv + '\'' +
                '}';
    }
}
