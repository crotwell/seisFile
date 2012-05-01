package edu.sc.seis.seisFile.syncFile;

import static org.junit.Assert.*;

import java.text.ParseException;

import org.junit.Test;

import edu.sc.seis.seisFile.SeisFileException;


public class SyncLineTest {

    @Test
    public void testParse() throws SeisFileException {
        SyncLine sl = SyncLine.parse("CO|JSC|00|HHZ|2012,001,00:00:01.000|2012,008,14:30:07.780||100.0|||||||");
        assertEquals("toString output", "CO|JSC|00|HHZ|2012,001,00:00:01.000|2012,008,14:30:07.780||100.0|||||||", sl.toString());
    }

    @Test
    public void testSyncLineStringStringStringStringDateDateFloatFloat() throws ParseException {
        String net = "CO";
        String sta = "JSC";
        String loc = "00";
        String chan = "HHZ";
        String start = "2012,001,00:00:01.000";
        String end = "2012,011,20:00:01.000";
        SyncLine sl = new SyncLine(net, sta, loc, chan, SyncLine.stringToDate(start), SyncLine.stringToDate(end), 0f, 1f);
        assertEquals("net", net, sl.getNet());
        assertEquals("sta", sta, sl.getSta());
        assertEquals("loc", loc, sl.getLoc());
        assertEquals("chan", chan, sl.getChan());
        assertEquals("start", start, sl.dateToString(sl.getStartTime()));
        assertEquals("end", end, sl.dateToString(sl.getEndTime()));
    }
}
