package edu.sc.seis.seisFile.syncFile;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.junit.Test;

import edu.sc.seis.seisFile.SeisFileException;


public class SyncFileTest {

    @Test
    public void testParse() throws SeisFileException, ParseException {
        SyncLine sl = SyncLine.parse("CO|JSC|00|BHZ|2010,243,08:00:05|2010,243,08:00:13|0.0|100.0|||||||");
        sl = SyncLine.parse("IU|ANMO|01|BHE|1994,258,00:00:00|1994,265,00:00:00|.0005|20||CG|||||1998,275||");
        sl = SyncLine.parse("IU|ANMO|01|BHE|1994,265,00:00:00|1994,275,00:00:00|.0005|20||CG|||||1998,275||");
        DateFormat dateFormat = new SimpleDateFormat("yyyy,DDD,HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        assertEquals(sl.startTime, dateFormat.parse("1994,265,00:00:00"));
        sl = SyncLine.parse("IU|ANMO|01|BHE|1994,265,00:00:00.000|1994,275,00:00:00|.0005|20||CG|||||1998,275||");
        assertEquals(sl.startTime, dateFormat.parse("1994,265,00:00:00"));
        DateFormat dayOnlyDateFormat = new SimpleDateFormat("yyyy,DDD");
        dayOnlyDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        assertEquals(sl.lineModByDCC, dayOnlyDateFormat.parse("1998,275"));
        
    }
    
    @Test
    public void testConcat() throws SeisFileException {
        SyncLine first = SyncLine.parse("CO|JSC|00|BHZ|2010,243,08:00:05|2010,243,08:00:13|0.0|100.0|||||||");
        SyncLine second = SyncLine.parse("CO|JSC|00|BHZ|2010,243,08:00:13|2010,243,08:00:21|0.0|100.0|||||||");
        System.out.println(first.endTime.getTime()+"  "+second.startTime.getTime());
        assertTrue("are contiguous", first.isContiguous(second, 0.01f));
        SyncLine joined = first.concat(second);
        assertEquals(first.startTime, joined.startTime);
        assertEquals(second.endTime, joined.endTime);
    }
}
