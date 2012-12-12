package edu.sc.seis.seisFile.dataSelectWS;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import edu.sc.seis.seisFile.BuildVersion;
import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.StringMSeedQueryReader;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.SeedRecord;


public class BulkDataSelectReader  extends StringMSeedQueryReader {

    public BulkDataSelectReader() {
        this(DEFAULT_WS_URL);
    }
    
    public BulkDataSelectReader(String urlBase) {
        this(urlBase, DEFAULT_TIMEOUT_MILLIS);
    }

    public BulkDataSelectReader(String urlBase, int timeoutMillis) {
        this.urlBase = urlBase;
        this.timeoutMillis = timeoutMillis;
    }
    
    @Override
    public String createQuery(String network, String station, String location, String channel, Date begin, Date end) {
        SimpleDateFormat longFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        longFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return network+" "+station+" "+(location.length()==0 || location.equals("  ")?"--":location)+" "+channel
                +" "+longFormat.format(begin)+" "+longFormat.format(end)+"\n";
    }

    @Override
    public List<DataRecord> read(String query) throws IOException, DataSelectException, SeedFormatException {
        URL requestURL = new URL(urlBase);
        if (isVerbose()) {
            System.out.println("query: "+requestURL);
        }
        HttpURLConnection conn = (HttpURLConnection)requestURL.openConnection();
        if(timeoutMillis != 0) {
            conn.setReadTimeout(timeoutMillis);
        }
        conn.setRequestMethod("POST");
        conn.setRequestProperty("User-Agent", userAgent);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        byte[] queryBytes = query.getBytes();
        conn.setRequestProperty("Content-Length", "" + 
                Integer.toString(queryBytes.length));
        conn.setUseCaches(false);
        conn.setAllowUserInteraction(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.connect();
        OutputStream outputStream = conn.getOutputStream();
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(outputStream));
        out.write(query);
        out.close();
        outputStream.close();
        queryBytes = null;

        if (conn.getResponseCode() != 200) {
            if (conn.getResponseCode() == 404) {
                logger.warn("reponseCode 404, no data");
                return new ArrayList<DataRecord>();
            } else if (conn.getResponseCode() == 405 || conn.getResponseCode() == 400) {
                    logger.warn("reponseCode  "+conn.getResponseCode() + conn.getResponseMessage()+" "+conn.getRequestMethod()+" "+conn.getHeaderField("Allow")+" \nQuery:\n"+query);
                    DataInputStream errorIn = new DataInputStream(new BufferedInputStream(conn.getErrorStream()));
                    while(errorIn.available() != 0) {
                        System.out.print(errorIn.readLine());
                    }
                    throw new DataSelectException("Did not get an OK repsonse code, code= :"+conn.getResponseCode() + conn.getResponseMessage()+"  query: "+query);
            } else {
                throw new DataSelectException("Did not get an OK repsonse code, code= :"+conn.getResponseCode() + conn.getResponseMessage()+"  query: "+query);
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
        conn.disconnect();
        return records;
    }
    
    
    public String getUserAgent() {
        return userAgent;
    }

    
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    
    public int getTimeoutMillis() {
        return timeoutMillis;
    }

    
    public String getUrlBase() {
        return urlBase;
    }

    protected int timeoutMillis;
    
    protected String urlBase;
    
    protected String userAgent = "SeisFile/"+BuildVersion.getVersion();

    public static final String DEFAULT_WS_URL = "http://www.iris.edu/ws/bulkdataselect/query";
    
    public static final int DEFAULT_TIMEOUT_MILLIS = 10*1000; // 30 seconds
    
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(BulkDataSelectReader.class);
    
}
