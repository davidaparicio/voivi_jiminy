package eu.aparicio.david.jiminy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

public class FilterVerticle extends AbstractVerticle {
    private static Logger logger = Logger.getAnonymousLogger();
    private static NLP nlp = new NLP();
    EventBus eventBus;

    @Override
    public void start() throws Exception {

        logger.info("[FilterVerticle] Starting in " + Thread.currentThread().getName());
        logger.info("[FilterVerticle] Started in " + Thread.currentThread().getName());

        vertx.eventBus()
                .consumer("events",
                        m -> {
                            logger.info("[FilterVerticle] Forwarding:" + ((JsonObject) m.body()).getString("message") + " [" + Thread.currentThread().getName() + "]");
                            vertx.eventBus().send("worker", m.body(), ar -> {
                                if (ar.succeeded()) {
                                    logger.info("[FilterVerticle] Received:" + ar.result().body() + " [" + Thread.currentThread().getName() + "]");
                                    m.reply(ar.result().body());
                                }
                            });
                        }
                );

    };

}
