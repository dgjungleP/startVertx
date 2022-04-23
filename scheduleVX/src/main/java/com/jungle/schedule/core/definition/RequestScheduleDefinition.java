package com.jungle.schedule.core.definition;

import cn.hutool.http.Method;
import com.jungle.schedule.enums.ScheduleType;
import com.jungle.schedule.util.RequestUtil;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.concurrent.TimeUnit;

@EqualsAndHashCode(callSuper = true)
@Data
public class RequestScheduleDefinition extends AbstractScheduleDefinition {
    private String requestLink;
    private HttpMethod method;

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
        definition.setMethod(HttpMethod.GET);
        definition.setRequestLink("http://localhost:9965/schedule-info");
        return definition;
    }


    public ScheduleDefinition buildWithJson(JsonObject requestBody) {
        super.doBuild(requestBody);
        String requestLink = requestBody.getString("requestLink");
        String method = requestBody.getString("method");
        this.setRequestLink(requestLink);
        this.setMethod(HttpMethod.valueOf(method));
        return this;
    }

    @Override
    public Handler<Long> handler() {
        return res -> RequestUtil.execute(this.method, this.requestLink);
    }
}
