package com.jungle.schedule.application;

import com.alibaba.fastjson.JSON;
import com.jungle.schedule.core.ManagerInfo;
import com.jungle.schedule.core.definition.RequestScheduleDefinition;
import com.jungle.schedule.core.definition.SimpleScheduleDefinition;
import com.jungle.schedule.core.definition.VerticalScheduleDefinition;
import com.jungle.schedule.core.manager.ScheduleManager;
import com.jungle.schedule.core.manager.SimpleScheduleManager;
import com.jungle.schedule.enums.ScheduleType;
import com.jungle.schedule.plugin.JVMConsole;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class SimpleApplication {
    public static Vertx vertx;

    public static void main(String[] args) {
        vertx = buildVertx();
        SimpleScheduleManager manager = new SimpleScheduleManager(vertx);
        vertx.getOrCreateContext().put("schedules_manager", manager);
        buildHttpService(vertx);

    }

    private static void buildHttpService(Vertx vertx) {

        HttpServerOptions serverOptions = new HttpServerOptions();
        serverOptions.setLogActivity(true);
        HttpServer httpServer = vertx.createHttpServer(serverOptions);
        Router router = Router.router(vertx);
        routerConfig(router);
        buildRouter(router);
        httpServer.requestHandler(router).listen(9965, res -> {
            if (res.succeeded()) {
                System.out.println("Service start success!");
                loadBaseSchedule();
            } else {
                System.out.println("Service start failed!" + res.cause().getMessage());
            }
        });
    }

    private static void buildRouter(Router router) {
        final ScheduleManager schedulesManager = vertx.getOrCreateContext().get("schedules_manager");
        router.get("/schedule-info").handler(ctx -> {
            HttpServerResponse response = ctx.response();
            response.putHeader("content-type", "text/plain");
            ManagerInfo info = schedulesManager.getInfo();
            response.end(JSON.toJSONString(info));
        });

        router.post("/load/request").handler(ctx -> {
            ctx.request().body().onSuccess(res -> {
                JsonObject requestBody = res.toJsonObject();
                Boolean success = schedulesManager.loadSchedule(new RequestScheduleDefinition().buildWithJson(requestBody));
                HttpServerResponse response = ctx.response();
                response.putHeader("content-type", "text/plain");
                response.end(Buffer.buffer(success ? "Success!" : "Failed!"));
            });
        });

        router.post("/load/vertical").handler(ctx -> {
            ctx.request().body().onSuccess(res -> {
                JsonObject requestBody = res.toJsonObject();
                Boolean success = schedulesManager.loadSchedule(new VerticalScheduleDefinition().buildWithJson(requestBody));
                HttpServerResponse response = ctx.response();
                response.putHeader("content-type", "text/plain");
                response.end(Buffer.buffer(success ? "Success!" : "Failed!"));
            });
        });

        router.post("/stop/schedule").handler(ctx -> {
            String id = ctx.queryParam("id").get(0);
            Boolean success = schedulesManager.stopSchedule(id);

            HttpServerResponse response = ctx.response();
            response.putHeader("content-type", "text/plain");
            response.end(Buffer.buffer(success ? "Success!" : "Failed!"));
        });
        router.post("/start/schedule").handler(ctx -> {
            String id = ctx.queryParam("id").get(0);
            Boolean success = schedulesManager.startSchedule(id);
            HttpServerResponse response = ctx.response();
            response.putHeader("content-type", "text/plain");
            response.end(Buffer.buffer(success ? "Success!" : "Failed!"));
        });
    }

    private static void routerConfig(Router router) {
        CorsHandler corsHandler = CorsHandler.create("*").allowedMethods(new HashSet<>(HttpMethod.values()));
        router.route().handler(corsHandler);
    }

    private static void loadBaseSchedule() {
        Random random = new Random();
        final ScheduleManager schedulesManager = (SimpleScheduleManager) vertx.getOrCreateContext().get("schedules_manager");
        for (int i = 0; i < 30; i++) {
            SimpleScheduleDefinition definition = new SimpleScheduleDefinition();
            definition.setName("系统监控" + i);
            definition.setDescription("监控系统的数据指标");
            definition.setType(ScheduleType.PERIODIC);
            definition.setDelay(3L + random.nextInt(10));
            definition.setUnit(TimeUnit.SECONDS);
            definition.setHandler(handler -> JVMConsole.simple());
            schedulesManager.loadSchedule(definition);
        }

    }


    private static Vertx buildVertx() {
        VertxOptions options = new VertxOptions();
        options.setWorkerPoolSize(50);
        return Vertx.vertx(options);
    }
}
