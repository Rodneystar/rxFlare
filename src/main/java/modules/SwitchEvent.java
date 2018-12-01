package modules;

public class SwitchEvent {

    String source;
    Boolean shouldBeOn;

    public SwitchEvent(String source, boolean shouldBeOn) {
        this.source = source; this.shouldBeOn = shouldBeOn;
    }
}
