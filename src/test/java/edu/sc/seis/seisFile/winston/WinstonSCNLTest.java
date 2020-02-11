package edu.sc.seis.seisFile.winston;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;



public class WinstonSCNLTest {

    @Test
    public void test() {
        WinstonSCNL scnl = new WinstonSCNL("ABC", "BHZ", "XX", "00", "W");
        assertEquals("W_ABC$BHZ$XX$00", scnl.getDatabaseName());
        scnl = new WinstonSCNL("ABC", "BHZ", "XX", "", "W");
        assertEquals("W_ABC$BHZ$XX", scnl.getDatabaseName());
    }
}
