package modules;

import modules.Switchable;

public class TestSwitchable implements Switchable {

    private Integer onCounter = 0;
    private Integer offCounter = 0;

    @Override
    public void on() {
        System.out.println("on MOFO");
        onCounter++;
    }

    @Override
    public void off() {
        System.out.println("Off MOFO");
        offCounter++;
    }

    public void reset() {
        onCounter = 0; offCounter = 0;
    }

    public Boolean onWasCalled() {
        return onCounter > 0;
    }

    public Boolean onCalledNTimes(Integer n) {
        return onCounter == n;
    }

    public Boolean offWasCalled() {
        return offCounter > 0;
    }

    public Boolean offCalledNTimes(Integer n) {
        return offCounter == n;
    }


}
