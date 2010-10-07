package edu.sc.seis.seisFile.dataSelectWS;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;


public class DataSelectReader {
    
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
    public URL createQuery(String network, String station, String location, String channel, Date begin, float durationSeconds) throws IOException, DataSelectException, SeedFormatException {
        String query = createQuery(network, station, location, channel);
        query += "&start=" + longFormat.format(begin);
        query += "&dur=" + durationSeconds;
        URL requestURL = new URL(urlBase + "?"+query);
        return requestURL;
    }
    
    public List<DataRecord> read(URL requestURL) throws IOException, DataSelectException, SeedFormatException {
        HttpURLConnection conn = (HttpURLConnection)requestURL.openConnection();
        conn.connect();
        if (conn.getResponseCode() != 200) {
            if (conn.getResponseCode() == 404) {
                // no data
                return new ArrayList<DataRecord>();
            } else {
                throw new DataSelectException("Did not get an OK repsonse code:"+conn.getResponseCode());
            }
        }
        BufferedInputStream bif = new BufferedInputStream(conn.getInputStream());
        DataInputStream in = new DataInputStream(bif);
        List<DataRecord> records = new ArrayList<DataRecord>();
        while (true) {
            try {
                records.add(DataRecord.read(in));
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
    
    public static SimpleDateFormat longFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

}
