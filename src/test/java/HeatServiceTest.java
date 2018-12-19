import io.reactivex.schedulers.TestScheduler;
import io.vertx.core.json.JsonObject;
import modules.Switchable;
import modules.TargetModule;
import modules.TestSwitchable;
import modules.timer.TimerModule;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

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
