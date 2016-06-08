package eu.aparicio.david.jiminy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

public class WorkerVerticle extends AbstractVerticle {
    private static Logger logger = Logger.getAnonymousLogger();
    private static NLP nlp = new NLP();
    EventBus eventBus;

    @Override
    public void start() throws Exception {

        logger.info("[WorkerVerticle] Starting in " + Thread.currentThread().getName());

        java.util.Date date= new java.util.Date();
        nlp.init();

        logger.info("[WorkerVerticle] Started in " + Thread.currentThread().getName());

        vertx.eventBus()
                .consumer("worker",
                        m -> {
                            logger.info("[WorkerVerticle] Forwarding:" + ((JsonObject) m.body()).getString("message") + " [" + Thread.currentThread().getName() + "]");

                            Future<Void> sentimentFuture = Future.future();
                            Future<Void> subjectFuture = Future.future();

                            final JsonArray[] sentimentArray = {null};
                            final JsonArray[] subjectArray = {null};
                            final Integer[] mainSentiment = {null};

                            JsonObject json = (JsonObject) m.body();
                            String message = json.getString("message");

                            vertx.eventBus().send("sentiment", m.body(), ar -> {
                                if (ar.succeeded()) {
                                    logger.info("[WorkerVerticle] SentimentReceived:" + ar.result().body() + " [" + Thread.currentThread().getName() + "]");
                                    sentimentArray[0] = ((JsonObject) ar.result().body()).getJsonArray("sentimentArray");
                                    mainSentiment[0] = ((JsonObject) ar.result().body()).getInteger("globalSentiment");
                                    sentimentFuture.complete();
                                } else {
                                    sentimentFuture.fail("SentimentFail");
                                }
                            });
                            vertx.eventBus().send("subject", m.body(), ar -> {
                                if (ar.succeeded()) {
                                    logger.info("[WorkerVerticle] SubjectReceived:" + ar.result().body() + " [" + Thread.currentThread().getName() + "]");
                                    subjectArray[0] = ((JsonObject) ar.result().body()).getJsonArray("subjectArray");
                                    subjectFuture.complete();
                                } else {
                                    subjectFuture.fail("SubjectFail");
                                }
                            });

                            CompositeFuture.all(sentimentFuture, subjectFuture).setHandler(ar -> {
                                Date today = Calendar.getInstance().getTime();
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss");
                                String timestamp = formatter.format(today);

                                if (ar.succeeded()) {
                                    // All worked
                                    logger.info("[WorkerVerticle] AR Succeeded:");
                                    m.reply(new JsonObject()
                                            .put("message", message)
                                            .put("timestamp", timestamp)
                                            .put("globalSentiment", mainSentiment[0])
                                            .put("subjectArray", subjectArray[0])
                                            .put("sentimentArray", sentimentArray[0])
                                            .put("from", Thread.currentThread().getName()));
                                } else {
                                    // At least one vertice failed
                                    logger.info("[WorkerVerticle] AR Failed:");
                                    m.reply(new JsonObject()
                                            .put("message", "Internal error :/")
                                            .put("timestamp", timestamp)
                                            .put("from", Thread.currentThread().getName()));

                                }
                            });

                        }
                );

    };

}