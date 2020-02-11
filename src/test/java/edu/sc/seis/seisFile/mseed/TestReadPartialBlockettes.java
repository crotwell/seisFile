package edu.sc.seis.seisFile.mseed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;


import edu.sc.seis.seisFile.sac.TestSacFileData;


public class TestReadPartialBlockettes {

    @Test
    public void readPartialBlockettesJSC() throws IOException, SeedFormatException {
        DataInputStream in = new DataInputStream(new BufferedInputStream(TestSacFileData.class.getClassLoader()
                                                 .getResourceAsStream("edu/sc/seis/seisFile/mseed/CO_JSC.seed")));
        int defaultSize = 0;
        while (true) {
            try {
                SeedRecord sr = SeedRecord.read(in, defaultSize);
                System.out.println("Read record: "+sr.getControlHeader()+" "+(sr instanceof ContinuedControlRecord)+" "+defaultSize);
                sr.writeASCII(new PrintWriter(new OutputStreamWriter(System.out)));
                if (sr instanceof ControlRecord) {
                    Blockette[] blockettes = ((ControlRecord)sr).getBlockettes();
                    for (Blockette b : blockettes) {
                        assertFalse( b instanceof PartialBlockette);
                    }
                    System.out.println(sr.getControlHeader());
                    defaultSize = sr.getRecordSize();
                } else {
                	assertTrue(sr instanceof ControlRecord, "Not a control record, skipping..."+sr.getControlHeader().getSequenceNum()+" "+sr.getControlHeader().getTypeCode());
                }
            } catch (EOFException e) {
                System.out.println("Got EOF");
                // end of data?
                break;
            }
        }
        in.close();
    }
}
