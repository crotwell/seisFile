package edu.sc.seis.seisFile;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;


public class QueryParamsTest {

    @Test
    public void test() throws Exception {
        String[] testDates = new String[] {"2012-07-18T06:00:00.000GMT", "2012-07-18T06:00:00.000Z", "2012-07-18T06:00:00.000", "2012-07-18T06:00:00"};
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz");
        Date d = dateFormat.parse(testDates[0]);
        QueryParams qp = new QueryParams(new String[0]);
        for (int i = 0; i < testDates.length; i++) {
            Date testDate = qp.extractDate(testDates[i]);
            assertEquals(i+" "+testDates[i], d, testDate);
        }
        // check with no time in date string
        String onlyDate = "2012-07-18";
        d = dateFormat.parse(onlyDate+"T00:00:00.000GMT");
        Date testDate = qp.extractDate(onlyDate);
        assertEquals("no time "+onlyDate, d, testDate);
    }
}
