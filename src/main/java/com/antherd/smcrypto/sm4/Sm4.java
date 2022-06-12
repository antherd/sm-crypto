package com.antherd.smcrypto.sm4;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author geyiwei
 */
public class Sm4 {

    private static Invocable invocable = null;

    static {
        try {
            InputStream inputStream = Sm4.class.getClassLoader().getResourceAsStream("sm4.js");
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            assert inputStream != null;
            engine.eval(new BufferedReader(new InputStreamReader(inputStream)));
            invocable = (Invocable) engine;
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加密
     *
     * @param msg        明文
     * @param key        key 16 进制字符串，要求为 128 比特
     * @param sm4Options 加密配置
     * @return 密文
     * @throws ScriptException Scripting通用异常
     */
    public static String encrypt(String msg, String key, Sm4Options sm4Options) throws ScriptException {
        if (msg == null || msg.trim().isEmpty()) {
            return "";
        }
        String encryptData = null;
        try {
            encryptData = (String) invocable.invokeFunction("encrypt", msg, key, getOptionsMap(sm4Options));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return encryptData;
    }

    /**
     * 加密
     *
     * @param msg 明文
     * @param key key 16 进制字符串，要求为 128 比特
     * @return 密文
     * @throws ScriptException Scripting通用异常
     */
    public static String encrypt(String msg, String key) throws ScriptException {
        return encrypt(msg, key, null);
    }

    /**
     * 解密
     *
     * @param encryptData 密文
     * @param key         key 16 进制字符串，要求为 128 比特
     * @param sm4Options  加密配置
     * @return 明文
     * @throws ScriptException Scripting通用异常
     */
    public static String decrypt(String encryptData, String key, Sm4Options sm4Options) throws ScriptException {
        if (encryptData == null || encryptData.trim().isEmpty()) {
            return "";
        }
        String decryptData = null;
        try {
            decryptData = (String) invocable.invokeFunction("decrypt", encryptData, key, getOptionsMap(sm4Options));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return decryptData;
    }

    /**
     * 解密
     *
     * @param encryptData 密文
     * @param key         16 进制字符串，要求为 128 比特
     * @return 明文
     * @throws ScriptException Scripting通用异常
     */
    public static String decrypt(String encryptData, String key) throws ScriptException {
        return decrypt(encryptData, key, null);
    }

    /**
     * 16 进制串转字节数组
     *
     * @param hex 16进制字符串
     * @return byte数组
     */
    public static byte[] hexToBytes(String hex) {
        int length = hex.length();
        byte[] result;
        if (length % 2 == 1) {
            length++;
            result = new byte[(length / 2)];
            hex = "0" + hex;
        } else {
            result = new byte[(length / 2)];
        }
        int j = 0;
        for (int i = 0; i < length; i += 2) {
            result[j] = hexToByte(hex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    /**
     * 16 进制字符转字节
     *
     * @param hex 16进制字符 0x00到0xFF
     * @return byte
     */
    private static byte hexToByte(String hex) {
        return (byte) Integer.parseInt(hex, 16);
    }

    /**
     * byte数组转 16 进制字符串
     *
     * @param bytes byte数组
     * @return 16 进制字符串
     */
    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * utf8 串转字节数组
     *
     * @param str utf8 串
     * @return byte数组
     */
    public static byte[] utf8ToArray(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 字节数组转 utf8 串
     *
     * @param arr byte数组
     * @return utf8 串
     */
    public static String arrayToUtf8(byte[] arr) {
        return new String(arr, StandardCharsets.UTF_8);
    }

    /**
     * 若sm4配置中为默认值， js调用时不添加此参数
     *
     * @param sm4Options 加密配置
     * @return 加密配置Map
     */
    private static Map<String, Object> getOptionsMap(Sm4Options sm4Options) {
        Map<String, Object> options = new HashMap<>();
        if (sm4Options == null) {
            return options;
        }
        String padding = sm4Options.getPadding();
        if (padding != null && !"".equals(padding.trim())) {
            options.put("padding", padding);
        }
        String mode = sm4Options.getMode();
        if (mode != null && !"".equals(mode.trim())) {
            options.put("mode", mode);
        }
        String iv = sm4Options.getIv();
        if (iv != null && !"".equals(iv.trim())) {
            options.put("iv", iv);
        }
        return options;
    }
}
