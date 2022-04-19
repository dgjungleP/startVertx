package com.jungle.vertx.demo;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.file.FileSystem;

public class FutureCompositionCore {

    public static void main(String[] args) {
        FileSystem fileSystem = Vertx.vertx().fileSystem();

        Future<Void> future = fileSystem.createFile("foo")
                .compose(v -> fileSystem.writeFile("foo", Buffer.buffer()))
                .compose(v -> fileSystem.move("foo", "bar"));
        System.out.println("future.isComplete() = " + future.result());


    }
}
