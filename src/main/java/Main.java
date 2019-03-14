import modules.Switchable;
import modules.TestSwitchable;
import modules.timer.TimerModule;

public class Main {



    public static void main(String[] args) {
        WebController controller = getController(
                getHeatService(getTimer(), getSwitchable())
        );

        controller.start();
    }

    private static TimerModule getTimer() {return new TimerModule();}
    private static HeatService getHeatService(TimerModule timer, Switchable s) {return new HeatService(timer, s);}
    private static Switchable getSwitchable() { return new TestSwitchable();}
    private static WebController getController(HeatService service) {return new WebController(service);}


}
