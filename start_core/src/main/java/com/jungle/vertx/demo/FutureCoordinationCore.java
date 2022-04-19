package com.jungle.vertx.demo;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.net.NetServer;

public class FutureCoordinationCore {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        Future<HttpServer> httpServerFuture = vertx.createHttpServer().listen();
        Future<NetServer> netServerFuture = vertx.createNetServer().listen();
        //all any join
        CompositeFuture.all(httpServerFuture, netServerFuture).onComplete(result -> {
            if (result.succeeded()) {
                System.out.println("Success!");
            } else {
                System.out.println("Failed!");
            }
        });
    }
}
