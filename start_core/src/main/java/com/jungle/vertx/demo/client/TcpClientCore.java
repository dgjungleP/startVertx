package com.jungle.vertx.demo.client;

import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;

public class TcpClientCore {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        NetClientOptions clientOptions = new NetClientOptions().setConnectTimeout(10000).setLogActivity(true);

        NetClient netClient = vertx.createNetClient(clientOptions);

        netClient.connect(4321, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("Connected!");
                NetSocket socket = res.result();
                socket.write("Hello");
            } else {
                System.out.println("Failed to connect:" + res.cause().getMessage());
            }
        });
    }
}
