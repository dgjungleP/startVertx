package com.jungle.schedule.core.manager;

import com.jungle.schedule.core.ManagerInfo;
import com.jungle.schedule.core.definition.ScheduleDefinition;
import com.jungle.schedule.core.loader.ScheduleLoader;
import com.jungle.schedule.enums.StatusType;
import com.jungle.schedule.util.IDUtil;
import io.vertx.core.Vertx;

import javax.sound.midi.Soundbank;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractScheduleManager implements ScheduleManager {

    protected final Vertx vertx;
    private final Map<String, ScheduleDefinition> PERIODIC_MAP = new ConcurrentHashMap<>();
    private final Map<String, ScheduleDefinition> TIMER_MAP = new ConcurrentHashMap<>();
    private final Map<Long, ScheduleDefinition> RUNNING_MAP = new ConcurrentHashMap<>();
    private final Map<String, Long> INCREASE_MAP = new ConcurrentHashMap<>();
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
        info.setRunningCount(RUNNING_MAP.values().size());
        return info;

    }

    @Override
    public Boolean loadSchedule(ScheduleDefinition definition) {
        switch (definition.getType()) {
            case PERIODIC:
                return loadPeriodic(definition);
            case TIMER:
                return loadTimer(definition);
        }
        return false;
    }

    protected Boolean startSchedule(ScheduleDefinition definition) {
        switch (definition.getType()) {
            case PERIODIC:
                return startPeriodic(definition);

            case TIMER:
                return startTimer(definition);

        }
        return false;
    }


    private Boolean loadTimer(ScheduleDefinition definition) {
        String scheduleId = fillScheduleId(definition);
        TIMER_MAP.put(scheduleId, definition);
        return startSchedule(scheduleId);
    }

    private void startIncrease(ScheduleDefinition definition) {
        long increaseId = vertx.setPeriodic(definition.getCurrentDelay(), res -> this.increase());
        INCREASE_MAP.put(definition.getId(), increaseId);
        RUNNING_MAP.put(definition.getTimerId(), definition);
        definition.setStatus(StatusType.RUNNING);
    }

    private Boolean loadPeriodic(ScheduleDefinition definition) {
        String scheduleId = fillScheduleId(definition);
        PERIODIC_MAP.put(scheduleId, definition);
        return startSchedule(scheduleId);
    }

    private Boolean startTimer(ScheduleDefinition definition) {
        long id = vertx.setTimer(definition.getCurrentDelay(), definition.handler());
        definition.setTimerId(id);
        startIncrease(definition);

        return true;
    }

    private Boolean startPeriodic(ScheduleDefinition definition) {
        long id = vertx.setPeriodic(definition.getCurrentDelay(), definition.handler());
        definition.setTimerId(id);
        startIncrease(definition);
        return true;
    }

    private String fillScheduleId(ScheduleDefinition definition) {
        String scheduleId = IDUtil.randomId();
        definition.setId(scheduleId);
        return scheduleId;
    }

    public void load() {
        List<ScheduleDefinition> scheduleDefinitions = new ArrayList<>();
        for (ScheduleLoader loader : SCHEDULE_LOADER_LIST) {
            scheduleDefinitions.addAll(loader.load());
        }
        scheduleDefinitions.forEach(this::loadSchedule);
    }

    @Override
    public Boolean stopSchedule(String id) {
        ScheduleDefinition schedule = getSchedule(id);
        if (schedule == null) {
            System.out.println("Not found the schedule:" + id);
            return false;
        }
        Long timerId = schedule.getTimerId();
        if (timerId != null && !RUNNING_MAP.containsKey(schedule.getTimerId())) {
            System.out.println("This schedule :" + id + "  is not running!");
            return false;
        }
        RUNNING_MAP.remove(timerId);
        Long increaseId = INCREASE_MAP.remove(id);
        vertx.cancelTimer(timerId);
        vertx.cancelTimer(increaseId);
        schedule.setStatus(StatusType.STOP);
        return true;
    }

    @Override
    public Boolean startSchedule(String id) {
        ScheduleDefinition schedule = getSchedule(id);
        if (schedule == null) {
            System.out.println("Not found the schedule:" + id);
            return false;
        }
        Long timerId = schedule.getTimerId();
        if (timerId != null && RUNNING_MAP.containsKey(timerId)) {
            System.out.println("This schedule :" + id + "  has running!");
            return false;
        }
        return startSchedule(schedule);
    }

    private ScheduleDefinition getSchedule(String id) {
        if (PERIODIC_MAP.containsKey(id)) {
            return PERIODIC_MAP.get(id);
        }
        if (TIMER_MAP.containsKey(id)) {
            return TIMER_MAP.get(id);
        }
        return null;
    }
}
