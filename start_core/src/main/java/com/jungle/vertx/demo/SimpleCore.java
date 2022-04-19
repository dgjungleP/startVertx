package com.jungle.vertx.demo;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.util.concurrent.TimeUnit;

public class SimpleCore {
    public static void main(String[] args) {
        VertxOptions options = new VertxOptions().setWorkerPoolSize(20);
        Vertx vertx = Vertx.vertx(options);
        vertx.setPeriodic(1000, id -> System.out.println("Hello Vertx:" + id));
    }
}
