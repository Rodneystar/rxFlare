package modules;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.schedulers.TestScheduler;
import io.vertx.core.json.JsonObject;

import java.time.Duration;
import java.time.LocalTime;

public class TimerModule implements ReceivesHeatingInstruction {

    private final Scheduler scheduler;

    public TimerModule(Scheduler sched) {
        this.scheduler = sched;
    }

    @Override
    public Maybe<JsonObject> receiveInstruction(Single<JsonObject> instruction) {
        return instruction.filter( inst -> "timer".equals(inst.getString("targetModule" )));
//        return instruction.map( i -> i.getJsonObject("update"));
    }


    public Observable<SwitchEvent> observe() {
        return Observable.just(new SwitchEvent("timer", true));
    }

    public void addTimer(LocalTime startTime, Duration duration) {

    }
}
