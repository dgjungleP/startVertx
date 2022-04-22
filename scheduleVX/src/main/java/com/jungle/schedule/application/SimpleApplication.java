package com.jungle.schedule.application;

import com.jungle.schedule.core.definition.RequestScheduleDefinition;
import com.jungle.schedule.core.manager.SimpleScheduleManager;

public class SimpleApplication {

    public static void main(String[] args) {
        SimpleScheduleManager manager = new SimpleScheduleManager();

        manager.loadSchedule(RequestScheduleDefinition.simplePeriodic());
        manager.loadSchedule(RequestScheduleDefinition.simplePeriodic());
        manager.loadSchedule(RequestScheduleDefinition.simpleTimer());


    }
}
