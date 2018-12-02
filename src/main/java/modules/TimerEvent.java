package modules;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TimerEvent {

    private LocalTime startTime;
    private Duration duration;
    private Scheduler sched;

    public TimerEvent( LocalTime start, Duration duration) {
        this(start,duration,Schedulers.io());
    }

    public TimerEvent(LocalTime start, Duration duration, Scheduler sched) {
        this.startTime = start;
        this.duration = duration;
        this.sched = sched;
    }

    public Observable<SwitchEvent> getRxInterval() {
        return Observable.merge(
            Observable.interval(getMinsUntilStart(),
                    Duration.ofDays(1).toMinutes(),
                    TimeUnit.MINUTES, sched)
                    .map( i -> new SwitchEvent(TargetModule.HEATING_TIMER, true)),
            Observable.interval(getMinsUntilStart() + duration.toMinutes(),
                    Duration.ofDays(1).toMinutes(),
                    TimeUnit.MINUTES, sched)
                    .map( i -> new SwitchEvent(TargetModule.HEATING_TIMER, false))
        );
    }
    public LocalTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public static Boolean isOverMidnight(TimerEvent event) {
        return event.getEndTime().isBefore(event.getStartTime());
    }

    public Boolean isOverMidnight() {
        return isOverMidnight(this);
    }

    public static Boolean areOverlapping(TimerEvent event1, TimerEvent event2){
        List<TimerEvent> list = Arrays.asList(event1,event2);
        list.sort(Comparator.comparing(timerEvent -> timerEvent.startTime));

        TimerEvent first = list.get(0); TimerEvent second = list.get(1);
        return (first.getEndTime().isAfter(second.getStartTime()) || first.isOverMidnight()) ||
                (second.getEndTime().isAfter(first.getStartTime()) && second.isOverMidnight());
    }

    public LocalTime getEndTime() {
        return startTime.plus(duration);
    }

    public Boolean isOverlapping(TimerEvent event) {
        return areOverlapping(this, event);
    }

    public long getMinsUntilStart() {
        long startTimeFromNow = LocalTime.now().until(this.startTime, ChronoUnit.MINUTES);
        return startTimeFromNow >= 0
                ? startTimeFromNow
                : startTimeFromNow + Duration.ofDays(1).toMinutes();

    }
}
