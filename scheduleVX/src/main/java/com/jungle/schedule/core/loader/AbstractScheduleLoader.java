package com.jungle.schedule.core.loader;

import com.jungle.schedule.core.definition.ScheduleDefinition;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractScheduleLoader implements ScheduleLoader {
    @Override
    public List<ScheduleDefinition> load() {
        return doLoad();
    }

    protected abstract List<ScheduleDefinition> doLoad();
}
