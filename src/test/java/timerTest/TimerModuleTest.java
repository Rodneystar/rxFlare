package timerTest;

import io.reactivex.Single;
import io.reactivex.internal.operators.observable.ObservableFlatMap;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import io.vertx.core.json.JsonObject;
import modules.timer.TimerEvent;
import modules.timer.TimerModule;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class TimerModuleTest {

    TestScheduler sched = new TestScheduler();
    TimerModule timer;
    JsonObject instructionJson;
    Single<JsonObject> instruction;

    @Before
    public void setup() {
        instructionJson = new JsonObject();
        instructionJson.put("targetModule", "timer");
        instruction = Single.just(instructionJson);
        timer = new TimerModule(sched);
    }

    @Test
    public void whenObserveCalled_returnsPublishSubjectOfSwitchEvent() {
        assertThat(timer.observe()).isInstanceOf(ObservableFlatMap.class);
    }

    @Test
    public void whenObserveCalled_returnsIntervalObservableOfSwitchEvents() throws InterruptedException {
        TestObserver<Boolean> test = timer.observe().map(e -> e.desiresOn()).test();

        test.assertNoValues();
    }

    @Test
    public void whenObserveCalled_returnsIntervalObservableOfSwitchEventsMANUALTEST() throws InterruptedException {
        TestObserver<Boolean> test = timer.observe().map(e -> e.desiresOn()).test();
        timer.addTimer(new TimerEvent(LocalTime.now().plus(3, ChronoUnit.MINUTES),
                Duration.ofMinutes(2), sched));

        test.assertNoValues();

        sched.advanceTimeBy(3, TimeUnit.MINUTES);
        test.assertValues(true);

        sched.advanceTimeBy(2, TimeUnit.MINUTES);
        test.assertValues(true, false);

        timer.addTimer(new TimerEvent(LocalTime.now().plus(6, ChronoUnit.MINUTES),
                        Duration.ofMinutes(2),sched ));

        test.assertValues(true, false);

        sched.advanceTimeBy(2, TimeUnit.MINUTES);
        test.assertValues(true, false, true );

        sched.advanceTimeBy(2, TimeUnit.MINUTES);
        test.assertValues(true, false, true, false );
    }

    @Test
    public void whenAddTimerCalled_getTimersReturnsObservableOfTimers() {
        LocalTime start = LocalTime.now().plus( 4, ChronoUnit.MINUTES);
        Duration duration = Duration.ofHours(2);
        timer.addTimer(start, duration);

        timer.getEvents()
                .map( e -> e.getDuration().toMinutes())
                .test()
                .assertValue(120L);
    }
}
