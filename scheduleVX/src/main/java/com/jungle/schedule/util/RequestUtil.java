package com.jungle.schedule.util;

import cn.hutool.http.Method;
import com.jungle.schedule.application.SimpleApplication;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.RequestOptions;
import io.vertx.ext.web.client.WebClient;

public class RequestUtil {
    public static WebClient CLIENT = WebClient.create(SimpleApplication.vertx);

    private RequestUtil() {
    }

    public static void execute(HttpMethod method, String url) {
        RequestOptions options = new RequestOptions();
        options.setAbsoluteURI(url);

        CLIENT.request(method, options)
                .send()
                .onSuccess(response -> System.out.println(response.bodyAsString()))
                .onFailure(err -> System.out.println(err.getMessage()));

    }



}
