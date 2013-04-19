package edu.sc.seis.seisFile.example;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import edu.sc.seis.seisFile.fdsnws.AbstractFDSNClient;
import edu.sc.seis.seisFile.fdsnws.FDSNDataSelectQueryParams;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.DataRecordIterator;
import edu.sc.seis.seisFile.mseed.SeedRecord;

public class FDSNDataSelect {

    public void run() {
        try {
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
            if (conn.getResponseCode() == 204) {
                System.out.println("No Data");
            } else if (conn.getResponseCode() == 200) {
                // success
                DataInputStream in = new DataInputStream(new BufferedInputStream(conn.getInputStream()));
                DataRecordIterator drIt = new DataRecordIterator(in);
                try {
                    while(drIt.hasNext()) {
                        DataRecord dr = drIt.next();
                        // do something with the DataRecord
                        System.out.println("Data Record: "+dr.getHeader());
                    }
                } finally {
                    in.close();
                }
            } else {
                System.err.println("oops, error Response Code :" + conn.getResponseCode());
                System.err.println("Error in connection with url: " + url);
                System.err.println(AbstractFDSNClient.extractErrorMessage(conn));
            }
        } catch(Exception e) {
            System.err.println("Oops. " + e.getMessage());
            e.printStackTrace();
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
