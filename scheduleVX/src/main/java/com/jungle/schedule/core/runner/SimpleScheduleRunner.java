package com.jungle.schedule.core.runner;

import io.vertx.core.Handler;

import java.util.Random;
import java.util.concurrent.TimeUnit;


public class SimpleScheduleRunner extends AbstractScheduleRunner {
    private Handler<Long> handler;


    @Override
    public Boolean run() {
        return run(0L);
    }

    public Boolean run(Long timerId) {
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
