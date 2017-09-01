package edu.sc.seis.seisFile;

import static org.junit.Assert.*;

import java.time.Duration;

import org.junit.Test;

public class TimeUtilsTest {

    @Test
    public void testDurationFromSeconds() {
        double seconds = 5.234;
        Duration d = TimeUtils.durationFromSeconds(seconds);
        assertEquals("seconds", Math.round(seconds), d.getSeconds());
        assertEquals("nanos", Math.round(seconds*TimeUtils.NANOS_IN_SEC), d.toNanos());
    }

    @Test
    public void testInstantFromEpochSeconds() {
        fail("Not yet implemented");
    }

}
