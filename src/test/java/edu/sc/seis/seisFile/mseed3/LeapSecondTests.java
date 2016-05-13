package edu.sc.seis.seisFile.mseed3;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.BeforeClass;
import org.junit.Test;


public class LeapSecondTests {
    private static MSeed3Header totallyBefore;
    private static MSeed3Header crossLeapStart;
    private static MSeed3Header startOnLeap;
    private static MSeed3Header endOnLeap;
    private static MSeed3Header leapInMiddle;
    
    @BeforeClass
    public static void setup() {
        // leap sec applied on 1995 Dec 31
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(1995, Calendar.DECEMBER, 31, 23, 59, 59);
        cal.set(Calendar.MILLISECOND, 123);
        long micros = 1000 * (cal.getTimeInMillis());
        
        totallyBefore = new MSeed3Header();
        totallyBefore.setStartTime(micros-1000000);
        totallyBefore.setSampleRate(10);
        totallyBefore.setNumSamples(10);

        crossLeapStart = new MSeed3Header();
        crossLeapStart.setStartTime(micros);
        crossLeapStart.setStartTimeInLeapSecond(false);
        crossLeapStart.setPosLeapSecInRecord(true);
        crossLeapStart.setSampleRate(10);
        crossLeapStart.setNumSamples(10);
        
        startOnLeap = new MSeed3Header();
        startOnLeap.setStartTime(micros);
        startOnLeap.setStartTimeInLeapSecond(true);
        startOnLeap.setSampleRate(10);
        startOnLeap.setNumSamples(10);
        
        endOnLeap = new MSeed3Header();
        endOnLeap.setStartTime(micros);
        endOnLeap.setPosLeapSecInRecord(true);
        endOnLeap.setSampleRate(10);
        endOnLeap.setNumSamples(10);
        
        leapInMiddle = new MSeed3Header();
        leapInMiddle.setStartTime(micros);
        leapInMiddle.setPosLeapSecInRecord(true);
        leapInMiddle.setSampleRate(10);
        leapInMiddle.setNumSamples(20);
    }
    
    @Test
    public void testSetBits() {
        MSeed3Header header = new MSeed3Header();
        assertFalse("original little endian", header.isLittleEndian());
        assertFalse("original start in leap", header.isStartTimeInLeapSecond());
        assertFalse("original pos leap", header.isPosLeapSecInRecord());
        assertFalse("original neg leap", header.isNegLeapSecInRecord());
        assertFalse("original time ques", header.isTimeTagQuestionable());
        assertFalse("original clock lock", header.isClockLocked());
        
        header.setStartTimeInLeapSecond(true);
        
        assertFalse("after little endian", header.isLittleEndian());
        assertTrue("after start in leap", header.isStartTimeInLeapSecond());
        assertFalse("after pos leap", header.isPosLeapSecInRecord());
        assertFalse("after neg leap", header.isNegLeapSecInRecord());
        assertFalse("after time ques", header.isTimeTagQuestionable());
        assertFalse("after clock lock", header.isClockLocked());
        
        header.setStartTimeInLeapSecond(false);
        
        assertFalse("after reset little endian", header.isLittleEndian());
        assertFalse("after reset start in leap", header.isStartTimeInLeapSecond());
        assertFalse("after reset pos leap", header.isPosLeapSecInRecord());
        assertFalse("after reset neg leap", header.isNegLeapSecInRecord());
        assertFalse("after reset time ques", header.isTimeTagQuestionable());
        assertFalse("after reset clock lock", header.isClockLocked());
    }

    @Test
    public void testGetLastSampleTime() {
        assertEquals("before", (totallyBefore.getNumSamples()-1)*totallyBefore.getSamplePeriodAsMicros(), totallyBefore.getLastSampleTime() - totallyBefore.getStartTime());
        assertEquals("startOnLeap", (startOnLeap.getNumSamples()-1)*startOnLeap.getSamplePeriodAsMicros() , startOnLeap.getLastSampleTime() - startOnLeap.getStartTime());
     // leap second starts in record time range, so coverage interval will be off by 1 sec
        assertEquals("crossLeapStart", (crossLeapStart.getNumSamples()-1)*crossLeapStart.getSamplePeriodAsMicros() -SEC, crossLeapStart.getLastSampleTime() - crossLeapStart.getStartTime());
        assertEquals("endOnLeap", (endOnLeap.getNumSamples()-1)*endOnLeap.getSamplePeriodAsMicros() -SEC, endOnLeap.getLastSampleTime() - endOnLeap.getStartTime());
        assertEquals("leapInMiddle", (leapInMiddle.getNumSamples()-1)*leapInMiddle.getSamplePeriodAsMicros()  - SEC, leapInMiddle.getLastSampleTime() - leapInMiddle.getStartTime());
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
        System.out.println("before "+crossLeapStart.isStartTimeInLeapSecond()+" "+crossLeapStart.isPosLeapSecInRecord());
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
    public void testIsPosLeapSecInRecord() {
        assertFalse("totallyBefore", totallyBefore.isPosLeapSecInRecord());
        assertTrue("crossLeapStart", crossLeapStart.isPosLeapSecInRecord());
        assertFalse("startOnLeap", startOnLeap.isPosLeapSecInRecord());
        assertTrue("endOnLeap", endOnLeap.isPosLeapSecInRecord());
        assertTrue("leapInMiddle", leapInMiddle.isPosLeapSecInRecord());
    }

    @Test
    public void testIsNegLeapSecInRecord() {
        assertFalse("totallyBefore", totallyBefore.isNegLeapSecInRecord());
        assertFalse("crossLeapStart", crossLeapStart.isNegLeapSecInRecord());
        assertFalse("startOnLeap", startOnLeap.isNegLeapSecInRecord());
        assertFalse("endOnLeap", endOnLeap.isNegLeapSecInRecord());
        assertFalse("leapInMiddle", leapInMiddle.isNegLeapSecInRecord());
    }
    
    public static final long SEC = 1000000;
}
