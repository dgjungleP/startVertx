package com.jungle.schedule.core.definition;

import com.jungle.schedule.enums.ScheduleType;
import com.jungle.schedule.enums.StatusType;
import io.vertx.core.Handler;
import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data
public abstract class AbstractScheduleDefinition implements ScheduleDefinition {
    protected ScheduleType type;
    protected Long id;
    protected StatusType status;
    protected Long delay;
    protected TimeUnit unit;


    public Long getCurrentDelay() {
        return unit.toMillis(delay);
    }
}
