package modules.timer;

import io.reactivex.Observable;
import modules.SwitchEvent;

import java.util.ArrayList;
import java.util.List;

public class TimerEventList {

    private List<TimerEvent> eventList;
    private Observable<TimerEvent> observable;

    public TimerEventList() {
        eventList = new ArrayList();
        observable = Observable.fromIterable(eventList);
    }

    public void addEvent(TimerEvent timerEvent) {
        if (!isOverlappingAny(timerEvent)) eventList.add(timerEvent);
    }

    public Observable<SwitchEvent> getRxIntervalForAllEvents() {
        return this.getAllEvents()
                .flatMap(TimerEvent::getRxInterval);
    }

    private boolean isOverlappingAny(TimerEvent timerEvent) {
        Boolean qm = false;
        for (TimerEvent e : eventList) {
            qm = e.isOverlapping(timerEvent);
        }
        return qm;
    }

    public Observable<TimerEvent> getAllEvents() {
        return observable;
    }
}
