package timerTest;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import modules.timer.TimerEvent;
import modules.timer.TimerEventList;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class TimerEventListTest {

    TimerEventList list;
    TestScheduler sched;

    @Before
    public void setUp() throws Exception {
        list = new TimerEventList();
        sched = new TestScheduler();
    }

    @Test
    public void whenGetIntervalForAllEvents_ObservableBehavesCorrectly() {
        list.addEvent(new TimerEvent(LocalTime.now().plus(5, ChronoUnit.MINUTES), Duration.ofMinutes(5), sched));
        list.addEvent(new TimerEvent(LocalTime.now().plus(15, ChronoUnit.MINUTES), Duration.ofMinutes(5), sched));

        TestObserver<Boolean> test = list.getRxIntervalForAllEvents().map( e-> e.desiresOn())
                .test();

        test.assertNoValues();

        sched.advanceTimeBy(5, TimeUnit.MINUTES);
        test.assertValues(true);
    }
    @Test
    public void givenEmptyTimerList_whenEventsAreAdded_thenGetEventsReturnsObservable() {
        Observable<TimerEvent> underTest = list.getAllEvents();

        underTest.test().assertNoValues();

        list.addEvent(new TimerEvent(LocalTime.of(12,0), Duration.ofHours(2)));
        underTest.test().assertValue( t -> t.getEndTime().getHour() == 14);
    }

    @Test
    public void givenEmptyTimerList_when2OverlappingEventsAreAdded_thenGetEventsReturnsObservableWithOneValue() {
        Observable<TimerEvent> underTest = list.getAllEvents();

        underTest.test().assertNoValues();

        list.addEvent(new TimerEvent(LocalTime.of(12,0), Duration.ofHours(2)));
        list.addEvent(new TimerEvent(LocalTime.of(13,56), Duration.ofHours(2)));
        underTest
                .map( e-> e.getEndTime().getHour())
                .test()
                .assertValue(14);

        list.addEvent(new TimerEvent(LocalTime.of(23,56), Duration.ofHours(17)));
        underTest
                .map( e-> e.getEndTime().getHour())
                .test()
                .assertValue(14);

        list.addEvent(new TimerEvent(LocalTime.of(23,56), Duration.ofHours(8)));
        underTest
                .map( e-> e.getStartTime().getHour())
                .test()
                .assertValues( 12, 23);
    }
}
