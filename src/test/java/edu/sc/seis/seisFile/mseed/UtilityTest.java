package edu.sc.seis.seisFile.mseed;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Test;

import edu.sc.seis.seisFile.winston.WinstonUtil;


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
    
    @Test
    public void testByteConvert() {
        long l = 123456789l;
        byte[] b = Utility.longToByteArray(l);
        long out = Utility.bytesToLong(b, 0, false);
        assertEquals(l, out);
        
        double d = 378670040.75;
        b = Utility.doubleToByteArray(d);
        System.out.println(b[0]+" "+b[1]+" "+b[2]+" "+b[3]+" "+b[4]+" "+b[5]+" "+b[6]+" "+b[7]);
        double dOut = Utility.bytesToDouble(b, 0, false);
        assertEquals("double round trip", d, dOut, 0.01);
        
        byte[] bOut = Utility.doubleToByteArray(dOut);
        assertArrayEquals(b, bOut);
    }
    
    
    @Test
    public void testSwappedExampleFromWinston() throws Exception {
        // date approx Jan 1, 2012 01:59:56
        byte[] b = new byte[] {(byte)65, (byte)211, (byte)191, (byte)232, (byte)128, (byte)64, (byte)0, (byte)0};
        long l = Utility.bytesToLong(b, 0, false);
        

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(bos);
        os.writeLong(l);
        os.close();
        byte[] bFromDOS = bos.toByteArray();
        assertArrayEquals("from DOS", b, bFromDOS);
        
        double d = Double.longBitsToDouble(l);
        
        ByteArrayInputStream bis = new ByteArrayInputStream(b);
        DataInputStream is = new DataInputStream(bis);
        double dFromDIS = is.readDouble();
        assertEquals(dFromDIS, d, 0.01);
        
        Calendar cal = new GregorianCalendar(2012, Calendar.JANUARY, 1, 0, 0, 0);
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        
        double jan1_2012 = cal.getTime().getTime()/1000;
        assertEquals("on 2012 jan 1 "+sdf.format(cal.getTime())+" err="+(jan1_2012-d), jan1_2012, d, 10);
        
    }
    
    @Test
    public void testBytesToShort() {
        short s = Utility.bytesToShort((byte)1, (byte)2, false);
        assertEquals(258, s);
        s = Utility.bytesToShort((byte)0, (byte)17, false);
        assertEquals(17, s);
        s = Utility.bytesToShort((byte)1, (byte)0, true);
        assertEquals(1, s);
        s = Utility.bytesToShort((byte)17, (byte)0, true);
        assertEquals(17, s);
        s = Utility.bytesToShort((byte)253, (byte)255, true);
        assertEquals(-3, s);
    }
}
