package edu.sc.seis.seisFile.fdsnws.dataSelectWS;

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

import edu.sc.seis.seisFile.BuildVersion;
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

    public DataSelectReader(String urlBase, int timeoutMillis) {
        this.urlBase = urlBase;
        this.timeoutMillis = timeoutMillis;
    }
    
    protected String createQuery(String network, String station, String location, String channel)  {
        String query = "net="+ network;
        query += "&sta=" + station;
        query += "&loc=" + location.replaceAll(" ", "%20");
        query += "&cha=" + channel;
        return query;
    }
    
    /* (non-Javadoc)
     * @see edu.sc.seis.seisFile.dataSelectWS.MSeedQueryReader#createQuery(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Date, float)
     */
    @Override
    public String createQuery(String network, String station, String location, String channel, Date begin, Date end) {
        String query = createQuery(network, station, location, channel);
        SimpleDateFormat longFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
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
        if (isVerbose()) {
            System.out.println("query: "+requestURL);
        }
        HttpURLConnection conn = (HttpURLConnection)requestURL.openConnection();
        if(timeoutMillis != 0) {
            conn.setReadTimeout(timeoutMillis);
        }
        conn.setRequestProperty("User-Agent", userAgent);
        conn.connect();
        if (conn.getResponseCode() != 200) {
            if (conn.getResponseCode() == 404) {
                logger.debug("reponseCode 404, no data");
                return new ArrayList<DataRecord>();
            } else if (conn.getResponseCode() == 400) {
                throw new DataSelectException("Did not get an OK repsonse code, code= :"+conn.getResponseCode()+" query was: "+query);
            } else {
                throw new DataSelectException("Did not get an OK repsonse code, code= :"+conn.getResponseCode()+" query was: "+query);
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
    
    public int getTimeoutMillis() {
        return timeoutMillis;
    }
    
    public void setTimeoutMillis(int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }
    
    public String getUserAgent() {
        return userAgent;
    }
    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    
    public String getUrlBase() {
        return urlBase;
    }

    protected int timeoutMillis;
    
    protected String urlBase;
    
    protected String userAgent = "SeisFile/"+BuildVersion.getVersion();

    public static final String DEFAULT_WS_URL = "http://www.iris.edu/ws/dataselect/query";
    
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(DataSelectReader.class);

}
