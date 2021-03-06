package edu.sc.seis.seisFile.earthworm;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import edu.sc.seis.seisFile.earthworm.EarthwormEscapeOutputStream;


public class EarthwormExportTest {

    @Test
    public void testWriteThreeChars() throws IOException {
        int val = 2;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        EarthwormEscapeOutputStream outStream = new EarthwormEscapeOutputStream(bos);
        outStream.writeThreeChars(val);
        outStream.close();
        String s = new String(bos.toByteArray());
        assertEquals(val, Integer.parseInt(s));
    }
}
