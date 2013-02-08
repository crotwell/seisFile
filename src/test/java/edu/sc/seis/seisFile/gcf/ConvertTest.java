package edu.sc.seis.seisFile.gcf;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import org.junit.Test;

import edu.sc.seis.seisFile.earthworm.TraceBuf2;


public class ConvertTest {

    
    @Test
    public void testToTraceBuf() {

        boolean isSerial = true;
        int[] data = new int[200];
        for (int i = 0; i < data.length; i++) {
            data[i] = i;
            if (i % 10 > 4) {
                data[i] *= -1;
            }
        }
        int[] diffData = new int[data.length];
        for (int i = 1; i < data.length; i++) {
            diffData[i] = data[i]-data[i-1];
        }
        GCFBlock mock = GCFBlock.mockGCF(Convert.convertTime(0,  0).getTime(), diffData, isSerial);
        Map<String, String[]> sysId_StreamIdToSCNL = new HashMap<String, String[]>();
        sysId_StreamIdToSCNL.put(GCFBlock.MOCK_SYSID+"_"+GCFBlock.MOCK_STREAMID, new String[] {"TEST", "ENZ", "XX", "00"});  
        
        
        Convert convert = new Convert(sysId_StreamIdToSCNL);
        TraceBuf2 tb = convert.toTraceBuf(mock);
        assertEquals("start", mock.getHeader().getStartAsDate(), tb.getStartDate());
        assertEquals("end", mock.getHeader().getLastSampleTime(), tb.getEndDate());
        assertEquals("npts", mock.getHeader().getNumPoints(), tb.getNumSamples());
        assertEquals("sps", mock.getHeader().getSps(), tb.getSampleRate(), 0.0001);
        assertArrayEquals(data, tb.getIntData());
    }
    
    @Test
    public void testDateToDaySec() throws ParseException {
        TreeMap<String, int[]> toCheck = new TreeMap<String, int[]>();
        toCheck.put("1989-11-16 00:00:00", new int[] {-1, 0});
        toCheck.put("1989-11-17 00:00:00", new int[] {0, 0});
        toCheck.put("1989-11-17 00:00:01", new int[] {0, 1});
        toCheck.put("1989-11-17 00:01:00", new int[] {0, 60});
        toCheck.put("1989-11-17 13:00:00", new int[] {0, 13*3600});
        toCheck.put("1989-11-18 00:00:00", new int[] {1, 0});
        toCheck.put("1989-12-17 00:00:00", new int[] {30, 0});
        toCheck.put("1989-12-27 00:00:00", new int[] {40, 0});
        toCheck.put("1989-12-31 00:00:00", new int[] {44, 0});
        toCheck.put("1990-01-01 00:00:00", new int[] {45, 0});
        toCheck.put("1990-01-31 00:00:00", new int[] {75, 0});
        toCheck.put("1990-02-28 00:00:00", new int[] {103, 0});
        toCheck.put("1990-11-17 00:00:00", new int[] {365, 0});
        toCheck.put("1991-11-17 00:00:00", new int[] {365*2, 0});
        toCheck.put("1992-11-17 00:00:00", new int[] {365*3+1, 0});
        toCheck.put("1993-11-17 00:00:00", new int[] {365*4+1, 0});
        toCheck.put("1994-01-01 00:00:00", new int[] {45+365*4+1, 0});
        toCheck.put("1994-03-01 00:00:00", new int[] {45+365*4+1+31+28, 0});
        toCheck.put("1994-04-01 00:00:00", new int[] {45+365*4+1+31+28+31, 0});
        toCheck.put("1994-05-01 00:00:00", new int[] {45+365*4+1+31+28+31+30, 0});
        toCheck.put("1994-06-01 00:00:00", new int[] {45+365*4+1+31+28+31+30+31, 0});
        toCheck.put("1994-06-03 00:00:00", new int[] {45+365*4+1+31+28+31+30+31+2, 0});
        toCheck.put("1994-06-05 00:00:00", new int[] {45+365*4+1+31+28+31+30+31+4, 0});
        toCheck.put("1994-06-07 00:00:00", new int[] {45+365*4+1+31+28+31+30+31+6, 0});
        toCheck.put("1994-06-08 00:00:00", new int[] {45+365*4+1+31+28+31+30+31+7, 0});
        toCheck.put("1994-06-09 00:00:00", new int[] {45+365*4+1+31+28+31+30+31+8, 0});
        toCheck.put("1994-06-10 00:00:00", new int[] {45+365*4+1+31+28+31+30+31+9, 0});
        toCheck.put("1994-06-20 00:00:00", new int[] {45+365*4+1+31+28+31+30+31+19, 0});
        toCheck.put("1994-06-30 00:00:00", new int[] {45+365*4+1+31+28+31+30+31+29, 0});
        toCheck.put("1994-07-01 00:00:00", new int[] {45+365*4+1+31+28+31+30+31+30, 0});
        toCheck.put("1994-08-01 00:00:00", new int[] {45+365*4+1+31+28+31+30+31+30+31, 0});
        toCheck.put("1994-09-01 00:00:00", new int[] {45+365*4+1+31+28+31+30+31+30+31+31, 0});
        toCheck.put("1994-10-01 00:00:00", new int[] {45+365*4+1+31+28+31+30+31+30+31+31+30, 0});
        toCheck.put("1994-11-17 00:00:00", new int[] {365*5+1, 0});
        toCheck.put("1995-11-17 00:00:00", new int[] {365*6+1, 0});
        toCheck.put("1996-11-17 00:00:00", new int[] {365*7+2, 0}); //1992, 96 are leap year
        toCheck.put("1990-11-16 00:00:00", new int[] {364, 0});
        for (String dateStr : toCheck.navigableKeySet()) {
            checkDate(dateStr, toCheck.get(dateStr));
        }
    }
    
    void checkDate(String date, int[] daySecExpected) throws ParseException {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date d = sdf.parse(date);
        int[] daySec = Convert.convertTime(d);
        assertArrayEquals(date, daySecExpected, daySec);
        Calendar calcCal = Convert.convertTime(daySecExpected[0], daySecExpected[1]);
        if (daySecExpected[0] < 45) {
            // same year so easy check
            assertEquals(date, daySecExpected[0], calcCal.get(Calendar.DAY_OF_YEAR)-Convert.NOV_17_DAY_OF_YEAR);
        }
        assertEquals(date, daySecExpected[1], calcCal.get(Calendar.SECOND)+calcCal.get(Calendar.MINUTE)*60+calcCal.get(Calendar.HOUR_OF_DAY)*3600);
        Date calcDate = calcCal.getTime();
        assertEquals(date+" "+daySecExpected[0]+" "+daySecExpected[1], d, calcDate);
    }
}
