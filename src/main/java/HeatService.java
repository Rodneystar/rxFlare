import modules.Switchable;
import modules.timer.TimerModule;

public class HeatService {

    private TimerModule timer;
    private Switchable switchable;

    public HeatService(TimerModule timer, Switchable switchable) {
        this.timer = timer;
        this.switchable = switchable;
    }
}
