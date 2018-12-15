import io.reactivex.Observable;
import io.reactivex.schedulers.TestScheduler;
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



    }

    @Test
    public void givenModManagerCreatedWithSwitchable_thenOffOnSwitchableIsCalled() {
        manager = new ModManager(switchable, sched);
//        manager.addModule(timer);
        sched.advanceTimeBy(10, TimeUnit.SECONDS);
        sched.triggerActions();
        assertThat(switchable.offWasCalled()).isTrue();
    }


    @Test
    public void givenModManagerCreatedWithSwitchable_whenGetModListCalled_thenResultingObservableHas3Emmissions() {
        manager.addModule(timer);

        sched.triggerActions();
        Observable<ReceivesHeatingInstruction> result = manager.getModList();

        result.subscribe( e -> System.out.println(e));

    }


    @Test
    public void test2() {
        manager.addModule(timer);
        manager.setActiveMod(TargetModule.HEATING_TIMER);

        timer.addTimer(LocalTime.now().plus(6, ChronoUnit.MINUTES),
                Duration.of(6, ChronoUnit.MINUTES));

        sched.advanceTimeBy(7, TimeUnit.MINUTES);

        System.out.println(sched.now(TimeUnit.MINUTES));

        assertThat(switchable.onWasCalled()).isTrue();


    }
}
