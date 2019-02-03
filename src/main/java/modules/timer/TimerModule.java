package modules.timer;

import io.reactivex.*;
import io.reactivex.subjects.PublishSubject;
import io.vertx.core.json.JsonObject;
import modules.SwitchEvent;

import java.time.Duration;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class TimerModule {

    private Scheduler scheduler;
    private TimerEventList timerEvents;
    private PublishSubject<Observable<SwitchEvent>> broadcastedEvents;

    public TimerModule(Scheduler sched) {
        this.scheduler = sched;
        timerEvents = new TimerEventList();
        broadcastedEvents = PublishSubject.create();
    }

    private void updateBroadcastedEvents() {
        broadcastedEvents.onNext(
                timerEvents.getRxIntervalForAllEvents().takeUntil(broadcastedEvents));
    }

    public Observable<SwitchEvent> observe() {
        updateBroadcastedEvents();
        return broadcastedEvents.flatMap(e -> e);
    }

    public void addTimer(TimerEvent event) {
        timerEvents.addEvent(event);
        updateBroadcastedEvents();
    }

    public void addTimer(LocalTime startTime, Duration duration) {
        addTimer(new TimerEvent(startTime, duration, scheduler));
    }

    public Observable<TimerEvent> getEvents() {
        return timerEvents.getAllEvents();
    }

    public void setScheduler(Scheduler sched) {
        this.scheduler = sched;
    }
}
