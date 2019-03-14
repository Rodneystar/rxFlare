import com.fasterxml.jackson.core.JsonParseException;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import modules.Switchable;
import modules.TargetModule;
import modules.TestSwitchable;
import modules.timer.TimerEvent;
import modules.timer.TimerModule;
import org.assertj.core.internal.bytebuddy.asm.Advice;
import org.junit.Before;
import org.junit.Test;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
public class HeatServiceTest {

    TimerModule timer;
    TestScheduler sched;
    TestSwitchable switchable;


    HeatService service;

    @Before
    public void setup() {
        switchable = new TestSwitchable();
        sched = new TestScheduler();
        timer = new TimerModule(sched);

        service = new HeatService(timer, switchable);
    }


    @Test
    public void getTimerList_oneTimerAdded_returnsObservableofOne() {
        timer.addTimer(LocalTime.parse("21:30"), Duration.of(1, ChronoUnit.HOURS));
        service.getTimerList()
                .test()
                .assertValueCount(1)
                .assertValueAt(0, event ->
                        event.getDuration().toHours() == 1);
    }

    @Test
    public void timersToJson_listOfOne_returnsJsonObject() {
        TimerEvent timerEvent = new TimerEvent(LocalTime.parse("21:30"), Duration.of(1, ChronoUnit.HOURS));
        ArrayList list = new ArrayList();
        list.add(0, timerEvent);
        Observable observableList = Observable.fromIterable(list);

        JsonArray expected = new JsonArray(list);

        JsonArray result = service.timersToJson(observableList);

        assertEquals(
                expected.getJsonObject(0).getString("startTime"),
                result.getJsonObject(0).getString("startTime"));
    }

    @Test
    public void removeEvent_calledWithBadIndex_indexOOBException() {
        timer.addTimer(LocalTime.parse("21:30"), Duration.of(1, ChronoUnit.HOURS));
        Single<JsonObject> result = service.removeTimer(new JsonObject("{\"index\": 1}"));

        result.test().assertError(IndexOutOfBoundsException.class);

    }

    @Test
    public void reMoveEvent_called_eventCount0AndSwitchableCalled0Times() {
        timer.addTimer(LocalTime.parse("21:30"), Duration.of(1, ChronoUnit.HOURS));
        timer.getEvents().test().assertValueCount(1);
        service.setMode(new JsonObject("{\"mode\": \"HEATING_TIMER\"}"));
        sched.advanceTimeBy(24, TimeUnit.HOURS);

        assertEquals( switchable.timesOnCalled(), Integer.valueOf(1));

        switchable.reset();
        timer.removeTimer(0);
        sched.advanceTimeBy(24, TimeUnit.HOURS);

        timer.getEvents().test().assertValueCount(0);
        assertEquals( switchable.timesOnCalled(), Integer.valueOf(0));
    }

    @Test
    public void addTimer_badJson_observableError() {
        Single<JsonObject> result = service.addTimer(new JsonObject("{\"start\": \"13:30\", \"duion\": 60 }"));
        result.test().assertError(Exception.class );

    }


    @Test
    public void addTimer_startAndDuration_timerIsAdded() {
        Single<JsonObject> result = service.addTimer(new JsonObject("{\"start\": \"13:30\", \"duration\": 60 }"));
        result.test().assertValue(json -> json.containsKey("created"));
        timer.getEvents()
                .test()
                .assertSubscribed()
                .assertValue( v -> v.getDuration().toMinutes() == 60);
    }

    @Test
    public void modeChangedFromTimedToOff_timeElapses_switchableStaysOff() {
//        service = new HeatService(timer, switchable);
        service.setMode(new JsonObject("{\"mode\": \"HEATING_TIMER\"}"));
        service.setMode(new JsonObject("{\"mode\": \"OFF\"}"));

        timer.addTimer(LocalTime.now().plus(2, ChronoUnit.MINUTES), Duration.ofMinutes(5));

        sched.advanceTimeBy(4, TimeUnit.MINUTES);

        assertThat( switchable.onWasCalled() ).isFalse();
    }

    @Test
    public void modeChangedFromTimedToOn_timeElapses_switchableStaysOn() {
        service = new HeatService(timer, switchable);
        service.setMode(new JsonObject("{\"mode\": \"HEATING_TIMER\"}"));
        service.setMode(new JsonObject("{\"mode\": \"ON\"}"));
        switchable.reset();
        timer.addTimer(LocalTime.now().plus(2, ChronoUnit.MINUTES), Duration.ofMinutes(5));

        sched.advanceTimeBy(22, TimeUnit.MINUTES);

        assertThat( switchable.offWasCalled() ).isFalse();
    }

    @Test
    public void testingTest() {

        timer.addTimer(LocalTime.now().plus(10, ChronoUnit.MINUTES), Duration.ofMinutes(10));
        timer.addTimer(LocalTime.now().plus(50, ChronoUnit.MINUTES), Duration.ofMinutes(10));

        timer.observe().subscribe( e -> System.out.println("event: " + e.desiresOn()));
        sched.advanceTimeBy(1, TimeUnit.DAYS);

    }
    @Test
    public void modeChangedFromTimedToOnAndBackToTimed_timeElapses_switchableStaysOn() {
//        service = new HeatService(timer, switchable);
        service.setMode(new JsonObject("{\"mode\": \"HEATING_TIMER\"}"));
        service.setMode(new JsonObject("{\"mode\": \"ON\"}"));
        switchable.reset();
        timer.addTimer(LocalTime.now().plus(10, ChronoUnit.MINUTES), Duration.ofMinutes(10));
        service.setMode(new JsonObject("{\"mode\": \"HEATING_TIMER\"}"));


        sched.advanceTimeBy(2, TimeUnit.DAYS);

        assertThat( switchable.timesOnCalled()).isEqualTo(2);
        assertThat( switchable.timesOffCalled()).isEqualTo(2);
    }


    @Test
    public void whenSetModeCalledWithOn_thenModeIsCorrect() {
        JsonObject modeReq = new JsonObject("{\"mode\": \"ON\"}");

        service.setMode(modeReq);

        assertThat( service.getMode() ).isEqualByComparingTo(TargetModule.ON);
    }

    @Test
    public void whenSetModeToTimer_thenDisposableAddedToComposite() {
        JsonObject modeReq = new JsonObject("{\"mode\": \"HEATING_TIMER\"}");
        service.setMode(modeReq);
        timer.addTimer(LocalTime.now().plus(30, ChronoUnit.MINUTES), Duration.of(30, ChronoUnit.MINUTES));

        sched.advanceTimeBy(35, TimeUnit.MINUTES);

        assertThat(switchable.onWasCalled()).isTrue();

        sched.advanceTimeBy(30, TimeUnit.MINUTES);
        assertThat(switchable.offWasCalled()).isTrue();
    }

    @Test
    public void whenSetModeCalledWithOn_thenJsonObjectWithCorrectModeReturned() {
        JsonObject modeReq = new JsonObject("{\"mode\": \"ON\"}");

        service.setMode(modeReq)
                .test()
                .assertValue( json -> json.getString("newMode").equals("ON"));
    }

    @Test
    public void whenSetModeCalledWithTimer_thenJsonObjectWithCorrectModeReturned() {
        JsonObject modeReq = new JsonObject("{\"mode\": \"HEATING_TIMER\"}");

        service.setMode(modeReq)
                .test()
                .assertValue( json -> json.getString("newMode").equals("HEATING_TIMER"));
    }

    @Test
    public void whenSetModeCalledWithInvalidMode_thenModeIsUnchanged() {
        JsonObject modeReq = new JsonObject("{\"mode\": \"ONGER\"}");

        try {
            service.setMode(modeReq);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        assertThat( service.getMode() ).isEqualByComparingTo(TargetModule.OFF);
    }
}
