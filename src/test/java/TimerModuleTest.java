import com.sun.media.sound.SoftChorus;
import io.reactivex.Single;
import io.reactivex.schedulers.TestScheduler;
import modules.SwitchEvent;
import modules.TimerModule;
import org.junit.Before;
import org.junit.Test;
import io.vertx.core.json.JsonObject;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


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
    public void whenTimerTarget_thenReturnsFullMaybe() {
        timer.receiveInstruction(instruction).test()
                    .assertValue(Objects::nonNull);
    }
    @Test
    public void whenNotTimerTarget_thenReturnsEmptyMaybe() {
        instructionJson.put("targetModule", "notTimer");
        timer.receiveInstruction(instruction).test()
                .assertNoValues();
    }

    @Test
    public void whenObserveCalled_returnsIntervalObservableOfSwitchEvents() {
        timer.observe().test()
                .assertValue(Objects::nonNull);
    }

    @Test
    public void whenAddTimerCalled_getTimersReturnsObservableOfTimers() {
        LocalTime start = LocalTime.now().plus( 4, ChronoUnit.MINUTES);
        Duration duration = Duration.ofHours(2);
        timer.addTimer(start, duration);

        final List<SwitchEvent> eventList = new ArrayList<>();
        timer.observe().subscribe( e -> eventList.add())
                timer.observe().test()


    }
}
