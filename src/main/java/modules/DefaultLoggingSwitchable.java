package modules;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultLoggingSwitchable implements Switchable {

    Logger log = LoggerFactory.getLogger(DefaultLoggingSwitchable.class);
    @Override
    public void on() {
        log.info( "{} {}", "fucking on", "fucking On");
    }

    @Override
    public void off() {
        log.info("{} {}", "fucking off", "fucking off");
    }
}
