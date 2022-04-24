package com.jungle.schedule.core.manager;

import com.jungle.schedule.core.ManagerInfo;
import com.jungle.schedule.core.definition.ScheduleDefinition;
import com.jungle.schedule.core.loader.ScheduleLoader;
import com.jungle.schedule.core.runner.ScheduleRunner;
import com.jungle.schedule.enums.StatusType;
import com.jungle.schedule.util.BufferUtil;
import com.jungle.schedule.util.IDUtil;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractScheduleManager implements ScheduleManager {

    protected final Vertx vertx;
    private final Map<String, ScheduleDefinition> PERIODIC_MAP = new ConcurrentHashMap<>();
    private final Map<String, ScheduleDefinition> TIMER_MAP = new ConcurrentHashMap<>();
    private final Map<String, ScheduleRunner> RUNNING_MAP = new ConcurrentHashMap<>();
    private final List<ScheduleLoader> SCHEDULE_LOADER_LIST = new ArrayList<>();
    private final AtomicInteger runningTimes = new AtomicInteger(0);
    private final EventBus eventBus;
    public static final long FAILED_TIMER = -1;

    enum ConsumerType {
        SCHEDULE_RUNNING, SCHEDULE_STOP, SCHEDULE_FINISH
    }

    public void increase() {
        runningTimes.incrementAndGet();
    }

    public AbstractScheduleManager(Vertx vertx) {
        this.vertx = vertx;
        this.eventBus = vertx.eventBus();
        prepare();
    }


    private void prepare() {
        eventBus.consumer(ConsumerType.SCHEDULE_RUNNING.name(), message -> {
            System.out.println(message + ConsumerType.SCHEDULE_RUNNING.name());
        });
        eventBus.consumer(ConsumerType.SCHEDULE_STOP.name(), message -> {
            System.out.println(message + ConsumerType.SCHEDULE_STOP.name());
        });
        eventBus.consumer(ConsumerType.SCHEDULE_FINISH.name(), message -> {
            System.out.println(message + ConsumerType.SCHEDULE_FINISH.name());
        });
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
        String scheduleId = fillScheduleId(definition);
        switch (definition.getType()) {
            case PERIODIC:
                PERIODIC_MAP.put(scheduleId, definition);
                break;
            case TIMER:
                TIMER_MAP.put(scheduleId, definition);
                break;
        }
        return startSchedule(scheduleId);
    }

    protected Boolean startSchedule(ScheduleDefinition definition) {
        ScheduleRunner runner = definition.makeRunner();
        long timerId = FAILED_TIMER;
        switch (definition.getType()) {
            case PERIODIC:
                timerId = vertx.setPeriodic(definition.getCurrentDelay(), makeRunningHandler(runner));
                break;
            case TIMER:
                timerId = vertx.setTimer(definition.getCurrentDelay(), makeRunningHandler(runner));
                break;
        }
        if (timerId == FAILED_TIMER) {
            return false;
        }
        definition.setTimerId(timerId);
        startIncrease(runner);
        return false;
    }


    private void startIncrease(ScheduleRunner runner) {
        RUNNING_MAP.put(runner.getId(), runner);
        ScheduleDefinition schedule = getSchedule(runner.getId());
        if (schedule != null) {
            this.increase();
            schedule.setStatus(StatusType.RUNNING);
        }
    }

    private Handler<Long> makeRunningHandler(ScheduleRunner runner) {
        return res -> {
            DeploymentOptions options = new DeploymentOptions();
            eventBus.send(ConsumerType.SCHEDULE_RUNNING.name(), BufferUtil.javaObject2Buffer(runner));
            runner.run();
            eventBus.send(ConsumerType.SCHEDULE_FINISH.name(), BufferUtil.javaObject2Buffer(runner));
        };
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
        if (timerId != null && !RUNNING_MAP.containsKey(schedule.getId())) {
            System.out.println("This schedule :" + id + "  is not running!");
            return false;
        }
        eventBus.send(ConsumerType.SCHEDULE_STOP.name(), BufferUtil.javaObject2Buffer(schedule));
        stopSchedule(schedule);
        return true;
    }

    private void stopSchedule(ScheduleDefinition schedule) {
        RUNNING_MAP.remove(schedule.getId());
        vertx.cancelTimer(schedule.getTimerId());
        schedule.setStatus(StatusType.STOP);
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
