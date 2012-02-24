package edu.sc.seis.seisFile.winston;

import static org.junit.Assert.*;
import java.util.Date;

import org.junit.Test;


public class WinstonUtilTest {

    @Test
    public void testSimpleJ2K() {
        Date d = WinstonUtil.j2KSecondsToDate(-1*WinstonUtil.Y1970_TO_Y2000_SECONDS);
        assertEquals(0, d.getTime());
        
        Date d1970 = new Date(0);
        assertEquals(-1* WinstonUtil.Y1970_TO_Y2000_SECONDS, WinstonUtil.dateToJ2kSeconds(d1970));
    }

    @Test
    public void testRoundTrip() {
        long[] testVals = new long[] {0, -1, 1, 2, -123, 9000000, 65499023356l, };
        for (long l : testVals) {
            assertEquals("test round trip: ", l, WinstonUtil.dateToJ2kSeconds(WinstonUtil.j2KSecondsToDate(l)));
        }
    }
}
