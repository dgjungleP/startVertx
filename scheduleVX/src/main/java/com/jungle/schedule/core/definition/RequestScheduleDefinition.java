package com.jungle.schedule.core.definition;

import com.jungle.schedule.enums.ScheduleType;
import io.vertx.core.Handler;

import java.util.concurrent.TimeUnit;

public class RequestScheduleDefinition extends AbstractScheduleDefinition {
    public static RequestScheduleDefinition simplePeriodic() {
        RequestScheduleDefinition definition = simple();
        definition.setType(ScheduleType.PERIODIC);
        return definition;

    }

    public static RequestScheduleDefinition simpleTimer() {
        RequestScheduleDefinition definition = simple();
        definition.setType(ScheduleType.TIMER);

        return definition;

    }

    private static RequestScheduleDefinition simple() {
        RequestScheduleDefinition definition = new RequestScheduleDefinition();
        definition.setDelay(1L);
        definition.setUnit(TimeUnit.SECONDS);
        return definition;
    }

    @Override
    public Handler<Long> handler() {
        return res -> {
            System.out.println("hello：" + id);
        };
    }
}
