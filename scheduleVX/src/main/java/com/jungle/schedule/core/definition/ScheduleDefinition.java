package com.jungle.schedule.core.definition;

import com.jungle.schedule.core.runner.ScheduleRunner;
import com.jungle.schedule.enums.ScheduleType;
import com.jungle.schedule.enums.StatusType;
import io.vertx.core.Handler;

public interface ScheduleDefinition {
    Handler<Long> handler();

    Long getCurrentDelay();

    ScheduleType getType();

    void setId(String id);

    String getId();

    void setTimerId(Long id);

    Long getTimerId();

    void setStatus(StatusType status);

    ScheduleRunner makeRunner();

}
