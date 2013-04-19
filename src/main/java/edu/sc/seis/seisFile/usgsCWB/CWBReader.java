package edu.sc.seis.seisFile.usgsCWB;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import edu.sc.seis.seisFile.StringMSeedQueryReader;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.SeedRecord;


public class CWBReader extends StringMSeedQueryReader {
    
    public CWBReader() {
        this(DEFAULT_HOST);
    }
    
    public CWBReader(String host) {
        this(host, DEFAULT_PORT);
    }

    public CWBReader(String host, int port) {
        this(host, port, 0);
    }

    public CWBReader(String host, int port, int timeoutMillis) {
        this.host = host;
        this.port = port;
        this.timeoutMillis = timeoutMillis;
    }
    
    protected String createQuery(String network, String station, String location, String channel) {
        String query = leftPad(network.trim(), 2);
        query += leftPad(station.trim(), 5);
        query += leftPad(channel.trim(), 3);
        query += leftPad(location.trim(), 2);
        return query;
    }
    
    @Override
    public String createQuery(String network, String station, String location, String channel, Date begin, Date end)  {
        String query = "'-s' '"+createQuery(network, station, location, channel)+"' ";
        SimpleDateFormat longFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
        longFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        query += "'-b' '" + longFormat.format(begin)+"' ";
        query += "'-d' '" + (int)Math.ceil((end.getTime()-begin.getTime())/1000f)+"' ";
        query += "'-t' 'ms'";
        query += "\t";
        return query;
    }
    
    @Override
    public List<DataRecord> read(String query) throws IOException, SeedFormatException {
        Socket socket = new Socket(host, port);
        socket.setReceiveBufferSize(512000);
        if(timeoutMillis != 0) {
            socket.setSoTimeout(timeoutMillis);
        }
        OutputStream outtcp = socket.getOutputStream();
        outtcp.write(query.getBytes());
        outtcp.flush();
        PushbackInputStream bif = new PushbackInputStream(new BufferedInputStream(socket.getInputStream()), 1);
        DataInputStream in = new DataInputStream(bif);
        List<DataRecord> records = new ArrayList<DataRecord>();
        while (true) {
            try {
                int nextByte = bif.read();
                if (nextByte == '<') {
                    // end of stream marked with "<EOR>" so end read
                    break;
                } else {
                    bif.unread(nextByte);
                }
                SeedRecord sr = SeedRecord.read(in);
                if (sr instanceof DataRecord) {
                    records.add((DataRecord)sr);
                } else {
                    System.err.println("None data record found, skipping...");
                }
            } catch (EOFException e) {
                // end of data?
                break;
            }
        }
        in.close();
        outtcp.close();
        socket.close();
        return records;
    }
    
    protected String leftPad(String in, int length) {
        if (in.length() == length) { 
            return in; 
        } else {
            return leftPad(in+"-", length);
        }
    }
    
    public int getTimeoutMillis() {
        return timeoutMillis;
    }
    
    public void setTimeoutMillis(int timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    private String host;
    private int port;
    
    protected int timeoutMillis;
    
    public static final String DEFAULT_HOST = "cwb-pub.cr.usgs.gov";
    
    public static final int DEFAULT_PORT = 2061;
}
