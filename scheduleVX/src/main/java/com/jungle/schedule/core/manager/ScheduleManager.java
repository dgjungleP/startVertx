package com.jungle.schedule.core.manager;


import com.jungle.schedule.core.ManagerInfo;
import com.jungle.schedule.core.definition.ScheduleDefinition;

public interface ScheduleManager {
    Boolean loadSchedule(ScheduleDefinition definition);

    ManagerInfo getInfo();

    Boolean stopSchedule(String id);

    Boolean startSchedule(String id);
}
