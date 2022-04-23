package com.jungle.schedule.application;

import com.alibaba.fastjson.JSON;
import com.jungle.schedule.core.ManagerInfo;
import com.jungle.schedule.core.definition.RequestScheduleDefinition;
import com.jungle.schedule.core.definition.SimpleScheduleDefinition;
import com.jungle.schedule.core.definition.VerticalScheduleDefinition;
import com.jungle.schedule.core.manager.ScheduleManager;
import com.jungle.schedule.core.manager.SimpleScheduleManager;
import com.jungle.schedule.enums.ScheduleType;
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
        final ScheduleManager schedulesManager = (SimpleScheduleManager) vertx.getOrCreateContext().get("schedules_manager");
        router.get("/schedule-info").handler(ctx -> {
            HttpServerResponse response = ctx.response();
            response.putHeader("content-type", "text/plain");
            ManagerInfo info = schedulesManager.getInfo();
            response.end(JSON.toJSONString(info));
        });

        router.post("/load/request").handler(ctx -> {
            ctx.request().body().onSuccess(res -> {
                JsonObject requestBody = res.toJsonObject();
                schedulesManager.loadSchedule(new RequestScheduleDefinition().buildWithJson(requestBody));
            });
            HttpServerResponse response = ctx.response();
            response.putHeader("content-type", "text/plain");
            response.end(Buffer.buffer("Success!"));
        });

        router.post("/load/vertical").handler(ctx -> {
            ctx.request().body().onSuccess(res -> {
                JsonObject requestBody = res.toJsonObject();
                schedulesManager.loadSchedule(new VerticalScheduleDefinition().buildWithJson(requestBody));
            });
            HttpServerResponse response = ctx.response();
            response.putHeader("content-type", "text/plain");
            response.end(Buffer.buffer("Success!"));
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
        final ScheduleManager schedulesManager = (SimpleScheduleManager) vertx.getOrCreateContext().get("schedules_manager");
        SimpleScheduleDefinition definition = new SimpleScheduleDefinition();
        definition.setType(ScheduleType.PERIODIC);
        definition.setHandler(handler -> System.out.println("Heart Beats!"));
        schedulesManager.loadSchedule(definition);
    }

    private static Vertx buildVertx() {
        VertxOptions options = new VertxOptions();
        options.setWorkerPoolSize(50);
        return Vertx.vertx(options);
    }
}
