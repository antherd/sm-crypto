package com.antherd.smcrypto;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Ice2Faith
 */
public class Provider {
    public static final String SM2_CLASSPATH_RESOURCE_PATH = "sm2.js";
    public static final String SM3_CLASSPATH_RESOURCE_PATH = "sm3.js";
    public static final String SM4_CLASSPATH_RESOURCE_PATH = "sm4.js";
    public static final String ENGINE_NAME = "JavaScript";

    public static ScriptEngine getJavaScriptEngine(String resourcePath) throws ScriptException {
        return getEngine(ENGINE_NAME, resourcePath);
    }

    public static ScriptEngine getEngine(String engineName, String resourcePath) throws ScriptException {
        ScriptEngine engine = new ScriptEngineManager().getEngineByName(engineName);
        if (resourcePath != null) {
            InputStream inputStream = Provider.class.getClassLoader().getResourceAsStream(resourcePath);
            assert inputStream != null;
            engine.eval(new BufferedReader(new InputStreamReader(inputStream)));
        }
        return engine;
    }
}
