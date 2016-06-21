package eu.aparicio.david.jiminy;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.text.ParseException;
import java.util.logging.Logger;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import java.security.SecureRandom;

public class SentimentVerticle extends AbstractVerticle {
    private static Logger logger = Logger.getAnonymousLogger();
    EventBus eventBus;

    @Override
    public void start() throws Exception {

        logger.info("[SentimentVerticle] Starting in " + Thread.currentThread().getName());
        logger.info("[SentimentVerticle] Started in " + Thread.currentThread().getName());

        Sentiment sentiment = new Sentiment();
        sentiment.init();

        // The shared key
        byte[] key128 = { (byte)177, (byte)119, (byte) 33, (byte) 13, (byte)164, (byte) 30, (byte)108, (byte)121, (byte)207, (byte)136, (byte)107, (byte)242, (byte) 12, (byte)224, (byte) 19, (byte)226 };

        vertx.eventBus()
                .consumer("sentiment",
                        m -> {
                            //Date today = Calendar.getInstance().getTime();
                            //SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss");
                            //String timestamp = formatter.format(today);

                            JsonObject json = (JsonObject) m.body();

                            String jweString = ((JsonObject) m.body()).getString("message");
                            // Parse into JWE object again...
                            JWEObject jweObject = null;
                            try {
                                jweObject = JWEObject.parse(jweString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            // Decrypt
                            try {
                                jweObject.decrypt(new DirectDecrypter(key128));
                            } catch (JOSEException e) {
                                e.printStackTrace();
                            }
                            // Get the plain text
                            Payload payload = jweObject.getPayload();
                            String message = payload.toString();

                            //JsonArray subjectArray   = nlp.findSubject(message);
                            JsonArray sentimentArray = sentiment.findSentiment(message);

                            logger.info("[SentimentVerticle] Receiving:" + message + " [" + Thread.currentThread().getName() + "]");
                            m.reply(new JsonObject()
                                    .put("message", message)
                                    //.put("timestamp", timestamp)
                                    .put("globalSentiment", sentiment.mainSentiment)
                                    //.put("subjectArray", subjectArray)
                                    .put("sentimentArray", sentimentArray)
                                    .put("from", Thread.currentThread().getName()));
                        }
                );

    };

}
