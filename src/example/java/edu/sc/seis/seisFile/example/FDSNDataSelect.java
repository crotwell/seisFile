package edu.sc.seis.seisFile.example;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
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
import edu.sc.seis.seisFile.ChannelTimeWindow;
import edu.sc.seis.seisFile.fdsnws.AbstractFDSNClient;
import edu.sc.seis.seisFile.fdsnws.FDSNDataSelectQueryParams;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.DataRecordIterator;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.SeedRecord;

public class FDSNDataSelect {

    public void run() {
        runGet();
        runPost();
    }
    
    public void runGet() {
        try {
            // A simple one channel request using GET
            FDSNDataSelectQueryParams queryParams = new FDSNDataSelectQueryParams();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            queryParams.setStartTime(sdf.parse("2013-03-15T12:34:21"))
                    .setEndTime(sdf.parse("2013-03-15T12:35:21"))
                    .appendToNetwork("CO")
                    .appendToStation("BIRD")
                    .appendToStation("JSC")
                    .appendToStation("CASEE")
                    .appendToChannel("HHZ");
            URL url = queryParams.formURI().toURL();
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestProperty("User-Agent", BuildVersion.getName()+"-"+BuildVersion.getVersion());
            handleOutput(conn);
        } catch(Exception e) {
            System.err.println("Oops. " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void runPost() {
        try {
            // A simple request using POST
            FDSNDataSelectQueryParams queryParams = new FDSNDataSelectQueryParams();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            List<ChannelTimeWindow> request = new ArrayList<ChannelTimeWindow>();
            Date start = sdf.parse("2013-03-15T12:34:21");
            int durationSecs = 60;
            String[] staCodes = new String[] {"BIRD", "JSC", "CASEE"};
            String[] chanCodes = new String[] {"HHZ", "HHN", "HHE"};
            for (int s = 0; s < staCodes.length; s++) {
                for (int c = 0; c < chanCodes.length; c++) {
                    request.add(new ChannelTimeWindow("CO", staCodes[s], "00", chanCodes[c], start, durationSecs));
                }
            }
            String postQuery = queryParams.formPostString(request);
            URL url = queryParams.formURI().toURL();
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", BuildVersion.getName()+"-"+BuildVersion.getVersion());
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            byte[] queryBytes = postQuery.getBytes();
            conn.setRequestProperty("Content-Length", "" + 
                    Integer.toString(queryBytes.length));
            conn.setUseCaches(false);
            conn.setAllowUserInteraction(false);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
            OutputStream outputStream = conn.getOutputStream();
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(outputStream));
            out.write(postQuery);
            out.close();
            outputStream.close();
            queryBytes = null;
            handleOutput(conn);
        } catch(Exception e) {
            System.err.println("Oops. " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleOutput(HttpURLConnection conn) throws IOException, SeedFormatException {
        if (conn.getResponseCode() == 204) {
            System.out.println("No Data");
        } else if (conn.getResponseCode() == 200) {
            // success
            DataInputStream in = new DataInputStream(new BufferedInputStream(conn.getInputStream()));
            DataRecordIterator drIt = new DataRecordIterator(in);
            try {
                while (drIt.hasNext()) {
                    DataRecord dr = drIt.next();
                    // do something with the DataRecord
                    System.out.println("Data Record: " + dr.getHeader());
                }
            } finally {
                in.close();
            }
        } else {
            System.err.println("oops, error Response Code :" + conn.getResponseCode());
            System.err.println("Error in connection with url: " + conn.getURL());
            System.err.println(AbstractFDSNClient.extractErrorMessage(conn));
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        FDSNDataSelect ds = new FDSNDataSelect();
        ds.run();
    }
}
