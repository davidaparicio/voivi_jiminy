package eu.aparicio.david.jiminy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

public class ProxyVerticle extends AbstractVerticle {
    private static Logger logger = Logger.getAnonymousLogger();
    EventBus eventBus;

    @Override
    public void start() throws Exception {

        logger.info("[ProxyVerticle] Starting in " + Thread.currentThread().getName());
        logger.info("[ProxyVerticle] Started in " + Thread.currentThread().getName());

        vertx.eventBus()
                .consumer("proxy",
                        m -> {
                            logger.info("[ProxyVerticle] Forwarding:" + ((JsonObject) m.body()).getString("message") + " [" + Thread.currentThread().getName() + "]");
                            vertx.eventBus().send("load", m.body(), ar -> {
                                if (ar.succeeded()) {
                                    logger.info("[ProxyVerticle] Received:" + ar.result().body() + " [" + Thread.currentThread().getName() + "]");
                                    m.reply(ar.result().body());
                                }
                            });
                        }
                );

    };

}
