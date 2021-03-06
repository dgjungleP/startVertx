package com.jungle.schedule.core.definition;

import com.jungle.schedule.core.runner.ScheduleRunner;
import com.jungle.schedule.core.runner.SimpleScheduleRunner;
import com.jungle.schedule.enums.ScheduleType;
import com.jungle.schedule.enums.StatusType;
import io.vertx.core.json.JsonObject;
import lombok.Data;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Data
public abstract class AbstractScheduleDefinition implements ScheduleDefinition {
    protected ScheduleType type;
    protected String id;
    protected Long timerId;
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

    protected void doBuild(JsonObject requestBody) {
        this.status = StatusType.INIT;
        String type = requestBody.getString("type");
        Long delay = requestBody.getLong("delay");
        String unit = requestBody.getString("unit");
        String name = requestBody.getString("name");
        String description = requestBody.getString("description");
        this.setName(name);
        this.setDelay(delay);
        this.setDescription(description);
        this.setType(ScheduleType.valueOf(type));
        this.setUnit(TimeUnit.valueOf(unit));
    }

    @Override
    public ScheduleRunner makeRunner() {
        SimpleScheduleRunner runner = new SimpleScheduleRunner();
        runner.setHandler(handler());
        runner.setId(this.id);
        return runner;
    }

    @Override
    public boolean isRunning() {
        return !status.equals(StatusType.STOP);
    }
}
