/**
 * DataHeaderTest.java
 * 
 * @author Created by Omnicore CodeGuide
 */
package edu.sc.seis.seisFile.mseed;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import junit.framework.TestCase;

public class DataHeaderTest extends TestCase {

    public DataHeaderTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testWrite() throws Exception {
        DataHeader header = new DataHeader(1, 'D', false);
        header.setChannelIdentifier("BHZ");
        short dataOffset = (short)(48 + 8);
        header.setDataOffset(dataOffset);
        short blockOffset = (short)48;
        header.setDataBlocketteOffset(blockOffset);
        header.setLocationIdentifier("00");
        header.setNetworkCode("SP");
        byte numBlockettes = (byte)1;
        header.setNumBlockettes(numBlockettes);
        short numSamp = (short)100;
        header.setNumSamples(numSamp);
        short sampFac = (short)20;
        header.setSampleRateFactor(sampFac);
        short sampMul = (short)-100;
        header.setSampleRateMultiplier(sampMul);
        Btime time = new Btime();
        time.year = 2001;
        time.jday = 251;
        time.hour = 13;
        time.min = 23;
        time.sec = 56;
        time.tenthMilli = 9870;
        header.setStartBtime(time);
        header.setStationIdentifier("ACFLR");
        ByteArrayOutputStream bos = new ByteArrayOutputStream(4096);
        header.write(new DataOutputStream(bos));
        byte[] out = bos.toByteArray();
        // for (int i = 0; i < 20; i++) {
        // System.out.print(out[i]);
        // }
        // System.out.println("");
        assertEquals("year=", time.year, Utility.bytesToShort(out[20],
                                                              out[21],
                                                              false));
        assertEquals("jday=", time.jday, Utility.bytesToShort(out[22],
                                                              out[23],
                                                              false));
        assertEquals("hour=", time.hour, out[24]);
        assertEquals("min=", time.min, out[25]);
        assertEquals("sec=", time.sec, out[26]);
        assertEquals("unused=", 0, out[27]);
        assertEquals("tsec=", time.tenthMilli, Utility.bytesToShort(out[28],
                                                                    out[29],
                                                                    false));
        assertEquals("numPTS=", numSamp, Utility.bytesToShort(out[30],
                                                              out[31],
                                                              false));
        assertEquals("sampFac=", sampFac, Utility.bytesToShort(out[32],
                                                               out[33],
                                                               false));
        assertEquals("sampMul=", sampMul, Utility.bytesToShort(out[34],
                                                               out[35],
                                                               false));
        byte zero = (byte)0;
        assertEquals("ac=", zero, out[36]);
        assertEquals("io=", zero, out[37]);
        assertEquals("qual=", zero, out[38]);
        assertEquals("numBlockettes=", numBlockettes, out[39]);
        assertEquals("ac=", zero, out[40]);
        assertEquals("ac=", zero, out[41]);
        assertEquals("ac=", zero, out[42]);
        assertEquals("ac=", zero, out[43]);
        assertEquals("beginData=", dataOffset, Utility.bytesToShort(out[44],
                                                                    out[45],
                                                                    false));
        assertEquals("firstBlockette=",
                     blockOffset,
                     Utility.bytesToShort(out[46], out[47], false));
    }
}
