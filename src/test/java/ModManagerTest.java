import io.reactivex.schedulers.TestScheduler;
import modules.ModManager;
import modules.TestSwitchable;
import modules.timer.TimerModule;
import org.junit.Before;
import org.junit.Test;

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

    @Test
    public void givenModManagerCreatedWithSwitchable_thenOffOnSwitchableIsCalled() {
        sched.triggerActions();
        assertThat(switchable.offWasCalled()).isTrue();
    }
}
