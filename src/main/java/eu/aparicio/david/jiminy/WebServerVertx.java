package eu.aparicio.david.jiminy;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.util.logging.Logger;

public class WebServerVertx {

    //private static final Logger logger = LoggerFactory.getLogger(WebServerVerticle.class);
    private static Logger logger = Logger.getAnonymousLogger();

    public static void main(String[] args) {
        Vertx.clusteredVertx(new VertxOptions(), ar -> {
            Vertx vertx = ar.result();
            logger.info("[WebServer] Starting in " + Thread.currentThread().getName());
            configureSockJSBridge(vertx);
            logger.info("[WebServer] Started in " + Thread.currentThread().getName());
        });
    }

    private static void configureSockJSBridge(Vertx vertx) {
        Router router = Router.router(vertx);

        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        BridgeOptions options = new BridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddress("events"))
                .addInboundPermitted(new PermittedOptions().setAddress("worker"))
                .addOutboundPermitted(new PermittedOptions().setAddress("events"))
                .addOutboundPermitted(new PermittedOptions().setAddress("worker"));
        sockJSHandler.bridge(options);

        router.route("/eventbus/*").handler(sockJSHandler);
        router.route().handler(StaticHandler.create());
        //Configure the WebServer
        vertx.createHttpServer().requestHandler(router::accept).listen(8080, handler -> {
                    if (handler.succeeded()) {
                        logger.info("[WebServer] - http://localhost:8080/");
                    } else {
                        logger.info("[WebServer] - Failed to listen on port 8080");
                    }
                }
        );
    }
}
