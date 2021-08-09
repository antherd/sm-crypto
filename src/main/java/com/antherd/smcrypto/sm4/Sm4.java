package com.antherd.smcrypto.sm4;

import org.apache.logging.log4j.util.Strings;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Sm4 {

    private static Invocable invocable = null;

    static {
        try {
            File sm2js = new ClassPathResource("sm4/sm4.js").getFile();
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            engine.eval(new FileReader(sm2js));
            invocable = (Invocable) engine;
        } catch (IOException | ScriptException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加密
     *
     * @return 16 进制密文字符串
     * @msg utf8 明文字符串
     */
    public static String encrypt(String msg, String key, @Nullable Sm4Options sm4Options) {
        String encryptData = null;
        try {
            encryptData = (String) invocable.invokeFunction("encrypt", msg, key, getOptionsMap(sm4Options));
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return encryptData;
    }

    /**
     * 加密
     *
     * @return 16 进制字符串
     * @msg utf8 字符串
     */
    public static String encrypt(String msg, String key) {
        return encrypt(msg, key, null);
    }

    /**
     * 解密
     *
     * @return utf8 字符串
     * @msg 16 进制字符串
     */
    public static String decrypt(String encryptData, String key, @Nullable Sm4Options sm4Options) {
        String decryptData = null;
        try {
            decryptData = (String) invocable.invokeFunction("decrypt", encryptData, key, getOptionsMap(sm4Options));
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return decryptData;
    }

    /**
     * 解密
     *
     * @return utf8 字符串
     * @msg 16 进制字符串
     */
    public static String decrypt(String encryptData, String key) {
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
     * 字节数组转 16 进制字符串
     */
    public static String bytesToHex(byte[] bytes) {
        if (bytes == null) return null;
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
     */
    public static byte[] utf8ToArray(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 字节数组转 utf8 串
     */
    public static String arrayToUtf8(byte[] arr) {
        return new String(arr, StandardCharsets.UTF_8);
    }

    /**
     * 若sm4参数中为默认值， js调用时不添加此参数
     *
     * @param sm4Options sm4 加密/解密 参数
     * @return js 加密/解密 参数
     */
    private static Map<String, Object> getOptionsMap(Sm4Options sm4Options) {
        Map<String, Object> options = new HashMap<>();
        if (sm4Options == null) return options;
        if (!Strings.isBlank(sm4Options.getPadding())) options.put("padding", sm4Options.getPadding());
        if (!Strings.isBlank(sm4Options.getMode())) options.put("mode", sm4Options.getMode());
        if (!Strings.isBlank(sm4Options.getIv())) options.put("iv", sm4Options.getIv());
        return options;
    }
}
