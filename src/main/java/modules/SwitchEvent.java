package modules;

public class SwitchEvent {

    TargetModule source;
    Boolean shouldBeOn;

    public Boolean getShouldBeOn() {
        return shouldBeOn;
    }

    public TargetModule getSource() {
        return source;
    }

    public SwitchEvent(TargetModule source, boolean shouldBeOn) {
        this.source = source; this.shouldBeOn = shouldBeOn;
    }
}
