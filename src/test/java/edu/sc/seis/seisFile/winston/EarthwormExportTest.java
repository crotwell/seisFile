package edu.sc.seis.seisFile.winston;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.junit.Test;


public class EarthwormExportTest {

    @Test
    public void testWriteThreeChars() throws IOException {
        int val = 2;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        EarthwormExport.writeThreeChars(bos, val);
        String s = new String(bos.toByteArray());
        assertEquals(val, Integer.parseInt(s));
    }
}
