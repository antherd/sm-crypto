package com.antherd.smcrypto.sm3;

import org.springframework.core.io.ClassPathResource;

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
            File sm3js = new ClassPathResource("sm3/sm3.js").getFile();
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
        String hashData = null;
        try {
            hashData = (String) invocable.invokeFunction("constructSm3", msg);
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return hashData;
    }
}
