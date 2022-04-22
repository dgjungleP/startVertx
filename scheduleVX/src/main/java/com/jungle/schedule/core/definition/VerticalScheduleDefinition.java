package com.jungle.schedule.core.definition;

import io.vertx.core.Handler;

public class VerticalScheduleDefinition extends AbstractScheduleDefinition {
    @Override
    public Handler<Long> handler() {
        return res -> {
            System.out.println("World");
        };
    }
}
