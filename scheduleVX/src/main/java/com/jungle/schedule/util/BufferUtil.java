package com.jungle.schedule.util;

import com.alibaba.fastjson.JSON;
import io.vertx.core.buffer.Buffer;


public class BufferUtil {
    private BufferUtil() {
    }

    public static Buffer javaObject2Buffer(Object object) {
        return Buffer.buffer(JSON.toJSONString(object));
    }
}
