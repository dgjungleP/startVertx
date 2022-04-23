package com.jungle.schedule.core.definition;

import com.jungle.schedule.enums.ScheduleType;
import com.jungle.schedule.enums.StatusType;
import io.vertx.core.Handler;
import lombok.Data;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Data
public abstract class AbstractScheduleDefinition implements ScheduleDefinition {
    protected ScheduleType type;
    protected Long id;
    protected StatusType status;
    protected Long delay;
    protected TimeUnit unit;
    protected String name;
    protected String description;


    public Long getCurrentDelay() {
        if (Objects.isNull(delay) || Objects.isNull(unit)) {
            return TimeUnit.MILLISECONDS.toMillis(1000L);
        }
        return unit.toMillis(delay);
    }
}
