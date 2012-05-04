package edu.sc.seis.seisFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import edu.sc.seis.seisFile.dataSelectWS.DataSelectException;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;

public interface MSeedQueryReader {

    public abstract List<DataRecord> read(String network,
                                          String station,
                                          String location,
                                          String channel,
                                          Date begin,
                                          Date end) throws IOException, SeisFileException,
            SeedFormatException;
}