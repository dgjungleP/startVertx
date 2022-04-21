package com.jungle.vertx.demo;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

//Cluster manager
public class ClusterVertxCore {

    public static void main(String[] args) {

        HazelcastClusterManager manager = new HazelcastClusterManager();

        VertxOptions options = new VertxOptions().setClusterManager(manager);

        Vertx.clusteredVertx(options, res -> {

            if (res.succeeded()) {
                Vertx vertx = res.result();

                EventBus eventBus = vertx.eventBus();

                System.out.println("There is a Event Bus:" + eventBus);

            } else {
                System.out.println("Failed:" + res.cause());
            }
        });

    }

}
