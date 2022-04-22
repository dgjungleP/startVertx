package com.jungle.schedule.core.manager;

import com.jungle.schedule.core.definition.ScheduleDefinition;
import com.jungle.schedule.core.loader.ScheduleLoader;
import com.sun.org.apache.xml.internal.security.utils.resolver.implementations.ResolverXPointer;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractScheduleManager implements ScheduleManager {

    protected final Vertx vertx;
    private final Map<Long, ScheduleDefinition> PERIODIC_MAP = new ConcurrentHashMap<>();
    private final Map<Long, ScheduleDefinition> TIMER_MAP = new ConcurrentHashMap<>();
    private final List<ScheduleLoader> SCHEDULE_LOADER_LIST = new ArrayList<>();

    public AbstractScheduleManager(Vertx vertx) {
        this.vertx = vertx;
        prepare();
    }

    public AbstractScheduleManager() {
        VertxOptions options = new VertxOptions();
        options.setWorkerPoolSize(50);
        vertx = Vertx.vertx(options);
        prepare();
    }

    private void prepare() {
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

    public void load() {
        List<ScheduleDefinition> scheduleDefinitions = new ArrayList<>();
        for (ScheduleLoader loader : SCHEDULE_LOADER_LIST) {
            scheduleDefinitions.addAll(loader.load());
        }
        scheduleDefinitions.forEach(this::loadSchedule);
    }
}
