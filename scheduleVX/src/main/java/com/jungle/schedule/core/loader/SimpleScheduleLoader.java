package com.jungle.schedule.core.loader;

import com.jungle.schedule.core.definition.ScheduleDefinition;

import java.util.ArrayList;
import java.util.List;

public class SimpleScheduleLoader extends AbstractScheduleLoader {
    @Override
    public List<ScheduleDefinition> doLoad() {
        return new ArrayList<>();
    }
}
