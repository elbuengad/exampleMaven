package com.example.testmaven;

import com.example.testmaven.http.Server;
import com.example.testmaven.utils.Constants;
import com.example.testmaven.utils.GenericPropertyLoader;
import io.vertx.core.Launcher;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class App  extends Launcher {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        GenericPropertyLoader.getInstance().loadProperties();

        Consumer<Vertx> runner = vertx -> {
            try {
                vertx.deployVerticle(Server.class.getName());
            } catch (Throwable t) {
                logger.error(t.getMessage());
            }
        };

        logger.info("Starting server on port "+Constants.SERVER_PORT);
        Vertx vertx = Vertx.vertx(new VertxOptions()
                .setWorkerPoolSize(Constants.VERTX_WORKER_POOL_SIZE)
                .setMaxWorkerExecuteTime(Constants.MAX_WORKER_EXECUTE_TIME));
        runner.accept(vertx);

    }

}
