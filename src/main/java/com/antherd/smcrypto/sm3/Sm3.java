package com.antherd.smcrypto.sm3;

import com.antherd.smcrypto.NashornProvider;
import com.antherd.smcrypto.Provider;

import javax.script.Invocable;
import javax.script.ScriptException;

/**
 * @author geyiwei
 */
public class Sm3 {
    static {
        NashornProvider.printNonNashorn();
    }
    private static Invocable invocable = null;

    static {
        try {
            invocable = (Invocable) Provider.getJavaScriptEngine(Provider.SM3_CLASSPATH_RESOURCE_PATH);
        } catch (ScriptException e) {
            e.printStackTrace();
        }
    }

    /**
     * 杂凑
     *
     * @param msg 明文
     * @return 杂凑
     * @throws ScriptException Scripting通用异常
     */
    public static String sm3(String msg) throws ScriptException {
        if (msg == null || msg.trim().isEmpty()) {
            return "";
        }
        String hashData = null;
        try {
            hashData = (String) invocable.invokeFunction("constructSm3", msg);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return hashData;
    }
}
