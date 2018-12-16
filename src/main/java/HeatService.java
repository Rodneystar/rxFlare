import com.sun.media.jfxmediaimpl.MediaDisposer;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.vertx.core.json.JsonObject;
import modules.Switchable;
import modules.TargetModule;
import modules.timer.TimerModule;

import java.lang.annotation.Target;

public class HeatService {

    private TimerModule timer;
    private Switchable switchable;
    private Disposable switchableSubscription;
    private TargetModule mode;

    public HeatService(TimerModule timer, Switchable switchable) {
        this.timer = timer;
        this.switchable = switchable;
        this.mode = TargetModule.OFF;
    }


    public Single<JsonObject> setMode(JsonObject bodyAsJson) {
        mode = TargetModule.valueOf(bodyAsJson.getString("mode"));

        return Single.just(new JsonObject().put("newMode", mode));
    }

    public TargetModule getMode() {
        return mode;
    }
}
