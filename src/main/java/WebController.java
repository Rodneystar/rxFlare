import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.handler.BodyHandler;
import modules.timer.TimerModule;

import static java.text.MessageFormat.format;

public class WebController {

    Logger logger = LoggerFactory.getLogger(WebController.class);
    Vertx vertx = Vertx.vertx();
    Router router = Router.router(vertx);
    HeatService heatService;

    public WebController(HeatService heatService) {
        this.heatService = heatService;
    }


    public void start() {

        router.route().handler(BodyHandler.create());

        router.route("/mode")
                .method(HttpMethod.PUT)
                .handler(context ->
                        Single
                                .just(context)
                                .doOnEvent((c, err) -> logger.info(format("{0}", context.getBodyAsJson())))
                                .flatMap(cText ->
                                        heatService.setMode(cText.getBodyAsJson()))
                                .subscribe(
                                        r -> context.response().end(r.encodePrettily()),
                                        e -> context.response().end(e.getMessage())

                                )
                );

        router.route("/add")
                .method(HttpMethod.POST)
                .handler(context ->
                        Single.just(context)
                                .doOnEvent((c, err) -> logger.info(format("{0}", context.getBodyAsJson())))
                                .flatMap( cText ->
                                        heatService.addTimer(cText.getBodyAsJson()))
                                .subscribe(
                                        r -> {
                                            context.response().setStatusCode(HttpResponseStatus.CREATED.code());
                                            context.response().end(r.encodePrettily());
                                        },
                                        e -> context.response().end(e.getMessage())
                                )
                );


        vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .rxListen(8080)
                .subscribe(server -> logger.info("server is running..."));

    }
}