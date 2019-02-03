import io.reactivex.schedulers.Schedulers;
import io.reactivex.schedulers.TestScheduler;
import modules.ModManager;
import modules.TargetModule;
import modules.TestSwitchable;
import modules.timer.TimerEvent;
import modules.timer.TimerModule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class ModManagerTest {
    TestSwitchable switchable;
    TimerModule timer;
    TestScheduler sched;

    ModManager manager;

    @Before
    public void setUp() {
         switchable = new TestSwitchable();
         sched = new TestScheduler();
         timer = new TimerModule(sched);
        manager = new ModManager(switchable, sched);
        manager.addModule(timer);
    }

    @After
    public void tearDown() {
        switchable.reset();
    }

    @Test
    public void whenModManagerCreatedWithSwitchable_thenOffOnSwitchableIsCalled() {
        sched.triggerActions();
        assertThat(switchable.offWasCalled()).isTrue();
    }

    @Test
    public void givenModManagerCreatedWithSwitchable_whenSetModCalledWithTimer_thenTimerWorksCalled() {
        manager.setActiveMod(TargetModule.HEATING_TIMER);
        timer.addTimer( LocalTime.now().plus(5, ChronoUnit.MINUTES),
                        Duration.of(5, ChronoUnit.MINUTES));
        sched.triggerActions();

        sched.advanceTimeBy(25, TimeUnit.MINUTES);

            assertThat(switchable.onWasCalled()).isTrue();
        sched.triggerActions();
            //            assertThat(switchable.onCalledNTimes(1)).isTrue();



    }


}
