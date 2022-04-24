package com.jungle.schedule.core.runner;

public interface ScheduleRunner {
    Boolean run();

    Boolean stop();

    String getId();
}
