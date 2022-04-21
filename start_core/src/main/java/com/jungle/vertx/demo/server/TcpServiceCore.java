package com.jungle.vertx.demo.server;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;


public class TcpServiceCore {

    public static void main(String[] args) {

        NetServerOptions serverOptions = new NetServerOptions().setPort(4321).setLogActivity(true);

        Vertx vertx = Vertx.vertx();
        NetServer netServer = vertx.createNetServer(serverOptions);

        netServer.connectHandler(socket -> {
            socket.handler(buffer -> {
                System.out.println("Get the buffer:" + buffer.length());
                socket.write("你好呀");
            });
            socket.closeHandler(v -> {
                System.out.println("Socket has been closed");
            });
        });
        netServer.listen(res -> {
            if (res.succeeded()) {
                System.out.println("Service is now listening");
            } else {
                System.out.println("Sorry Failed to bind!");
            }
        });
        EventBus eventBus = vertx.eventBus();
    }
}
