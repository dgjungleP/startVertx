package com.jungle.schedule.core.manager;


import com.jungle.schedule.core.ManagerInfo;
import com.jungle.schedule.core.definition.ScheduleDefinition;

public interface ScheduleManager {
    void loadSchedule(ScheduleDefinition definition);

    ManagerInfo getInfo();

    void stopSchedule(String id);

    void startSchedule(String id);
}
