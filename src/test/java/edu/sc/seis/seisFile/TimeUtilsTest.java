package edu.sc.seis.seisFile;

import java.time.Duration;
import java.time.Instant;

import org.junit.Assert;
import org.junit.ComparisonFailure;
import org.junit.Test;


public class TimeUtilsTest {
    
    @Test
    public void testParse() {
        TimeUtils.parseISOString("20170913T15:43:08.0000Z");
    }

    @Test
    public void testDurationFromSeconds() {
        double seconds = 5.234;
        Duration d = TimeUtils.durationFromSeconds(seconds);
        Assert.assertEquals("seconds", Math.round(seconds), d.getSeconds());
        Assert.assertEquals("nanos", Math.round(seconds*TimeUtils.NANOS_IN_SEC), d.toNanos());
    }

    @Test
    public void testInstantFromEpochSeconds() {
        Instant a = TimeUtils.parseISOString("2017-09-13T15:43:08.123Z");
        Instant b = TimeUtils.instantFromEpochSeconds(TimeUtils.instantToEpochSeconds(a));
        Assert.assertEquals(a.getEpochSecond(),b.getEpochSecond());
        Assert.assertEquals(a.getNano()/TimeUtils.NANOS_IN_SEC, b.getNano()/TimeUtils.NANOS_IN_SEC, 0.0001);
    }
    
    @Test
    public void testInstantToDoubleRT() {
        // now: 2017-09-01T18:58:02.673Z   1.504292282673E9    1504292282.673
        Instant now = Instant.now();
        double d = TimeUtils.instantToEpochSeconds(now);
        Instant rt = TimeUtils.instantFromEpochSeconds(d);
        System.out.println("now: "+now+"  "+d);
        assertEquals("to double round trip", now, rt, TimeUtils.ONE_MICROSECOND);
    }

    @Test
    public void testTheTestHelper() {
        Instant expected = TimeUtils.parseISOString("2017-09-01T18:34:08.865000000Z");
        Instant actual = TimeUtils.parseISOString("2017-09-01T18:34:08.864900000Z");
        Duration error = Duration.ofNanos(100000);
        assertEquals("check within tenthmilli", expected, actual, error);
    }
    
    
    static public void assertEquals(String message, Instant expected,
                                    Instant actual, Duration error) {
        if (expected == null && actual == null) {
            return;
        } else if (Duration.between(expected, actual).abs().compareTo(error) > 0) {
            String cleanMessage = message == null ? "" : message;
            throw new ComparisonFailure(cleanMessage+" greater than "+error, expected.toString(),
                                         actual.toString());
        }
    }
    


    static String format(String message, Object expected, Object actual) {
        String formatted = "";
        if (message != null && !message.equals("")) {
            formatted = message + " ";
        }
        String expectedString = String.valueOf(expected);
        String actualString = String.valueOf(actual);
        return formatted + "expected:<" + expectedString + "> but was:<"+ actualString + ">";
    }
}
