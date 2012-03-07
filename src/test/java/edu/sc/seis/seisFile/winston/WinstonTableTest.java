package edu.sc.seis.seisFile.winston;

import static org.junit.Assert.*;

import java.text.ParseException;

import org.junit.Test;


public class WinstonTableTest {

    @Test
    public void test() throws ParseException {
        String prefix = "W";
        String databaseName = "BIRD$BHN$CO$00";
        WinstonSCNL database = new WinstonSCNL(WinstonUtil.prefixTableName(prefix, databaseName), prefix);
        String heliTable = "BIRD$BHN$CO$00$$H2010_04_12";
        String plainTable = "BIRD$BHN$CO$00$$2010_04_12";
        WinstonTable wt = new WinstonTable(database, heliTable);
        assertEquals(prefix+"_"+databaseName, database.getDatabaseName());
        assertEquals(2010, wt.getYear());
        assertEquals(4, wt.getMonth());
        assertEquals(12, wt.getDay());
        assertEquals(plainTable, wt.getTableName());
        assertEquals(heliTable, wt.getHeliTableName());
        wt = new WinstonTable(database, plainTable);
        assertEquals(2010, wt.getYear());
        assertEquals(4, wt.getMonth());
        assertEquals(12, wt.getDay());
        assertEquals(heliTable, wt.getHeliTableName());
        assertEquals(plainTable, wt.getTableName());
    }
}
