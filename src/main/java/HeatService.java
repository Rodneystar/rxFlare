import com.sun.media.jfxmediaimpl.MediaDisposer;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.observers.DisposableObserver;
import io.vertx.core.json.JsonObject;
import modules.Switchable;
import modules.TargetModule;
import modules.timer.TimerModule;

import java.lang.annotation.Target;

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


    public Single<JsonObject> setMode(JsonObject bodyAsJson) {
        mode = TargetModule.valueOf(bodyAsJson.getString("mode"));

        switch (mode) {
            case OFF:
                switchable.off();
                break;
            case ON:
                switchable.on();
                break;
            case HEATING_TIMER:
                subscribeSwitchableToTimer();
                break;

        }
        return Single.just(new JsonObject().put("newMode", mode));
    }

    private void subscribeSwitchableToTimer() {
        switchableSubscription.clear();
        switchableSubscription.add(
                timer.observe().subscribe( e -> {
                    if(e.getShouldBeOn()) switchable.on();
                    else if(!e.getShouldBeOn()) switchable.off();
                })
        );
    }

    public TargetModule getMode() {
        return mode;
    }
}
