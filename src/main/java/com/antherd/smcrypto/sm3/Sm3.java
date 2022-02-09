package com.antherd.smcrypto.sm3;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;

/**
 * @author geyiwei
 */
public class Sm3 {

    private static Invocable invocable = null;

    static {
        try {
            InputStream inputStream = Sm3.class.getClassLoader().getResourceAsStream("sm3.js");
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            assert inputStream != null;
            engine.eval(new BufferedReader(new InputStreamReader(inputStream)));
            invocable = (Invocable) engine;
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    /**
     * 杂凑
     *
     * @param msg 明文
     * @return 杂凑
     */
    public static String sm3(String msg) {
        if (msg == null || msg.trim().isEmpty()) {
            return "";
        }
        String hashData = null;
        try {
            hashData = (String) invocable.invokeFunction("constructSm3", msg);
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return hashData;
    }
}
