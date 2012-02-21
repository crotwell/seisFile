package edu.sc.seis.seisFile.syncFile;

import static org.junit.Assert.*;

import java.text.ParseException;

import org.junit.Test;


public class SyncFileTest {

    @Test
    public void testParse() throws NumberFormatException, ParseException {
        SyncLine sl = SyncLine.parse("CO|JSC|00|BHZ|2010,243,08:00:05|2010,243,08:00:13|0.0|100.0|||||||");
        sl = SyncLine.parse("IU|ANMO|01|BHE|1994,258,00:00:00|1994,265,00:00:00|.0005|20||CG|||||1998,275||");
        sl = SyncLine.parse("IU|ANMO|01|BHE|1994,265,00:00:00|1994,275,00:00:00|.0005|20||CG|||||1998,275||");
    }
}
