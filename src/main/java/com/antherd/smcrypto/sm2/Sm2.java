package com.antherd.smcrypto.sm2;

import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author geyiwei
 */
public class Sm2 {

    private static Invocable invocable = null;

    static {
        try {
            InputStream inputStream = Sm2.class.getClassLoader().getResourceAsStream("sm2.js");
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            assert inputStream != null;
            engine.eval(new BufferedReader(new InputStreamReader(inputStream)));
            invocable = (Invocable) engine;
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成密钥对：publicKey = privateKey * G
     *
     * @return 公私钥对
     * @throws ScriptException Scripting通用异常
     */
    public static Keypair generateKeyPairHex() throws ScriptException {
        ScriptObjectMirror scriptObjectMirror = null;
        try {
            scriptObjectMirror = (ScriptObjectMirror) invocable.invokeFunction("generateKeyPairHex");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return new Keypair((String) scriptObjectMirror.get("privateKey"), (String) scriptObjectMirror.get("publicKey"));
    }

    /**
     * 加密
     *
     * @param msg        明文
     * @param publicKey  公钥
     * @param cipherMode 1 - C1C3C2，0 - C1C2C3
     * @return 密文
     * @throws ScriptException Scripting通用异常
     */
    public static String doEncrypt(String msg, String publicKey, int cipherMode) throws ScriptException {
        if (msg == null || msg.trim().isEmpty()) {
            return "";
        }
        String encryptMsg = null;
        try {
            Object[] param = new Object[]{msg, publicKey, cipherMode};
            encryptMsg = (String) invocable.invokeFunction("doEncrypt", param);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return encryptMsg;
    }

    /**
     * 加密 cipherMode = 1
     *
     * @param msg       明文
     * @param publicKey 公钥
     * @return 密文
     * @throws ScriptException Scripting通用异常
     */
    public static String doEncrypt(String msg, String publicKey) throws ScriptException {
        return doEncrypt(msg, publicKey, 1);
    }

    /**
     * 解密
     *
     * @param encryptData 密文
     * @param privateKey  私钥
     * @param cipherMode  1 - C1C3C2，0 - C1C2C3
     * @return 明文
     * @throws ScriptException Scripting通用异常
     */
    public static String doDecrypt(String encryptData, String privateKey, int cipherMode) throws ScriptException {
        if (encryptData == null || encryptData.trim().isEmpty()) {
            return "";
        }
        String msg = null;
        try {
            Object[] param = new Object[]{encryptData, privateKey, cipherMode};
            msg = (String) invocable.invokeFunction("doDecrypt", param);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return msg;
    }

    /**
     * 解密 cipherMode = 1
     *
     * @param encryptData 密文
     * @param privateKey  私钥
     * @return 明文
     * @throws ScriptException Scripting通用异常
     */
    public static String doDecrypt(String encryptData, String privateKey) throws ScriptException {
        return doDecrypt(encryptData, privateKey, 1);
    }

    /**
     * 签名
     *
     * @param msg              明文
     * @param publicKey        公钥
     * @param signatureOptions 签名配置
     * @return 签名
     * @throws ScriptException Scripting通用异常
     */
    public static String doSignature(String msg, String publicKey, SignatureOptions signatureOptions) throws ScriptException {
        String signature = null;
        try {
            signature = (String) invocable.invokeFunction("doSignature", msg, publicKey, getOptionsMap(signatureOptions));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return signature;
    }

    /**
     * 签名
     *
     * @param msg       明文
     * @param publicKey 公钥
     * @return 签名
     * @throws ScriptException Scripting通用异常
     */
    public static String doSignature(String msg, String publicKey) throws ScriptException {
        return doSignature(msg, publicKey, null);
    }

    /**
     * 验签
     *
     * @param msg              明文
     * @param signHex          签名
     * @param publicKey        公钥
     * @param signatureOptions 签名配置
     * @return 验签是否通过
     * @throws ScriptException Scripting通用异常
     */
    public static boolean doVerifySignature(String msg, String signHex, String publicKey, SignatureOptions signatureOptions) throws ScriptException {
        boolean result = false;
        try {
            result = (boolean) invocable.invokeFunction("doVerifySignature", msg, signHex, publicKey, getOptionsMap(signatureOptions));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 验签
     *
     * @param msg       明文
     * @param signHex   签名
     * @param publicKey 公钥
     * @return 验签是否通过
     * @throws ScriptException Scripting通用异常
     */
    public static boolean doVerifySignature(String msg, String signHex, String publicKey) throws ScriptException {
        return doVerifySignature(msg, signHex, publicKey, null);
    }

    /**
     * 若签名配置中为默认值， js调用时不添加此参数
     *
     * @param signatureOptions 签名配置
     * @return 签名配置Map
     */
    private static Map<String, Object> getOptionsMap(SignatureOptions signatureOptions) {
        Map<String, Object> options = new HashMap<>(6);
        if (signatureOptions == null) {
            return options;
        }
        if (signatureOptions.getPointPool() != null && signatureOptions.getPointPool().size() == 4) {
            options.put("pointPool", signatureOptions.getPointPool());
        }
        if (signatureOptions.isDer()) {
            options.put("der", signatureOptions.isDer());
        }
        if (signatureOptions.isHash()) {
            options.put("hash", signatureOptions.isHash());
        }
        String publicKey = signatureOptions.getPublicKey();
        if (publicKey != null && !"".equals(publicKey.trim())) {
            options.put("publicKey", publicKey);
        }
        String userId = signatureOptions.getUserId();
        if (userId != null && !"".equals(userId.trim())) {
            options.put("userId", userId);
        }
        return options;
    }

    /**
     * 获取椭圆曲线点
     *
     * @return 椭圆曲线点
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
