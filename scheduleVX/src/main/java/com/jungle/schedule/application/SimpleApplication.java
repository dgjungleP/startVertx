package com.jungle.schedule.application;

import com.jungle.schedule.core.ManagerInfo;
import com.jungle.schedule.core.definition.RequestScheduleDefinition;
import com.jungle.schedule.core.definition.SimpleScheduleDefinition;
import com.jungle.schedule.core.manager.ScheduleManager;
import com.jungle.schedule.core.manager.SimpleScheduleManager;
import com.jungle.schedule.enums.ScheduleType;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;


public class SimpleApplication {
    public static Vertx vertx;

    public static void main(String[] args) {

        vertx = buildVertx();
        SimpleScheduleManager manager = new SimpleScheduleManager(vertx);
        vertx.getOrCreateContext().put("schedules_manager", manager);
        buildHttpService(vertx);

    }

    private static void buildHttpService(Vertx vertx) {
        final ScheduleManager schedulesManager = (SimpleScheduleManager) vertx.getOrCreateContext().get("schedules_manager");

        HttpServerOptions serverOptions = new HttpServerOptions();
        HttpServer httpServer = vertx.createHttpServer(serverOptions);
        Router router = Router.router(vertx);
        router.get("/schedule-info").handler(ctx -> {
            HttpServerResponse response = ctx.response();
            response.putHeader("content-type", "text/plain");
            ManagerInfo info = schedulesManager.getInfo();
            response.end(info.toString());
        });

        httpServer.requestHandler(router).listen(9965, res -> {
            if (res.succeeded()) {
                System.out.println("Service start success!");
                loadBaseSchedule(schedulesManager);

            } else {
                System.out.println("Service start failed!" + res.cause().getMessage());
            }
        });
    }

    private static void loadBaseSchedule(ScheduleManager schedulesManager) {
        SimpleScheduleDefinition definition = new SimpleScheduleDefinition();
        definition.setType(ScheduleType.PERIODIC);
        definition.setHandler(handler -> {
            System.out.println(schedulesManager.getInfo());
        });
        schedulesManager.loadSchedule(definition);
        schedulesManager.loadSchedule(RequestScheduleDefinition.simplePeriodic());

    }

    private static Vertx buildVertx() {
        VertxOptions options = new VertxOptions();
        options.setWorkerPoolSize(50);
        return Vertx.vertx(options);
    }
}
