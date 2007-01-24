package edu.sc.seis.seisFile.mseed;

import junit.framework.TestCase;

public class Blockette2000Test extends TestCase {

    public void testInternalConsistency() {
        String[] headerFields = new String[] {"one", "two", "three"};
        Blockette2000 b2000 = new Blockette2000(headerFields, new byte[] {1});
        assertEquals(b2000, new Blockette2000(b2000.info));
        for(int i = 0; i < headerFields.length; i++) {
            assertEquals(headerFields[i], b2000.getHeaderField(i));
        }
        assertEquals(1, b2000.getOpaqueData().length);
        assertEquals(1, b2000.getOpaqueData()[0]);
    }
}
