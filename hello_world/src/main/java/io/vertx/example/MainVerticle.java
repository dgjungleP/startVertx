package io.vertx.example;

import io.vertx.core.AbstractVerticle;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() {
        vertx.createHttpServer().requestHandler(request -> request.response().end("Hello")).listen(33235);
    }

}
