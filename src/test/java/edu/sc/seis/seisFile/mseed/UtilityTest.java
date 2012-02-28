package edu.sc.seis.seisFile.mseed;

import static org.junit.Assert.*;

import org.junit.Test;


public class UtilityTest {

    @Test
    public void testExtractString() {
        String answer = "THE ANSWER";
        String prefix = "bla bla bla";
        String suffix = " more bla bla";
        String testString = prefix+answer+"~"+suffix;
        byte[] testBytes = testString.getBytes();
        assertEquals(answer, Utility.extractVarString(testBytes, prefix.length(), 20));
        // change "~" to null
        testBytes[prefix.length()+answer.length()] = (byte)0;
        assertEquals(answer, Utility.extractNullTermString(testBytes, prefix.length(), 20));
    }
}
