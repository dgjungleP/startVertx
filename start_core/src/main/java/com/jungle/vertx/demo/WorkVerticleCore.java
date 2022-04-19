package com.jungle.vertx.demo;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;

public class WorkVerticleCore {

    public static void main(String[] args) {
        DeploymentOptions options = new DeploymentOptions().setWorker(true);
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new MyVerticle(),  res -> {
            if (res.succeeded()) {
                System.out.println("Deployment id is :" + res.result());
            } else {
                System.out.println("Deployment failed!");
            }
        });
    }
}
