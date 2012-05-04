package edu.sc.seis.seisFile.dataSelectWS;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import edu.sc.seis.seisFile.MSeedQueryReader;
import edu.sc.seis.seisFile.StringMSeedQueryReader;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.SeedRecord;


public class DataSelectReader extends StringMSeedQueryReader {
    
    public DataSelectReader() {
        this(DEFAULT_WS_URL);
    }
    
    public DataSelectReader(String urlBase) {
        this.urlBase = urlBase;
    }
    
    protected String createQuery(String network, String station, String location, String channel) throws IOException, DataSelectException, SeedFormatException {
        String query = "net="+ network;
        query += "&sta=" + station;
        query += "&loc=" + location;
        query += "&cha=" + channel;
        return query;
    }
    
    /* (non-Javadoc)
     * @see edu.sc.seis.seisFile.dataSelectWS.MSeedQueryReader#createQuery(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Date, float)
     */
    @Override
    public String createQuery(String network, String station, String location, String channel, Date begin, Date end) throws IOException, DataSelectException, SeedFormatException {
        String query = createQuery(network, station, location, channel);
        SimpleDateFormat longFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        longFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        query += "&start=" + longFormat.format(begin);
        query += "&end=" + longFormat.format(end);
        return query;
    }
    
    /* (non-Javadoc)
     * @see edu.sc.seis.seisFile.dataSelectWS.MSeedQueryReader#read(java.lang.String)
     */
    public List<DataRecord> read(String query) throws IOException, DataSelectException, SeedFormatException {
        URL requestURL = new URL(urlBase + "?"+query);
        HttpURLConnection conn = (HttpURLConnection)requestURL.openConnection();
        conn.connect();
        if (conn.getResponseCode() != 200) {
            if (conn.getResponseCode() == 404) {
                logger.debug("reponseCode 404, no data");
                return new ArrayList<DataRecord>();
            } else {
                throw new DataSelectException("Did not get an OK repsonse code, code= :"+conn.getResponseCode());
            }
        }
        BufferedInputStream bif = new BufferedInputStream(conn.getInputStream());
        DataInputStream in = new DataInputStream(bif);
        List<DataRecord> records = new ArrayList<DataRecord>();
        while (true) {
            try {
                SeedRecord sr = SeedRecord.read(in);
                if (sr instanceof DataRecord) {
                    records.add((DataRecord)sr);
                } else {
                    logger.warn("Not a data record, skipping..."+sr.getControlHeader().getSequenceNum()+" "+sr.getControlHeader().getTypeCode());
                }
            } catch (EOFException e) {
                // end of data?
                break;
            }
        }
        in.close();
        return records;
    }
    
    protected String urlBase;

    public static final String DEFAULT_WS_URL = "http://www.iris.edu/ws/dataselect/query";
    
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DataSelectReader.class);

}
