package com.jungle.schedule.core.manager;

import com.jungle.schedule.core.ManagerInfo;
import com.jungle.schedule.core.definition.ScheduleDefinition;
import com.jungle.schedule.core.loader.ScheduleLoader;
import io.vertx.core.Vertx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractScheduleManager implements ScheduleManager {

    protected final Vertx vertx;
    private final Map<Long, ScheduleDefinition> PERIODIC_MAP = new ConcurrentHashMap<>();
    private final Map<Long, ScheduleDefinition> TIMER_MAP = new ConcurrentHashMap<>();
    private final Map<Long, Long> INCREASE_MAP = new ConcurrentHashMap<>();
    private final List<ScheduleLoader> SCHEDULE_LOADER_LIST = new ArrayList<>();
    private final AtomicInteger runningTimes = new AtomicInteger(0);


    public void increase() {
        runningTimes.incrementAndGet();
    }

    public AbstractScheduleManager(Vertx vertx) {
        this.vertx = vertx;
        prepare();
    }


    private void prepare() {
    }

    @Override
    public ManagerInfo getInfo() {
        return buildManagerInfo();
    }

    protected ManagerInfo buildManagerInfo() {
        Collection<ScheduleDefinition> periodicValue = PERIODIC_MAP.values();
        Collection<ScheduleDefinition> timerValue = TIMER_MAP.values();
        ManagerInfo info = new ManagerInfo();
        info.setPeriodicScheduleList(new ArrayList<>(periodicValue));
        info.setTimerScheduleList(new ArrayList<>(timerValue));
        info.setCurrentCount(periodicValue.size() + timerValue.size());
        info.setTotalRunningTime(runningTimes.get());
        info.setRunningCount(0);
        return info;

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
        long increaseId = vertx.setPeriodic(definition.getCurrentDelay(), res -> {
            this.increase();
        });

        TIMER_MAP.put(id, definition);
        INCREASE_MAP.put(id, increaseId);
    }

    private void loadPeriodic(ScheduleDefinition definition) {
        long id = vertx.setPeriodic(definition.getCurrentDelay(), definition.handler());
        definition.setId(id);
        long increaseId = vertx.setPeriodic(definition.getCurrentDelay(), res -> {
            this.increase();
        });
        PERIODIC_MAP.put(id, definition);
        INCREASE_MAP.put(id, increaseId);
    }

    public void load() {
        List<ScheduleDefinition> scheduleDefinitions = new ArrayList<>();
        for (ScheduleLoader loader : SCHEDULE_LOADER_LIST) {
            scheduleDefinitions.addAll(loader.load());
        }
        scheduleDefinitions.forEach(this::loadSchedule);
    }
}
