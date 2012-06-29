package edu.sc.seis.seisFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;

public abstract class StringMSeedQueryReader implements MSeedQueryReader {

    @Override
    public List<DataRecord> read(String network, String station, String location, String channel, Date begin, Date end)
            throws IOException, SeisFileException, SeedFormatException {
        String query = createQuery(network, station, location, channel, begin, end);
        logger.info("Request: " + query);
        return read(query);
    }

    public abstract String createQuery(String network,
                                       String station,
                                       String location,
                                       String channel,
                                       Date begin,
                                       Date end) throws IOException, SeisFileException, SeedFormatException;

    public abstract List<DataRecord> read(String query) throws IOException, SeisFileException, SeedFormatException;

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    private boolean verbose = false;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(StringMSeedQueryReader.class);
}
