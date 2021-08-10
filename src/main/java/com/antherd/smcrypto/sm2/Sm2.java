package com.antherd.smcrypto.sm2;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Sm2 {

    private static Invocable invocable = null;

    static {
        try {
            File sm2js = new File("src/js/sm2.js");
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            engine.eval(new FileReader(sm2js));
            invocable = (Invocable) engine;
        } catch (IOException | ScriptException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成密钥对：publicKey = privateKey * G
     */
    public static Keypair generateKeyPairHex() {
        ScriptObjectMirror scriptObjectMirror = null;
        try {
            scriptObjectMirror = (ScriptObjectMirror) invocable.invokeFunction("generateKeyPairHex");
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return new Keypair((String) scriptObjectMirror.get("privateKey"), (String) scriptObjectMirror.get("publicKey"));
    }

    /**
     * 加密
     *
     * @param cipherMode 1 - C1C3C2，0 - C1C2C3
     */
    public static String doEncrypt(String msg, String publicKey, int cipherMode) {
        if (msg == null || msg.trim().isEmpty()) return "";
        String encryptMsg = null;
        try {
            Object[] param = new Object[]{msg, publicKey, cipherMode};
            encryptMsg = (String) invocable.invokeFunction("doEncrypt", param);
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return encryptMsg;
    }

    /**
     * 加密 cipherMode = 1
     */
    public static String doEncrypt(String msg, String publicKey) {
        return doEncrypt(msg, publicKey, 1);
    }

    /**
     * 解密
     *
     * @param cipherMode 1 - C1C3C2，0 - C1C2C3
     */
    public static String doDecrypt(String encryptData, String privateKey, int cipherMode) {
        if (encryptData == null || encryptData.trim().isEmpty()) return "";
        String msg = null;
        try {
            Object[] param = new Object[]{encryptData, privateKey, cipherMode};
            msg = (String) invocable.invokeFunction("doDecrypt", param);
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return msg;
    }

    /**
     * 解密 cipherMode = 1
     */
    public static String doDecrypt(String msg, String publicKey) {
        return doDecrypt(msg, publicKey, 1);
    }

    /**
     * 签名
     */
    public static String doSignature(String msg, String publicKey, SignatureOptions signatureOptions) {
        String signature = null;
        try {
            signature = (String) invocable.invokeFunction("doSignature", msg, publicKey, getOptionsMap(signatureOptions));
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return signature;
    }

    /**
     * 签名
     */
    public static String doSignature(String msg, String publicKey) {
        return doSignature(msg, publicKey, null);
    }

    /**
     * 验签
     */
    public static boolean doVerifySignature(String msg, String signHex, String publicKey, SignatureOptions signatureOptions) {
        boolean result = false;
        try {
            result = (boolean) invocable.invokeFunction("doVerifySignature", msg, signHex, publicKey, getOptionsMap(signatureOptions));
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 验签
     */
    public static boolean doVerifySignature(String msg, String signHex, String publicKey) {
        return doVerifySignature(msg, signHex, publicKey, null);
    }

    /**
     * 若签名参数中为默认值， js调用时不添加此参数
     *
     * @param signatureOptions
     * @return
     */
    private static Map<String, Object> getOptionsMap(SignatureOptions signatureOptions) {
        Map<String, Object> options = new HashMap<>();
        if (signatureOptions == null) return options;
        if (signatureOptions.getPointPool() != null && signatureOptions.getPointPool().size() == 4)
            options.put("pointPool", signatureOptions.getPointPool());
        if (signatureOptions.isDer()) options.put("der", signatureOptions.isDer());
        if (signatureOptions.isHash()) options.put("hash", signatureOptions.isHash());
        String publicKey = signatureOptions.getPublicKey();
        if (!(publicKey == null) && !publicKey.trim().equals("")) options.put("publicKey", publicKey);
        String userId = signatureOptions.getUserId();
        if (!(userId == null) && !userId.trim().equals("")) options.put("userId", userId);
        return options;
    }

    /**
     * 获取椭圆曲线点
     */
    public static Point getPoint() {
        Point point = null;
        try {
            ScriptObjectMirror invokeResult = (ScriptObjectMirror) invocable.invokeFunction("getPoint");
            point = new Point((String) invokeResult.get("privateKey"), (String) invokeResult.get("publicKey"), (Map<String, Object>) invokeResult.get("k"), (Map<String, Object>) invokeResult.get("x1"));
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return point;
    }
}
