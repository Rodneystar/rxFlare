package timerTest;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static java.lang.System.*;

public class LearningTests {

    private void println(String theString) {
        System.out.println(theString);
    }
    @Test
    public void testInetAddress() throws UnknownHostException {
        println( InetAddress.getLocalHost().getCanonicalHostName());
    }
}
