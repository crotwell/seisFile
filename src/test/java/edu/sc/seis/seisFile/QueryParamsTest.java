package edu.sc.seis.seisFile;

import static org.junit.Assert.*;

import java.time.Instant;

import org.junit.Test;


public class QueryParamsTest {

    @Test
    public void testDates() throws Exception {
        String[] testDates = new String[] {"2012-07-18T06:00:00.000GMT", "2012-07-18T06:00:00.000Z", "2012-07-18T06:00:00.000", "2012-07-18T06:00:00"};
        Instant d = TimeUtils.parseISOString(testDates[0]);
        QueryParams qp = new QueryParams(new String[0]);
        for (int i = 0; i < testDates.length; i++) {
            Instant testDate = qp.extractDate(testDates[i]);
            assertEquals(i+" "+testDates[i], d, testDate);
        }
        // check with no time in date string
        String onlyDate = "2012-07-18";
        d = TimeUtils.parseISOString(onlyDate+"T00:00:00.000GMT");
        Instant testDate = qp.extractDate(onlyDate);
        assertEquals("no time "+onlyDate, d, testDate);
    }
}
