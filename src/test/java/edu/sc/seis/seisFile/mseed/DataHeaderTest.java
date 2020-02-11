/**
 * DataHeaderTest.java
 * 
 * @author Created by Omnicore CodeGuide
 */
package edu.sc.seis.seisFile.mseed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;


public class DataHeaderTest  {

	@Test
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
        assertEquals( time.year, Utility.bytesToShort(out[20],
                                                              out[21],
                                                              false));
        assertEquals( time.jday, Utility.bytesToShort(out[22],
                                                              out[23],
                                                              false));
        assertEquals( time.hour, out[24]);
        assertEquals( time.min, out[25]);
        assertEquals( time.sec, out[26]);
        assertEquals( 0, out[27]);
        assertEquals( time.tenthMilli, Utility.bytesToShort(out[28],
                                                                    out[29],
                                                                    false));
        assertEquals( numSamp, Utility.bytesToShort(out[30],
                                                              out[31],
                                                              false));
        assertEquals( sampFac, Utility.bytesToShort(out[32],
                                                               out[33],
                                                               false));
        assertEquals( sampMul, Utility.bytesToShort(out[34],
                                                               out[35],
                                                               false));
        byte zero = (byte)0;
        assertEquals( zero, out[36]);
        assertEquals( zero, out[37]);
        assertEquals( zero, out[38]);
        assertEquals( numBlockettes, out[39]);
        assertEquals( zero, out[40]);
        assertEquals( zero, out[41]);
        assertEquals( zero, out[42]);
        assertEquals( zero, out[43]);
        assertEquals( dataOffset, Utility.bytesToShort(out[44],
                                                                    out[45],
                                                                    false));
        assertEquals(blockOffset,
                     Utility.bytesToShort(out[46], out[47], false));
    }
}
