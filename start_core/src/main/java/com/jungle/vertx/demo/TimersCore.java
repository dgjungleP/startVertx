package com.jungle.vertx.demo;

import io.vertx.core.Vertx;
import sun.nio.cs.SingleByte;

import javax.sound.midi.Soundbank;
import java.util.concurrent.TimeUnit;

public class TimersCore {
    public static void main(String[] args) throws InterruptedException {

        Vertx vertx = Vertx.vertx();

        vertx.setTimer(5000, id -> {
            System.out.println("And five second later this is printed");
        });
        long periodic = vertx.setPeriodic(1000, id -> {
            System.out.println("Every one second later this is printed");
        });

        System.out.println("First this is printed");

        TimeUnit.SECONDS.sleep(10);
        vertx.cancelTimer(periodic);
    }
}
