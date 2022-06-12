package com.antherd.smcrypto;

import com.antherd.smcrypto.sm3.Sm3;
import org.junit.Test;

import javax.script.ScriptException;

public class Sm3Test {

    @Test
    public void test() throws ScriptException {

        /**
         * 杂凑
         */
        String hashData = Sm3.sm3("abc"); // 杂凑
        System.out.println("hashData: " + hashData);
    }
}
