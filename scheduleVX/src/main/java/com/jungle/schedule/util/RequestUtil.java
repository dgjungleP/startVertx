package com.jungle.schedule.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;

public class RequestUtil {


    private RequestUtil() {


    }

    public static HttpResponse execute(Method method, String url) {
        HttpRequest request = HttpUtil.createRequest(method, url);

        return request.execute();
    }


}
