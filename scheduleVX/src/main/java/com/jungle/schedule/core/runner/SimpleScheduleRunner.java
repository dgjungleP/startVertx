package com.jungle.schedule.core.runner;

import io.vertx.core.Handler;

import java.util.concurrent.TimeUnit;


public class SimpleScheduleRunner extends AbstractScheduleRunner {
    private Handler<Long> handler;


    @Override
    public Boolean run() {
        return run(0L);
    }

    public Boolean run(Long timerId) {
        System.out.println("Do Action");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        handler.handle(timerId);
        return true;
    }

    @Override
    public Boolean stop() {
        return null;
    }

    public void setHandler(Handler<Long> handler) {
        this.handler = handler;
    }
}
