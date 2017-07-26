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
    
    @BeforeClass
    public static void setup() {
        // leap sec applied on 1995 Dec 31
        ZonedDateTime start = ZonedDateTime.of(1995, Month.DECEMBER.getValue(), 31, 23, 59, 59, 123000000, ZoneId.of("Z"));
        
        
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
    public void testGetLastSampleTime() {
        assertEquals("before", (totallyBefore.getNumSamples()-1)*totallyBefore.getSamplePeriodAsNanos(), totallyBefore.getStartDateTime().until(totallyBefore.getLastSampleTime(), ChronoUnit.NANOS));
        assertEquals("startOnLeap", (startOnLeap.getNumSamples()-1)*startOnLeap.getSamplePeriodAsNanos() , startOnLeap.getStartDateTime().until(startOnLeap.getLastSampleTime(), ChronoUnit.NANOS));
     // leap second starts in record time range, so coverage interval will be off by 1 sec
        assertEquals("crossLeapStart", (crossLeapStart.getNumSamples()-1)*crossLeapStart.getSamplePeriodAsNanos() -SEC, crossLeapStart.getStartDateTime().until(crossLeapStart.getLastSampleTime(), ChronoUnit.NANOS));
        assertEquals("endOnLeap", (endOnLeap.getNumSamples()-1)*endOnLeap.getSamplePeriodAsNanos() -SEC, endOnLeap.getStartDateTime().until(endOnLeap.getLastSampleTime(), ChronoUnit.NANOS));
        assertEquals("leapInMiddle", (leapInMiddle.getNumSamples()-1)*leapInMiddle.getSamplePeriodAsNanos()  - SEC, leapInMiddle.getStartDateTime().until(leapInMiddle.getLastSampleTime(), ChronoUnit.NANOS));
    }

    @Test
    public void testGetStartTimeString() {
        assertEquals("before", "19951231T235958.123000", totallyBefore.getStartTimeString());
        assertEquals("crossLeapStart", "19951231T235959.123000", crossLeapStart.getStartTimeString());
        assertEquals("startOnLeap", "19951231T235960.123000", startOnLeap.getStartTimeString());
        assertEquals("endOnLeap", "19951231T235959.123000", endOnLeap.getStartTimeString());
        assertEquals("leapInMiddle", "19951231T235959.123000", leapInMiddle.getStartTimeString());
    }

    @Test
    public void testGetLastSampleTimeString() {
        assertEquals("before", "19951231T235959.023000", totallyBefore.getLastSampleTimeString());
        assertEquals("crossLeapStart", "19951231T235960.023000", crossLeapStart.getLastSampleTimeString());
        System.out.println("before "+crossLeapStart.isStartTimeInLeapSecond()+" "+crossLeapStart.getLeapSecInRecord());
        assertEquals("startOnLeap", "19960101T000000.023000", startOnLeap.getLastSampleTimeString());
        System.out.println("after");
        assertEquals("endOnLeap", "19951231T235960.023000", endOnLeap.getLastSampleTimeString());
        assertEquals("leapInMiddle", "19960101T000000.023000", leapInMiddle.getLastSampleTimeString());
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
    
    public static final long SEC = 1000000;
}
