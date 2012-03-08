package edu.sc.seis.seisFile.winston;

import static org.junit.Assert.*;

import org.junit.Test;


public class WinstonSCNLTest {

    @Test
    public void test() {
        WinstonSCNL scnl = new WinstonSCNL("ABC", "BHZ", "XX", "00", "W");
        assertEquals("W_ABC$BHZ$XX$00", scnl.getDatabaseName());
        scnl = new WinstonSCNL("ABC", "BHZ", "XX", "", "W");
        assertEquals("W_ABC$BHZ$XX", scnl.getDatabaseName());
    }
}
