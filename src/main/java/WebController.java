import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import modules.ModManager;
import modules.TimerModule;

import java.text.MessageFormat;

import static java.text.MessageFormat.format;

public class WebController {

    Logger logger = LoggerFactory.getLogger(WebController.class);
    Vertx vertx = Vertx.vertx();
    Router router = Router.router(vertx);
    private ModManager modManager;

    public WebController() {
        modManager = new ModManager();
        modManager.addModule(new TimerModule(Schedulers.computation()));
    }


    public void start() {

        router.route().handler(BodyHandler.create());
        router
                .route("/")
                .method(HttpMethod.PUT)
                .handler(context ->
                        Single
                                .just(context)
                                .doOnEvent((c, err) -> logger.info(format("{0}", context.getBodyAsJson())))
                                .flatMap(cText ->
                                    modManager.order(cText.getBodyAsJson()))
                                .subscribe(
                                        r -> context.response().end( r.encodePrettily() ),
                                        e -> context.response().end( e.getMessage() )

                                )
                );

        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .rxListen(8080)
                .subscribe(server -> logger.info("server is running..."));

    }
}