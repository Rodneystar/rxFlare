package modules;

public class SwitchEvent {

    TargetModule source;
    Boolean desiresTargetOn;

    public Boolean desiresOn() {
        return desiresTargetOn;
    }

    public TargetModule getSource() {
        return source;
    }

    public SwitchEvent(TargetModule source, boolean desiresTargetOn) {
        this.source = source; this.desiresTargetOn = desiresTargetOn;
    }
}
