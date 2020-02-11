package edu.sc.seis.seisFile.mseed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


public class Blockette2000Test {

	@Test
    public void testInternalConsistency() throws SeedFormatException {
        String[] headerFields = new String[] {"one", "two", "three"};
        Blockette2000 b2000 = new Blockette2000(headerFields, new byte[] {1});
        assertEquals(b2000, new Blockette2000(b2000.info, false));
        for(int i = 0; i < headerFields.length; i++) {
            assertEquals(headerFields[i], b2000.getHeaderField(i));
        }
        assertEquals(1, b2000.getOpaqueData().length);
        assertEquals(1, b2000.getOpaqueData()[0]);
    }
}
