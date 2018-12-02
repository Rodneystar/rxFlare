package modules;

import io.reactivex.*;
import io.reactivex.subjects.PublishSubject;
import io.vertx.core.json.JsonObject;

import java.time.Duration;
import java.time.LocalTime;

public class TimerModule implements ReceivesHeatingInstruction {

    private Scheduler scheduler;
    private TimerEventList timerEvents;
    private PublishSubject<Observable<SwitchEvent>> broadcastedEvents;

    public TimerModule(Scheduler sched) {
        this.scheduler = sched;
        timerEvents = new TimerEventList();
        broadcastedEvents = PublishSubject.create();
    }

    @Override
    public Maybe<JsonObject> receiveInstruction(Single<JsonObject> instruction) {
        return instruction.filter( inst -> "timer".equals(inst.getString("targetModule" )));
    }

    private void updateBroadcastedEvents() {
        broadcastedEvents.onNext(
                timerEvents.getRxIntervalForAllEvents().takeUntil(broadcastedEvents));
    }

    public Observable<SwitchEvent> observe() {
        return broadcastedEvents.flatMap( e -> e);
    }

    public void addTimer(TimerEvent event) {
        timerEvents.addEvent(event);
        updateBroadcastedEvents();
    }

    public void addTimer(LocalTime startTime, Duration duration) {
        addTimer(new TimerEvent(startTime, duration, scheduler));
        updateBroadcastedEvents();
    }

    public Observable<TimerEvent> getEvents() {
        return timerEvents.getAllEvents();
    }

    public void setScheduler(Scheduler sched) {
        this.scheduler = sched;
    }
}
