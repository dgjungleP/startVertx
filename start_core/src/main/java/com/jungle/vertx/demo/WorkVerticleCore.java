package com.jungle.vertx.demo;

import io.vertx.core.Context;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import javax.sound.midi.Soundbank;

public class WorkVerticleCore {

    public static void main(String[] args) {
        JsonObject config = new JsonObject().put("name", "Jungle").put("directory", "/blah");
        DeploymentOptions options = new DeploymentOptions().setWorker(false).setConfig(config);
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new MyVerticle(), options, res -> {
            if (res.succeeded()) {
                System.out.println("Deployment id is :" + res.result());
            } else {
                System.out.println("Deployment failed!");
            }
        });
        final Context context = vertx.getOrCreateContext();
        context.put("data", "hello");
        context.runOnContext((v) -> {
            System.out.println("context.config() = " + context.get("data"));
        });

    }
}
