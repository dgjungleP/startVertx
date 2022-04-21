package com.jungle.vertx.demo.event;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.eventbus.MessageConsumer;

import java.util.concurrent.TimeUnit;

public class EventBusCore {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        EventBus eventBus = vertx.eventBus();

        eventBus.consumer("jungle.test", message -> {
            System.out.println(message.address() + "1:received  message.body() = " + message.body() + " " + message.headers().entries().toString());
        });
        eventBus.consumer("jungle.test", message -> {
            System.out.println(message.address() + "2:received  message.body() = " + message.body() + " " + message.headers().entries().toString());
            try {
                TimeUnit.MILLISECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            message.reply("Ok, get it!");
        });
        MessageConsumer<String> consumer = eventBus.consumer("jungle.test1");

        consumer.handler(message -> {
            System.out.println(message.address() + ":received  message.body() = " + message.body() + " " + message.headers().entries().toString());
        });

        consumer.completionHandler(res -> {
            if (res.succeeded()) {
                System.out.println("Register Success!");
            } else {
                System.out.println("Register Failed!");
            }
        });


        DeliveryOptions options = new DeliveryOptions();
        options.addHeader("token", "abc123");
        options.setSendTimeout(2);
        eventBus.publish("jungle.test", "Hello");
        eventBus.publish("jungle.test1", "world", options);
        eventBus.send("jungle.test", "Send message");
        eventBus.send("jungle.test", "Send message", options);
        eventBus.send("jungle.test", "Send message", options);

        eventBus.request("jungle.test", "Yo Yo request", options, res -> {
            if (res.succeeded()) {
                System.out.println("Received reply:" + res.result().body());
            }
            if (res.failed()) {
                System.out.println("Failed with :" + res.cause().getMessage());
            }

        });
        eventBus.request("jungle.test", "Yo Yo request", options, res -> {
            if (res.succeeded()) {
                System.out.println("Received reply:" + res.result().body());
            }
            if (res.failed()) {
                System.out.println("Failed with :" + res.cause().getMessage());
            }
        });
    }
}
