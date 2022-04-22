package com.jungle.schedule.core.definition;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import com.jungle.schedule.enums.ScheduleType;
import com.jungle.schedule.util.RequestUtil;
import io.vertx.core.Handler;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.concurrent.TimeUnit;

@EqualsAndHashCode(callSuper = true)
@Data
public class RequestScheduleDefinition extends AbstractScheduleDefinition {
    private String requestLink;
    private Method method;

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
        definition.setMethod(Method.GET);
        definition.setRequestLink("http://localhost:9965/schedule-info");
        return definition;
    }

    @Override
    public Handler<Long> handler() {
        return res -> {
            RequestUtil.execute(this.method, this.requestLink);
        };
    }
}
