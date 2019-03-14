package modules.timer;

public class TimerOverLappingException extends RuntimeException {

    public TimerOverLappingException(String message) {
        super(message);
    }
}
