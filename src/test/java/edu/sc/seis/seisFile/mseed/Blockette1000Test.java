/**
 * Blockette1000Test.java
 * 
 * @author Created by Omnicore CodeGuide
 */
package edu.sc.seis.seisFile.mseed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * DataHeaderTest.java
 * 
 * @author Created by Omnicore CodeGuide
 */
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;


public class Blockette1000Test  {
	
	@Test
    public void testWrite() throws Exception {
        Blockette1000 blockette = new Blockette1000();
        byte encoding = (byte)10;
        byte wordOrder = (byte)1;
        byte dataLength = (byte)12;
        byte reserved = (byte)0;
        short nextOffset = (short)(48 + 8);
        blockette.setEncodingFormat(encoding);
        blockette.setWordOrder(wordOrder);
        blockette.setDataRecordLength(dataLength);
        blockette.setReserved(reserved);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(4096);
        blockette.write(new DataOutputStream(bos), nextOffset);
        byte[] out = bos.toByteArray();
        assertEquals(8, out.length);
        assertEquals((short)blockette.getType(),
                     Utility.bytesToShort(out[0], out[1], false));
        assertEquals( nextOffset, Utility.bytesToShort(out[2],
                                                              out[3],
                                                              false));
        assertEquals( encoding, out[4]);
        assertEquals( wordOrder, out[5]);
        assertEquals( dataLength, out[6]);
        assertEquals( reserved, out[7]);
    }
}
