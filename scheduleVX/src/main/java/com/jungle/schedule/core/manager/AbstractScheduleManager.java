package com.jungle.schedule.core.manager;

import com.jungle.schedule.core.definition.ScheduleDefinition;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractScheduleManager implements ScheduleManager {

    protected final Vertx vertx;
    private final Map<Long, ScheduleDefinition> PERIODIC_MAP = new ConcurrentHashMap<>();
    private final Map<Long, ScheduleDefinition> TIMER_MAP = new ConcurrentHashMap<>();

    public AbstractScheduleManager(Vertx vertx) {
        this.vertx = vertx;
    }

    public AbstractScheduleManager() {
        VertxOptions options = new VertxOptions();
        options.setWorkerPoolSize(50);
        vertx = Vertx.vertx(options);
    }

    @Override
    public void loadSchedule(ScheduleDefinition definition) {
        switch (definition.getType()) {
            case PERIODIC:
                loadPeriodic(definition);
                break;
            case TIMER:
                loadTimer(definition);
                break;
        }
    }

    private void loadTimer(ScheduleDefinition definition) {
        long id = vertx.setTimer(definition.getCurrentDelay(), definition.handler());
        definition.setId(id);
        TIMER_MAP.put(id, definition);
    }

    private void loadPeriodic(ScheduleDefinition definition) {
        long id = vertx.setPeriodic(definition.getCurrentDelay(), definition.handler());
        definition.setId(id);
        PERIODIC_MAP.put(id, definition);

    }
}
