package edu.sc.seis.seisFile.winston;

import static org.junit.Assert.*;

import java.text.ParseException;

import org.junit.Test;


public class WinstonTableTest {

    @Test
    public void test() throws ParseException {
        String prefix = "W_";
        WinstonSCNL database = new WinstonSCNL(prefix+"BIRD$BHN$CO$00", prefix);
        String heliTable = "BIRD$BHN$CO$00$$H2010_04_12";
        String plainTable = "BIRD$BHN$CO$00$$2010_04_12";
        WinstonTable wt = new WinstonTable(database, heliTable);
        assertEquals(2010, wt.getYear());
        assertEquals(4, wt.getMonth());
        assertEquals(12, wt.getDay());
        assertEquals(heliTable, wt.getHeliTableName());
        assertEquals(plainTable, wt.getTableName());
        wt = new WinstonTable(database, plainTable);
        assertEquals(2010, wt.getYear());
        assertEquals(4, wt.getMonth());
        assertEquals(12, wt.getDay());
        assertEquals(heliTable, wt.getHeliTableName());
        assertEquals(plainTable, wt.getTableName());
    }
}
