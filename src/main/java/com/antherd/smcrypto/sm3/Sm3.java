package com.antherd.smcrypto.sm3;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Sm3 {

    private static Invocable invocable = null;

    static {
        try {
            File sm3js = new File("src/js/sm3.js");
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            engine.eval(new FileReader(sm3js));
            invocable = (Invocable) engine;
        } catch (IOException | ScriptException e) {
            e.printStackTrace();
        }
    }

    /**
     * 杂凑
     */
    public static String sm3(String msg) {
        if (msg == null || msg.trim().isEmpty()) return "";
        String hashData = null;
        try {
            hashData = (String) invocable.invokeFunction("constructSm3", msg);
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return hashData;
    }
}
