package com.jungle.schedule.core;

import com.jungle.schedule.core.definition.ScheduleDefinition;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class ManagerInfo {
    private List<ScheduleDefinition> periodicScheduleList;
    private List<ScheduleDefinition> timerScheduleList;

}
