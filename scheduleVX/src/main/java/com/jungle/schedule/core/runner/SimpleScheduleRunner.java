package com.jungle.schedule.core.runner;

import io.vertx.core.Handler;


public class SimpleScheduleRunner extends AbstractScheduleRunner {
    private Handler<Long> handler;


    @Override
    public Boolean run() {
        handler.handle(0L);
        return true;
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
