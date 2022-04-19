package com.jungle.vertx.demo;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;

public class MyVerticle extends AbstractVerticle {

    private HttpServer server;

    @Override
    public void start() throws Exception {
        super.start();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        server = vertx.createHttpServer().requestHandler(req -> {
            req.response().putHeader("content-type", "text/plain")
                    .end("Hello Jungle!!");
        });

        server.listen(9965, res -> {
            if (res.succeeded()) {
                startPromise.complete();
            } else {
                startPromise.fail(res.cause());
            }
        });
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        super.stop(stopPromise);
    }
}
