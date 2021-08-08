package com.antherd.smcrypto;

import com.antherd.smcrypto.sm2.Keypair;
import com.antherd.smcrypto.sm2.Point;
import com.antherd.smcrypto.sm2.SignatureOptions;
import com.antherd.smcrypto.sm2.Sm2;
import org.junit.jupiter.api.Test;

import javax.script.ScriptException;
import java.util.*;

public class Sm2Test {

    @Test
    public void test() throws ScriptException, NoSuchMethodException {

        /**
         * 生成密钥对
         */
        Keypair keypair = Sm2.generateKeyPairHex();
        String privateKey = keypair.getPrivateKey(); // 公钥
        String publicKey = keypair.getPublicKey(); // 私钥
        System.out.println("privateKey: " + privateKey);
        System.out.println("publicKey: " + publicKey);

        /**
         * 加密解密
         */
        String msg = "这是需要加密的文字！！！";
        System.out.println("msg: " + msg);
        int cipherMode = 1; // 1 - C1C3C2，0 - C1C2C3，默认为1
        String encryptData = Sm2.doEncrypt(msg, publicKey, cipherMode); // 加密结果
        String decryptData = Sm2.doDecrypt(encryptData, privateKey, cipherMode); // 解密结果
        System.out.println("encryptData: " + encryptData);
        System.out.println("decryptData: " + decryptData);

        // 签名验签 ps：理论上来说，只做纯签名是最快的。
        /**
         * 纯签名 + 生成椭圆曲线点
         */
        long startTime = System.currentTimeMillis();
        String sigValueHex = Sm2.doSignature(msg, privateKey); // 签名
        System.out.println("[纯签名 + 生成椭圆曲线点] 耗时: " + (System.currentTimeMillis() - startTime));
        boolean verifyResult = Sm2.doVerifySignature(msg, sigValueHex, publicKey); // 验签结果
        System.out.println("sigValueHex: " + sigValueHex);
        System.out.println("verifyResult: " + verifyResult);

        /**
         * 纯签名
         */
        Queue<Point> pointPool = new LinkedList(Arrays.asList(Sm2.getPoint(), Sm2.getPoint(), Sm2.getPoint(), Sm2.getPoint()));
        SignatureOptions signatureOptions2 = new SignatureOptions();
        signatureOptions2.setPointPool(pointPool); // 传入事先已生成好的椭圆曲线点，可加快签名速度
        long startTime2 = System.currentTimeMillis();
        String sigValueHex2 = Sm2.doSignature(msg, privateKey, signatureOptions2);
        System.out.println("[纯签名] 耗时: " + (System.currentTimeMillis() - startTime2));
        boolean verifyResult2 = Sm2.doVerifySignature(msg, sigValueHex2, publicKey); // 验签结果
        System.out.println("sigValueHex2: " + sigValueHex2);
        System.out.println("verifyResult2: " + verifyResult2);

        /**
         * 纯签名 + 生成椭圆曲线点 + der编解码
         */
        SignatureOptions signatureOptions3 = new SignatureOptions();
        signatureOptions3.setDer(true);
        long startTime3 = System.currentTimeMillis();
        String sigValueHex3 = Sm2.doSignature(msg, privateKey, signatureOptions3); // 签名
        System.out.println("[纯签名 + 生成椭圆曲线点 + der编解码] 耗时: " + (System.currentTimeMillis() - startTime3));
        boolean verifyResult3 = Sm2.doVerifySignature(msg, sigValueHex3, publicKey, signatureOptions3); // 验签结果
        System.out.println("sigValueHex3: " + sigValueHex3);
        System.out.println("verifyResult3: " + verifyResult3);

        /**
         * 纯签名 + 生成椭圆曲线点 + sm3杂凑
         */
        SignatureOptions signatureOptions4 = new SignatureOptions();
        signatureOptions4.setHash(true);
        long startTime4 = System.currentTimeMillis();
        String sigValueHex4 = Sm2.doSignature(msg, privateKey, signatureOptions4); // 签名
        System.out.println("[纯签名 + 生成椭圆曲线点 + sm3杂凑] 耗时: " + (System.currentTimeMillis() - startTime4));
        boolean verifyResult4 = Sm2.doVerifySignature(msg, sigValueHex4, publicKey, signatureOptions4); // 验签结果
        System.out.println("sigValueHex4: " + sigValueHex4);
        System.out.println("verifyResult4: " + verifyResult4);

        /**
         * 纯签名 + 生成椭圆曲线点 + sm3杂凑（不做公钥推导）
         */
        SignatureOptions signatureOptions5 = new SignatureOptions();
        signatureOptions5.setHash(true);
        signatureOptions5.setPublicKey(publicKey); // 传入公钥的话，可以去掉sm3杂凑中推导公钥的过程，速度会比纯签名 + 生成椭圆曲线点 + sm3杂凑快
        long startTime5 = System.currentTimeMillis();
        String sigValueHex5 = Sm2.doSignature(msg, privateKey, signatureOptions5); // 签名
        System.out.println("[纯签名 + 生成椭圆曲线点 + sm3杂凑（不做公钥推导）] 耗时: " + (System.currentTimeMillis() - startTime5));
        boolean verifyResult5 = Sm2.doVerifySignature(msg, sigValueHex5, publicKey, signatureOptions5); // 验签结果
        System.out.println("sigValueHex5: " + sigValueHex5);
        System.out.println("verifyResult5: " + verifyResult5);

        /**
         * 纯签名 + 生成椭圆曲线点 + sm3杂凑 + 不做公钥推 + 添加 userId（长度小于 8192）
         * 默认 userId 值为 1234567812345678
         */
        SignatureOptions signatureOptions6 = new SignatureOptions();
        signatureOptions6.setHash(true);
        signatureOptions6.setPublicKey(publicKey);
        signatureOptions6.setUserId("testUserId");
        long startTime6 = System.currentTimeMillis();
        String sigValueHex6 = Sm2.doSignature(msg, privateKey, signatureOptions6); // 签名
        System.out.println("[纯签名 + 生成椭圆曲线点 + sm3杂凑 + 不做公钥推 + 添加 userId（长度小于 8192）] 耗时: " + (System.currentTimeMillis() - startTime6));
        boolean verifyResult6 = Sm2.doVerifySignature(msg, sigValueHex6, publicKey, signatureOptions6); // 验签结果
        System.out.println("sigValueHex6: " + sigValueHex6);
        System.out.println("verifyResult6: " + verifyResult6);

        /**
         * 获取椭圆曲线点
         */
        Point point = Sm2.getPoint(); // 获取一个椭圆曲线点，可在sm2签名时传入
        System.out.println("point: " + point);
    }
}
