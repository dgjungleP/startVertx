package com.jungle.schedule.core;

import com.jungle.schedule.core.definition.ScheduleDefinition;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
public class ManagerInfo {
    private List<ScheduleDefinition> periodicScheduleList;
    private List<ScheduleDefinition> timerScheduleList;
    private Integer currentCount;
    private Integer runningCount;
    private Integer totalRunningTime;

}
