package edu.sc.seis.seisFile.mseed;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;


public class DataRecordIterator {

    public DataRecordIterator(DataInput in) {
        this.in = in;
    }

    public boolean hasNext() throws SeedFormatException, IOException {
        if (in == null) {
            return false;
        }
        while (in != null && nextDr == null) {
            try {
                SeedRecord sr = SeedRecord.read(in);
                if (sr instanceof DataRecord) {
                    nextDr = (DataRecord)sr;
                    break;
                } else {
                    logger.warn("Not a data record, skipping..." + sr.getControlHeader().getSequenceNum() + " "
                            + sr.getControlHeader().getTypeCode());
                }
            } catch(EOFException e) {
                in = null;
                nextDr = null;
                break;
            }
        }
        return nextDr != null;
    }

    public DataRecord next() throws SeedFormatException, IOException {
        if (hasNext()) {
            DataRecord out = nextDr;
            nextDr = null;
            return out;
        }
        return null;
    }

    DataRecord nextDr;

    DataInput in;
    
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DataRecordIterator.class);
}
