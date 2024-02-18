package edu.sc.seis.seisFile.mseed3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeAll;


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

    @BeforeAll
    public static void setup() {
        // leap sec applied on 1995 Dec 31
        ZonedDateTime start = ZonedDateTime.of(S_YEAR, Month.DECEMBER.getValue(), 31, 23, 59, 59, 123000000, ZoneId.of("Z"));


        totallyBefore = new MSeed3Record();
        totallyBefore.setStartDateTime(start.minusSeconds(1));
        totallyBefore.setSampleRatePeriod(10);
        totallyBefore.setNumSamples(10);

        crossLeapStart = new MSeed3Record();
        crossLeapStart.setStartDateTime(start);
        crossLeapStart.setLeapSecInRecord(1);
        crossLeapStart.setSampleRatePeriod(10);
        crossLeapStart.setNumSamples(10);

        startOnLeap = new MSeed3Record();
        startOnLeap.setStartDateTime(start);
        startOnLeap.setSecond(60);
        startOnLeap.setLeapSecInRecord(1);
        startOnLeap.setSampleRatePeriod(10);
        startOnLeap.setNumSamples(10);

        endOnLeap = new MSeed3Record();
        endOnLeap.setStartDateTime(start);
        endOnLeap.setLeapSecInRecord(1);
        endOnLeap.setSampleRatePeriod(10);
        endOnLeap.setNumSamples(10);

        leapInMiddle = new MSeed3Record();
        leapInMiddle.setStartDateTime(start);
        leapInMiddle.setLeapSecInRecord(1);
        leapInMiddle.setSampleRatePeriod(10);
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
        assertEquals(S_YEAR, start.getYear());
        assertEquals(S_DAY_OF_YEAR, start.getDayOfYear());
        assertEquals(S_HOUR, start.getHour());
        assertEquals(S_MIN, start.getMinute());
        assertEquals(S_SEC, start.getSecond());
        assertEquals(S_NANO, start.getNano());
    }

    @Test
    public void testGetLastSampleTime() {
        assertEquals( (totallyBefore.getNumSamples()-1)*totallyBefore.getSamplePeriodAsNanos(), totallyBefore.getStartDateTime().until(totallyBefore.getLastSampleTime(), ChronoUnit.NANOS));
        assertEquals( (startOnLeap.getNumSamples()-1)*startOnLeap.getSamplePeriodAsNanos() , startOnLeap.getStartDateTime().until(startOnLeap.getLastSampleTime(), ChronoUnit.NANOS));
     // next 3 tests, leap second starts in record time range, so coverage interval will be off by 1 sec
        assertEquals( 59, crossLeapStart.getLastSampleTime().getSecond());
        assertEquals( 1, crossLeapStart.getLeapSecInRecord());
        assertEquals( 23000000, crossLeapStart.getLastSampleTime().getNano());
        assertEquals( (crossLeapStart.getNumSamples()-1)*crossLeapStart.getSamplePeriodAsNanos() -SEC, crossLeapStart.getStartDateTime().until(crossLeapStart.getLastSampleTime(), ChronoUnit.NANOS));
        assertEquals( (endOnLeap.getNumSamples()-1)*endOnLeap.getSamplePeriodAsNanos() -SEC, endOnLeap.getStartDateTime().until(endOnLeap.getLastSampleTime(), ChronoUnit.NANOS));
        assertEquals( (leapInMiddle.getNumSamples()-1)*leapInMiddle.getSamplePeriodAsNanos()  - SEC, leapInMiddle.getStartDateTime().until(leapInMiddle.getLastSampleTime(), ChronoUnit.NANOS));
    }

    @Test
    public void testGetStartTimeString() {
        assertEquals( "1995-365T23:59:58.123Z", totallyBefore.getStartTimeString());
        assertEquals( "1995-365T23:59:59.123Z", crossLeapStart.getStartTimeString());
        assertEquals( "1995-365T23:59:60.123Z", startOnLeap.getStartTimeString());
        assertEquals( "1995-365T23:59:59.123Z", endOnLeap.getStartTimeString());
        assertEquals( "1995-365T23:59:59.123Z", leapInMiddle.getStartTimeString());
    }

    @Test
    public void testGetLastSampleTimeString() {
        assertEquals("1995-365T23:59:59.023Z", totallyBefore.getLastSampleTimeString());
        assertEquals( 59, crossLeapStart.getLastSampleTime().getSecond());
        assertEquals( 1, crossLeapStart.getLeapSecInRecord());
        assertEquals( 23000000, crossLeapStart.getLastSampleTime().getNano());
        assertEquals( "1995-365T23:59:60.023Z", crossLeapStart.getLastSampleTimeString());
        System.out.println("before "+crossLeapStart.isStartTimeInLeapSecond()+" "+crossLeapStart.getLeapSecInRecord());
        assertEquals( "1996-001T00:00:00.023Z", startOnLeap.getLastSampleTimeString());
        System.out.println("after");
        assertEquals("1995-365T23:59:60.023Z", endOnLeap.getLastSampleTimeString());
        assertTrue( leapInMiddle.getSecond()+leapInMiddle.getSamplePeriodAsNanos() * (leapInMiddle.getNumSamples() - 1)/1000000000.0 < 61);
        assertEquals( "1996-001T00:00:00.023Z", leapInMiddle.getLastSampleTimeString());
    }

    /* can't predict next starttime due to leap seconds
    @Test
    public void testGetPredictedNextStartTimeString() {
        assertEquals("before", "1995-1231T23:59:59.123000", before.getPredictedNextStartTimeString());
        assertEquals("startOnLeap", "1996-0101T00:00:00.123000", startOnLeap.getPredictedNextStartTimeString());
        assertEquals("endOnLeap", "1995-1231T23:59:60.123000", endOnLeap.getPredictedNextStartTimeString());
        assertEquals("leapInMiddle", "1995-1231T23:59:60.623000", leapInMiddle.getPredictedNextStartTimeString());
    }
    */

    @Test
    public void testIsStartTimeInLeapSecond() {
        assertFalse( totallyBefore.isStartTimeInLeapSecond());
        assertFalse( crossLeapStart.isStartTimeInLeapSecond());
        assertTrue( startOnLeap.isStartTimeInLeapSecond());
        assertFalse( endOnLeap.isStartTimeInLeapSecond());
        assertFalse( leapInMiddle.isStartTimeInLeapSecond());
    }

    @Test
    public void testgetLeapSecInRecord() {
        assertEquals( 0, totallyBefore.getLeapSecInRecord());
        assertEquals( 1, crossLeapStart.getLeapSecInRecord());
        assertEquals(1, startOnLeap.getLeapSecInRecord());
        assertEquals( 1, endOnLeap.getLeapSecInRecord());
        assertEquals( 1, leapInMiddle.getLeapSecInRecord());
    }

    @Test
    public void testIsEndTimeInLeapSecond() {
        assertFalse(totallyBefore.isEndTimeInLeapSecond());
        assertTrue( crossLeapStart.isEndTimeInLeapSecond());
        assertFalse( startOnLeap.isEndTimeInLeapSecond());
        assertTrue( endOnLeap.isEndTimeInLeapSecond());
        assertFalse( leapInMiddle.isEndTimeInLeapSecond());

    }

    public static final long SEC = 1000000000;
}
