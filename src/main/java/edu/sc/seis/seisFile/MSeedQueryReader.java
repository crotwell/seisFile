package edu.sc.seis.seisFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import edu.sc.seis.seisFile.dataSelectWS.DataSelectException;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;

public interface MSeedQueryReader {

    public abstract String createQuery(String network,
                                       String station,
                                       String location,
                                       String channel,
                                       Date begin,
                                       float durationSeconds) throws IOException, DataSelectException,
            SeedFormatException;

    public abstract List<DataRecord> read(String query) throws IOException, DataSelectException, SeedFormatException;
}