package com.lh.utils;

public class RuntimeUtil {
    public static int cpuNum() {
        return Runtime.getRuntime().availableProcessors();
    }
}
