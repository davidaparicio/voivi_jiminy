package eu.aparicio.david.jiminy;

import edu.stanford.nlp.ie.util.RelationTriple;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.logging.Logger;

public class SentimentVertx {
    private static Logger logger = Logger.getAnonymousLogger();

    public static void main(String[] args) {
        Vertx.clusteredVertx(new VertxOptions(), ar -> {
            Vertx vertx = ar.result();
            logger.info("[SentimentVertx] Starting in " + Thread.currentThread().getName());
            vertx.deployVerticle(
                    SentimentVerticle.class.getName(),
                    new DeploymentOptions()
                            .setInstances(2)
                            //.setHa(true)
                            .setWorker(true));
            logger.info("[SentimentVertx] Started in " + Thread.currentThread().getName());
        });
    }
}
