package edu.sc.seis.seisFile.mseed3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.BeforeClass;
import org.junit.Test;


public class LeapSecondTests {
    private static MSeed3Record totallyBefore;
    private static MSeed3Record crossLeapStart;
    private static MSeed3Record startOnLeap;
    private static MSeed3Record endOnLeap;
    private static MSeed3Record leapInMiddle;

    public static final int S_YEAR = 1995;
    public static final int S_DAY_OF_YEAR = 365;
    public static final int S_HOUR = 23;
    public static final int S_MIN = 59;
    public static final int S_SEC = 59;
    public static final int S_NANO = 123000000;
    
    @BeforeClass
    public static void setup() {
        // leap sec applied on 1995 Dec 31
        ZonedDateTime start = ZonedDateTime.of(S_YEAR, Month.DECEMBER.getValue(), 31, 23, 59, 59, 123000000, ZoneId.of("Z"));
        
        
        totallyBefore = new MSeed3Record();
        totallyBefore.setStartDateTime(start.minusSeconds(1));
        totallyBefore.setSampleRate(10);
        totallyBefore.setNumSamples(10);

        crossLeapStart = new MSeed3Record();
        crossLeapStart.setStartDateTime(start);
        crossLeapStart.setLeapSecInRecord(1);
        crossLeapStart.setSampleRate(10);
        crossLeapStart.setNumSamples(10);
        
        startOnLeap = new MSeed3Record();
        startOnLeap.setStartDateTime(start);
        startOnLeap.setSecond(60);
        startOnLeap.setLeapSecInRecord(1);
        startOnLeap.setSampleRate(10);
        startOnLeap.setNumSamples(10);
        
        endOnLeap = new MSeed3Record();
        endOnLeap.setStartDateTime(start);
        endOnLeap.setLeapSecInRecord(1);
        endOnLeap.setSampleRate(10);
        endOnLeap.setNumSamples(10);
        
        leapInMiddle = new MSeed3Record();
        leapInMiddle.setStartDateTime(start);
        leapInMiddle.setLeapSecInRecord(1);
        leapInMiddle.setSampleRate(10);
        leapInMiddle.setNumSamples(20);
    }

    @Test
    public void testGetStartTime() {
        MSeed3Record ms3 = new MSeed3Record();
        ms3.setYear(S_YEAR);
        ms3.setDayOfYear(S_DAY_OF_YEAR);
        ms3.setHour(S_HOUR);
        ms3.setMinute(S_MIN);
        ms3.setSecond(S_SEC);
        ms3.setNanosecond(S_NANO);
        ZonedDateTime start = ms3.getStartDateTime();
        assertEquals("year", S_YEAR, start.getYear());
        assertEquals("day of year", S_DAY_OF_YEAR, start.getDayOfYear());
        assertEquals("hour", S_HOUR, start.getHour());
        assertEquals("min", S_MIN, start.getMinute());
        assertEquals("sec", S_SEC, start.getSecond());
        assertEquals("nano", S_NANO, start.getNano());
    }

    @Test
    public void testGetLastSampleTime() {
        assertEquals("before", (totallyBefore.getNumSamples()-1)*totallyBefore.getSamplePeriodAsNanos(), totallyBefore.getStartDateTime().until(totallyBefore.getLastSampleTime(), ChronoUnit.NANOS));
        assertEquals("startOnLeap", (startOnLeap.getNumSamples()-1)*startOnLeap.getSamplePeriodAsNanos() , startOnLeap.getStartDateTime().until(startOnLeap.getLastSampleTime(), ChronoUnit.NANOS));
     // next 3 tests, leap second starts in record time range, so coverage interval will be off by 1 sec
        assertEquals("crossLeapStart start", 59, crossLeapStart.getLastSampleTime().getSecond());
        assertEquals("crossLeapStart leaps", 1, crossLeapStart.getLeapSecInRecord());
        assertEquals("crossLeapStart nanos", 23000000, crossLeapStart.getLastSampleTime().getNano());
        assertEquals("crossLeapStart", (crossLeapStart.getNumSamples()-1)*crossLeapStart.getSamplePeriodAsNanos() -SEC, crossLeapStart.getStartDateTime().until(crossLeapStart.getLastSampleTime(), ChronoUnit.NANOS));
        assertEquals("endOnLeap", (endOnLeap.getNumSamples()-1)*endOnLeap.getSamplePeriodAsNanos() -SEC, endOnLeap.getStartDateTime().until(endOnLeap.getLastSampleTime(), ChronoUnit.NANOS));
        assertEquals("leapInMiddle", (leapInMiddle.getNumSamples()-1)*leapInMiddle.getSamplePeriodAsNanos()  - SEC, leapInMiddle.getStartDateTime().until(leapInMiddle.getLastSampleTime(), ChronoUnit.NANOS));
    }

    @Test
    public void testGetStartTimeString() {
        assertEquals("before", "1995365T235958.123000000Z", totallyBefore.getStartTimeString());
        assertEquals("crossLeapStart", "1995365T235959.123000000Z", crossLeapStart.getStartTimeString());
        assertEquals("startOnLeap", "1995365T235960.123000000Z", startOnLeap.getStartTimeString());
        assertEquals("endOnLeap", "1995365T235959.123000000Z", endOnLeap.getStartTimeString());
        assertEquals("leapInMiddle", "1995365T235959.123000000Z", leapInMiddle.getStartTimeString());
    }

    @Test
    public void testGetLastSampleTimeString() {
        assertEquals("before", "1995365T235959.023000Z", totallyBefore.getLastSampleTimeString());
        assertEquals("crossLeapStart start", 59, crossLeapStart.getLastSampleTime().getSecond());
        assertEquals("crossLeapStart leaps", 1, crossLeapStart.getLeapSecInRecord());
        assertEquals("crossLeapStart nanos", 23000000, crossLeapStart.getLastSampleTime().getNano());
        assertEquals("crossLeapStart", "1995365T235960.023000Z", crossLeapStart.getLastSampleTimeString());
        System.out.println("before "+crossLeapStart.isStartTimeInLeapSecond()+" "+crossLeapStart.getLeapSecInRecord());
        assertEquals("startOnLeap", "1996001T000000.023000Z", startOnLeap.getLastSampleTimeString());
        System.out.println("after");
        assertEquals("endOnLeap", "1995365T235960.023000Z", endOnLeap.getLastSampleTimeString());
        assertTrue("leapInMiddle last sec", leapInMiddle.getSecond()+leapInMiddle.getSamplePeriodAsNanos() * (leapInMiddle.getNumSamples() - 1)/1000000000.0 < 61);
        assertEquals("leapInMiddle", "1996001T000000.023000Z", leapInMiddle.getLastSampleTimeString());
    }

    /* can't predict next starttime due to leap seconds
    @Test
    public void testGetPredictedNextStartTimeString() {
        assertEquals("before", "19951231T235959.123000", before.getPredictedNextStartTimeString());
        assertEquals("startOnLeap", "19960101T000000.123000", startOnLeap.getPredictedNextStartTimeString());
        assertEquals("endOnLeap", "19951231T235960.123000", endOnLeap.getPredictedNextStartTimeString());
        assertEquals("leapInMiddle", "19951231T235960.623000", leapInMiddle.getPredictedNextStartTimeString());
    }
    */

    @Test
    public void testIsStartTimeInLeapSecond() {
        assertFalse("totallyBefore", totallyBefore.isStartTimeInLeapSecond());
        assertFalse("crossLeapStart", crossLeapStart.isStartTimeInLeapSecond());
        assertTrue("startOnLeap", startOnLeap.isStartTimeInLeapSecond());
        assertFalse("endOnLeap", endOnLeap.isStartTimeInLeapSecond());
        assertFalse("leapInMiddle", leapInMiddle.isStartTimeInLeapSecond());
    }

    @Test
    public void testgetLeapSecInRecord() {
        assertEquals("totallyBefore", 0, totallyBefore.getLeapSecInRecord());
        assertEquals("crossLeapStart", 1, crossLeapStart.getLeapSecInRecord());
        assertEquals("startOnLeap", 1, startOnLeap.getLeapSecInRecord());
        assertEquals("endOnLeap", 1, endOnLeap.getLeapSecInRecord());
        assertEquals("leapInMiddle", 1, leapInMiddle.getLeapSecInRecord());
    }
    
    @Test
    public void testIsEndTimeInLeapSecond() {
        assertFalse("totallyBefore", totallyBefore.isEndTimeInLeapSecond());
        assertTrue("crossLeapStart", crossLeapStart.isEndTimeInLeapSecond());
        assertFalse("startOnLeap", startOnLeap.isEndTimeInLeapSecond());
        assertTrue("endOnLeap", endOnLeap.isEndTimeInLeapSecond());
        assertFalse("leapInMiddle", leapInMiddle.isEndTimeInLeapSecond());
        
    }
    
    public static final long SEC = 1000000000;
}
