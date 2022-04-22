package com.jungle.schedule.util;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;

public class RequestUtil {
    private static final HttpClient httpClient;

    static {
        httpClient = Vertx.vertx().createHttpClient();
    }

    private RequestUtil() {


    }

    public Future<HttpClientRequest> get(String url) {
        return httpClient.request(HttpMethod.GET, url);
    }


}
