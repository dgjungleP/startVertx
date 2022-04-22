package com.jungle.schedule.core.loader;

import com.jungle.schedule.core.definition.ScheduleDefinition;

import java.util.List;

public interface ScheduleLoader {
    List<ScheduleDefinition> load();
}
