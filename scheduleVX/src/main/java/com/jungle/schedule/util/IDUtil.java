package com.jungle.schedule.util;

import cn.hutool.core.lang.UUID;

public class IDUtil {

    private IDUtil() {
    }

    public static String randomId() {

        return UUID.randomUUID().toString(true);
    }
}
