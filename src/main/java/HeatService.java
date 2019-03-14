import com.fasterxml.jackson.core.JsonParseException;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import modules.Switchable;
import modules.TargetModule;
import modules.timer.TimerEvent;
import modules.timer.TimerModule;

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class HeatService {

    private TimerModule timer;
    private Switchable switchable;
    private CompositeDisposable switchableSubscription;
    private TargetModule mode;

    public HeatService(TimerModule timer, Switchable switchable) {
        this.timer = timer;
        this.switchable = switchable;
        this.mode = TargetModule.OFF;
        this.switchableSubscription = new CompositeDisposable();

    }

    public Observable<TimerEvent> getTimerList() {
        return timer.getEvents();
    }

    public JsonArray timersToJson(Observable<TimerEvent> events) {
        return new JsonArray(events.toList().blockingGet());
    }


    public Single<JsonObject> addTimer(JsonObject bodyAsJson) {
        try {
            LocalTime start = LocalTime.parse(bodyAsJson.getString("start"));
            Duration duration = Duration.ofMinutes(bodyAsJson.getInteger("duration"));
            TimerEvent event = new TimerEvent(start, duration);
            timer.addTimer(event);
        } catch(Exception e) {
            return Single.error(e);
        }
        return Single.just(new JsonObject("{\"created\": \"success\"}"));
    }

    public Single<JsonObject> setRunDown(JsonObject runDownRequest) {
        switchableSubscription.clear();
        switchable.on();
        switchableSubscription.add(
                Observable.interval(runDownRequest.getInteger("duration"), TimeUnit.MINUTES)
                    .take(1)
                    .subscribe( x -> switchMode())
        );
        return Single.just(runDownRequest);
    }


    public Single<JsonObject> setMode(JsonObject bodyAsJson) {
        mode = TargetModule.valueOf(bodyAsJson.getString("mode"));
        switchMode();
        return Single.just(new JsonObject().put("newMode", mode));
    }

    private void switchMode() {
        switch (mode) {
            case OFF:
                switchable.off();
                switchableSubscription.clear();
                break;
            case ON:
                switchable.on();
                switchableSubscription.clear();
                break;
            case HEATING_TIMER:
                subscribeSwitchableToTimer();
                break;

        }
    }

    private void subscribeSwitchableToTimer() {
        switchableSubscription.clear();
        switchableSubscription.add(
                timer.subscribe(e -> {
                    if (e.desiresOn()) switchable.on();
                    else if (!e.desiresOn()) switchable.off();
                })
        );
    }

    public TargetModule getMode() {
        return mode;
    }

    public Single<JsonObject> removeTimer(JsonObject requestBody) {
        try {
            timer.removeTimer(requestBody.getInteger("index"));
        } catch(Exception e) {
            return Single.error(e);
        }
        return Single.just(new JsonObject("\"deleted\": \"true\""));
    }
}
