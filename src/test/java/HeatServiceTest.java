import io.reactivex.schedulers.TestScheduler;
import io.vertx.core.json.JsonObject;
import modules.Switchable;
import modules.TargetModule;
import modules.TestSwitchable;
import modules.timer.TimerModule;
import org.junit.Before;
import org.junit.Test;

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
    public void whenSetModeCalledWithOn_thenJsonObjectWithCorrectModeReturned() {
        JsonObject modeReq = new JsonObject("{\"mode\": \"ON\"}");

        service.setMode(modeReq)
                .test()
                .assertValue( json -> json.getString("newMode").equals("ON"));

//        assertThat( service.getMode() ).isEqualByComparingTo(TargetModule.ON);
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
