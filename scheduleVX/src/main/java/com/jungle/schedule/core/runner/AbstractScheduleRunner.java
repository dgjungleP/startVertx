package com.jungle.schedule.core.runner;

import io.vertx.core.Handler;
import lombok.Data;

@Data
public abstract class AbstractScheduleRunner implements ScheduleRunner {
    private String id;

    @Override
    public Boolean run() {
        return null;
    }

    @Override
    public Boolean stop() {
        return null;
    }


}
