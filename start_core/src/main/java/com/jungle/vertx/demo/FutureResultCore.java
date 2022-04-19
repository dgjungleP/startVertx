package com.jungle.vertx.demo;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileProps;
import io.vertx.core.file.FileSystem;

public class FutureResultCore {

    public static void main(String[] args) {
        FileSystem fileSystem = Vertx.vertx().fileSystem();
        Future<FileProps> future = fileSystem.props("my_file.txt");

        future.onComplete(result -> {
            if (result.succeeded()) {
                FileProps fileProps = result.result();

                System.out.println("File Size:" + fileProps.size());
            } else {
                System.out.println("Failure:" + result.cause().getMessage());
            }
        });


    }
}
