package com.jungle.schedule.core.manager;

import com.jungle.schedule.core.ManagerInfo;
import com.jungle.schedule.core.definition.ScheduleDefinition;
import com.jungle.schedule.core.loader.ScheduleLoader;
import com.jungle.schedule.core.runner.ScheduleRunner;
import com.jungle.schedule.enums.StatusType;
import com.jungle.schedule.util.BufferUtil;
import com.jungle.schedule.util.IDUtil;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;

import javax.sound.midi.Soundbank;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
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
            String id = message.body().toString();
            doStartSchedule(id);
        });
        eventBus.consumer(ConsumerType.SCHEDULE_STOP.name(), message -> {
            String id = message.body().toString();
            doStopSchedule(id);

        });
        eventBus.consumer(ConsumerType.SCHEDULE_FINISH.name(), message -> {
            String id = message.body().toString();
            doFinishSchedule(id);
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
        System.out.println(runner.getId());
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
        return false;
    }


    private Handler<Long> makeRunningHandler(ScheduleRunner runner) {
        return res -> {
            eventBus.publish(ConsumerType.SCHEDULE_RUNNING.name(), Buffer.buffer(runner.getId()));
            System.out.println("Start Event");
            runner.run();
            eventBus.publish(ConsumerType.SCHEDULE_FINISH.name(), Buffer.buffer(runner.getId()));
            System.out.println("Finish Event");
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
        if (timerId != null && !schedule.isRunning()) {
            System.out.println("This schedule :" + id + "  is not running!");
            return false;
        }
        eventBus.publish(ConsumerType.SCHEDULE_STOP.name(), Buffer.buffer(id));
        return true;
    }

    private void doStartSchedule(String id) {
        ScheduleDefinition schedule = getSchedule(id);
        if (schedule != null) {
            RUNNING_MAP.put(id, schedule.makeRunner());
            this.increase();
            schedule.setStatus(StatusType.RUNNING);
            System.out.println(schedule);

        }
    }

    private void doFinishSchedule(String id) {
        ScheduleDefinition schedule = getSchedule(id);
        RUNNING_MAP.remove(id);
        if (schedule != null) {
            schedule.setStatus(StatusType.INIT);
            System.out.println(schedule);

        }
    }

    private void doStopSchedule(String id) {
        ScheduleDefinition schedule = getSchedule(id);
        if (schedule == null) {
            System.out.println("Not found the schedule:" + id);
            return;
        }
        Long timerId = schedule.getTimerId();
        if (timerId != null && !schedule.isRunning()) {
            System.out.println("This schedule :" + id + "  is not running!");
            return;
        }
        RUNNING_MAP.remove(id);
        vertx.cancelTimer(timerId);
        schedule.setStatus(StatusType.STOP);
        System.out.println(schedule);
    }

    @Override
    public Boolean startSchedule(String id) {
        ScheduleDefinition schedule = getSchedule(id);
        if (schedule == null) {
            System.out.println("Not found the schedule:" + id);
            return false;
        }
        Long timerId = schedule.getTimerId();
        if (timerId != null && schedule.isRunning()) {
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
