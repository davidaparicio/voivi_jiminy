package eu.aparicio.david.jiminy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.logging.Logger;

public class SubjectVerticle extends AbstractVerticle {
    private static Logger logger = Logger.getAnonymousLogger();
    EventBus eventBus;

    @Override
    public void start() throws Exception {

        logger.info("[SubjectVerticle] Starting in " + Thread.currentThread().getName());
        logger.info("[SubjectVerticle] Started in " + Thread.currentThread().getName());

        Subject subject = new Subject();
        subject.init();

        vertx.eventBus()
                .consumer("subject",
                        m -> {
                            //Date today = Calendar.getInstance().getTime();
                            //SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss");
                            //String timestamp = formatter.format(today);

                            JsonObject json = (JsonObject) m.body();
                            String message = json.getString("message");
                            JsonArray subjectArray = subject.findSubject(message);
                            //JsonArray sentimentArray = nlp.findSentiment(message);

                            logger.info("[SubjectVerticle] Receiving:" + message + " [" + Thread.currentThread().getName() + "]");
                            m.reply(new JsonObject()
                                    .put("message", message)
                                    //.put("timestamp", timestamp)
                                    //.put("globalSentiment", nlp.mainSentiment)
                                    .put("subjectArray", subjectArray)
                                    //.put("sentimentArray", sentimentArray)
                                    .put("from", Thread.currentThread().getName()));
                        }
                );

    };

}
