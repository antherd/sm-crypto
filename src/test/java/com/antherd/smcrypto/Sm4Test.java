package com.antherd.smcrypto;

import com.antherd.smcrypto.sm4.Sm4;
import com.antherd.smcrypto.sm4.Sm4Options;
import org.junit.Test;

public class Sm4Test {

    @Test
    public void test() {

        /**
         * 加密
         */
        String msg = "hello world! 我是 antherd.";
        String key = "0123456789abcdeffedcba9876543210"; // 16 进制字符串，要求为 128 比特

        String encryptData1 = Sm4.encrypt(msg, key); // 加密，默认使用 pkcs#5 填充，输出16进制字符串
        System.out.println("encryptData1: " + encryptData1);

        Sm4Options sm4Options2 = new Sm4Options();
        sm4Options2.setPadding("none");
        String encryptData2 = Sm4.encrypt(msg, key, sm4Options2); // 加密，不使用 padding，输出16进制字符串
        System.out.println("encryptData2: " + encryptData2);

        Sm4Options sm4Options3 = new Sm4Options();
        sm4Options3.setPadding("none");
        byte[] encryptData3 = Sm4.hexToBytes(Sm4.encrypt(msg, key, sm4Options3)); // 加密，不使用 padding，输出转为字节数组
        System.out.println("encryptData3: " + Sm4.bytesToHex(encryptData3)); // 字节数组转为16进制字符串

        Sm4Options sm4Options4 = new Sm4Options();
        sm4Options4.setMode("cbc");
        sm4Options4.setIv("fedcba98765432100123456789abcdef");
        String encryptData4 = Sm4.encrypt(msg, key, sm4Options4); // 加密，cbc 模式，输出16进制字符串
        System.out.println("encryptData4: " + encryptData4);

        /**
         * 解密
         */
        String encryptData = "0e395deb10f6e8a17e17823e1fd9bd98a1bff1df508b5b8a1efb79ec633d1bb129432ac1b74972dbe97bab04f024e89c"; // 加密后的 16 进制字符串

        String decryptData5 = Sm4.decrypt(encryptData, key); // 解密，默认使用 pkcs#5 填充，输出 utf8 字符串
        System.out.println("decryptData5：" + decryptData5);

        Sm4Options sm4Options6 = new Sm4Options();
        sm4Options2.setPadding("none");
        String decryptData6 = Sm4.decrypt(encryptData, key, sm4Options6); // 解密，不使用 padding，输出 utf8 字符串
        System.out.println("decryptData6：" + decryptData6);

        Sm4Options sm4Options7 = new Sm4Options();
        sm4Options2.setPadding("none");
        byte[] decryptData7 = Sm4.utf8ToArray(Sm4.decrypt(encryptData, key, sm4Options7)); // 解密，不使用 padding，输出转为字节数组
        System.out.println("decryptData7：" + Sm4.arrayToUtf8(decryptData7));

        Sm4Options sm4Options8 = new Sm4Options();
        sm4Options4.setMode("cbc");
        sm4Options4.setIv("fedcba98765432100123456789abcdef");
        String decryptData8 = Sm4.decrypt(encryptData, key, sm4Options8); // 解密，cbc 模式，输出 utf8 字符串
        System.out.println("encryptData8: " + decryptData8);
    }
}
