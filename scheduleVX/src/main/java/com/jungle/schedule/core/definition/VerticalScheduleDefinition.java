package com.jungle.schedule.core.definition;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

public class VerticalScheduleDefinition extends AbstractScheduleDefinition {
    @Override
    public Handler<Long> handler() {
        return res -> {
            System.out.println("World");
        };
    }

    public ScheduleDefinition buildWithJson(JsonObject requestBody) {
        super.doBuild(requestBody);
        return this;
    }
}
