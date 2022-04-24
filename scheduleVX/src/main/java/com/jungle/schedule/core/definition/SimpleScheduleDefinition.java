package com.jungle.schedule.core.definition;

import io.vertx.core.Handler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
public class SimpleScheduleDefinition extends AbstractScheduleDefinition {
    private Handler<Long> handler;


    @Override
    public Handler<Long> handler() {
        return handler;
    }
}
