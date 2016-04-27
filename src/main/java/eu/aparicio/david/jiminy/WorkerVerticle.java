package eu.aparicio.david.jiminy;

import io.vertx.core.AbstractVerticle;
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
                .consumer("events",
                        m -> {
                            Date today = Calendar.getInstance().getTime();
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss");
                            String timestamp = formatter.format(today);

                            JsonObject json = (JsonObject) m.body();
                            String message = json.getString("message");
                            JsonArray subjectArray   = nlp.findSubject(message);
                            JsonArray sentimentArray = nlp.findSentiment(message);

                            logger.info("[WorkerVerticle] Receiving:" + message);
                            m.reply(new JsonObject()
                                    .put("message", message)
                                    .put("timestamp", timestamp)
                                    .put("globalSentiment", nlp.mainSentiment)
                                    .put("subjectArray", subjectArray)
                                    .put("sentimentArray", sentimentArray)
                                    .put("from", Thread.currentThread().getName()));
                        }
                );

    };

}