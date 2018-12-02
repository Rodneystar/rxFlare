import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import modules.TimerEvent;
import org.assertj.core.data.Offset;
import org.assertj.core.internal.bytebuddy.asm.Advice;
import org.junit.Test;

import java.awt.*;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class TimerEventTest {

    TestScheduler sched = new TestScheduler();

    @Test
    public void whenGetRxIntervalCalled_returnsIntervalObservableOfBooleanValues() {
        TimerEvent event1 = new TimerEvent(
                LocalTime.now().plus(1, ChronoUnit.HOURS),
                Duration.of(1, ChronoUnit.HOURS),
                sched);
        TestObserver test = event1.getRxInterval().map(e -> e.getShouldBeOn())
                .test();
        test.assertNoValues();

        sched.advanceTimeBy(60, TimeUnit.MINUTES);
        test.assertValue(true);

        sched.advanceTimeBy(60, TimeUnit.MINUTES);
        test.assertValues(true, false);

        sched.advanceTimeBy(22, TimeUnit.HOURS);
        test.assertValues(true, false);

        sched.advanceTimeBy(2, TimeUnit.HOURS);
        test.assertValues(true, false, true, false);
    }

    @Test
    public void givenEventOnSameDay_WhenGetMinsUntilStartIsCalled_thenReturnCorrectTime() {
        TimerEvent event1 =
                new TimerEvent(LocalTime.now().plus(156,
                        ChronoUnit.MINUTES), Duration.ofHours(1));
        assertThat(event1.getMinsUntilStart()).isCloseTo(156L, Offset.offset(1L));

    }

    @Test
    public void givenEventOnSameDayAgain_WhenGetMinsUntilStartIsCalled_thenReturnCorrectTime() {
        TimerEvent event1 =
                new TimerEvent(LocalTime.now().plus(623,
                        ChronoUnit.MINUTES), Duration.ofHours(1));
        assertThat(event1.getMinsUntilStart()).isCloseTo(623L, Offset.offset(1L));

    }
    @Test
    public void givenEventNextDay_WhenGetMinsUntilStartIsCalled_thenReturnCorrectTime() {
        TimerEvent event1 =
                new TimerEvent(LocalTime.now().minus(5, ChronoUnit.MINUTES),
                        Duration.ofMinutes(2));

        assertThat(event1.getMinsUntilStart()).isCloseTo(Duration.ofDays(1).toMinutes() - 5, Offset.offset(1L));

    }
    @Test
    public void given2OverlappingEvents_whenIsOverLappingCalled_ReturnsTrue() {
        TimerEvent event1 = new TimerEvent(LocalTime.now(), Duration.ofHours(1));
        TimerEvent event2 = new TimerEvent(
                LocalTime.now().plus(55, ChronoUnit.MINUTES),
                Duration.ofHours(1)
        );
        assertThat(event1.isOverlapping(event2)).isTrue();
    }

    @Test
    public void given2NonOverlappingEvents_whenIsOverLappingCalled_ReturnsFalse() {
        TimerEvent event1 = new TimerEvent(LocalTime.now(), Duration.ofHours(1));
        TimerEvent event2 = new TimerEvent(
                LocalTime.now().plus(2, ChronoUnit.HOURS),
                Duration.ofHours(1)
        );
        assertThat(event1.isOverlapping(event2)).isFalse();
    }

    @Test
    public void given2OverlappingEventsOverMidnight_whenIsOverLappingCalled_ReturnsTrue() {
        TimerEvent event1 = new TimerEvent(LocalTime.of(23,30), Duration.ofHours(1));
        TimerEvent event2 = new TimerEvent(
                LocalTime.of(0, 14),
                Duration.ofHours(1)
        );
        assertThat(event1.isOverlapping(event2)).isTrue();
    }

    @Test
    public void given1EventInsideAnotherOverMidnight_whenIsOverLappingCalled_ReturnsTrue() {
        TimerEvent event1 = new TimerEvent(LocalTime.of(23,30), Duration.ofHours(1));
        TimerEvent event2 = new TimerEvent(
                LocalTime.of(23, 14),
                Duration.ofHours(2)
        );
        assertThat(event1.isOverlapping(event2)).isTrue();
    }
}
