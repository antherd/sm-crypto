package com.antherd.smcrypto;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ice2Faith
 */
public class NashornProvider {

    public static final String JDK_NASHORN_CLASS_NAME = "jdk.nashorn.api.scripting.NashornScriptEngine";
    public static final String OPENJDK_NASHORN_CLASS_NAME = "org.openjdk.nashorn.api.scripting.NashornScriptEngine";
    public static final String NASHORN_MAVEN_DEPENDENCY = "" +
            "<dependency>\n" +
            "    <groupId>org.openjdk.nashorn</groupId>\n" +
            "    <artifactId>nashorn-core</artifactId>\n" +
            "    <version>15.4</version>\n" +
            "</dependency>";

    private static final AtomicBoolean printed = new AtomicBoolean(false);
    private static final AtomicBoolean checked = new AtomicBoolean(false);
    private static final AtomicBoolean status = new AtomicBoolean(false);

    static {
        printNonNashorn();
    }

    public static void printNonNashorn() {
        if (printed.getAndSet(true)) {
            return;
        }
        boolean has = checkHasNashorn();
        if (has) {
            return;
        }
        System.err.println("none nashorn JavaScript engine found! please add this dependency into your pom.xml: \n" + NASHORN_MAVEN_DEPENDENCY);
    }

    public static synchronized boolean checkHasNashorn() {
        if (checked.get()) {
            return status.get();
        }
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String[] arr = new String[]{JDK_NASHORN_CLASS_NAME, OPENJDK_NASHORN_CLASS_NAME};
        for (int i = 0; i < arr.length; i++) {
            String className = arr[i];
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz != null) {
                    status.set(true);
                    break;
                }
            } catch (Throwable e) {

            }
            try {
                Class<?> clazz = loader.loadClass(className);
                if (clazz != null) {
                    status.set(true);
                    break;
                }
            } catch (Throwable e) {

            }
        }
        checked.set(true);
        return status.get();
    }
}
