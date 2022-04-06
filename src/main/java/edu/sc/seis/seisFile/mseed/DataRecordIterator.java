package edu.sc.seis.seisFile.mseed;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

import edu.sc.seis.seisFile.fdsnws.FDSNDataSelectQuerier;


public class DataRecordIterator implements AutoCloseable {

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
    
    public void close() {
        if (in != null && in instanceof DataInputStream) {
            try {
                ((DataInputStream)in).close();
            } catch(IOException e) {
                // oh well...
            }
            in = null;
        }
    }

    DataRecord nextDr;

    DataInput in;
    
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DataRecordIterator.class);
    
    /* this is to prevent the FdsnDataSelectQuerier from being garbage collected and closing the input stream. */
    FDSNDataSelectQuerier querier;
    public void setQuerier(FDSNDataSelectQuerier q) {
        this.querier = q;
    }
}
