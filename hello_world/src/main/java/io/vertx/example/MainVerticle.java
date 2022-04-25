package io.vertx.example;

import io.vertx.core.AbstractVerticle;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start() {
        vertx.createHttpServer().requestHandler(request -> request.response().end("Hello")).listen(33235);
    }
//
//    public static void main(String[] args) {
//        String title = "C15 C20 C25 C30 C35 C40 C45 C50 C55 C60 C65 C70 C75 C80\n" +
//                "7.2 9.6 11.9 14.3 16.7 19.1 21.1 23.1 25.3 27.5 29.7 31.8 33.8 35.9\n" +
//                "10.0 13.4 16.7 20.1 23.4 26.8 29.6 32.4 35.5 38.5 41.5 44.5 47.4 50.2";
//
//        String[] split = title.split("\n");
//        List<String> keyList = Arrays.stream(split[0].split(" ")).collect(Collectors.toList());
//        List<String> value1List = Arrays.stream(split[1].split(" ")).collect(Collectors.toList());
//        List<String> value2List = Arrays.stream(split[2].split(" ")).collect(Collectors.toList());
//        for (int i = 0; i < keyList.size(); i++) {
//            System.out.println("{key:\"" + keyList.get(i) + "\",config: {fc:" + value1List.get(i) + ",fck:" + value2List.get(i) + "}},");
//        }
//
//
//    }

}
