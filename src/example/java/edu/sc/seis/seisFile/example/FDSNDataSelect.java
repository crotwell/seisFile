package edu.sc.seis.seisFile.example;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import edu.sc.seis.seisFile.ChannelTimeWindow;
import edu.sc.seis.seisFile.fdsnws.FDSNDataSelectQuerier;
import edu.sc.seis.seisFile.fdsnws.FDSNDataSelectQueryParams;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.DataRecordIterator;
import edu.sc.seis.seisFile.mseed.SeedFormatException;

public class FDSNDataSelect {

    public void run() {
        runGet();
        runPost();
    }

    public void runGet() {
        try {
            // A simple one time window request using GET
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
            FDSNDataSelectQuerier querier = new FDSNDataSelectQuerier(queryParams);
            handleOutput(querier.getDataRecordIterator());
        } catch(Exception e) {
            System.err.println("Oops. " + e.getMessage());
            e.printStackTrace();
            if (e.getCause() != null) {
                System.err.println(e.getCause().getMessage());
                e.getCause().printStackTrace();
            }
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
            FDSNDataSelectQuerier querier = new FDSNDataSelectQuerier(queryParams, request);
            handleOutput(querier.getDataRecordIterator());
        } catch(Exception e) {
            System.err.println("Oops. " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleOutput(DataRecordIterator drIt) throws IOException, SeedFormatException {
        if (!drIt.hasNext()) {
            System.out.println("No Data");
        } else {
            // success
            try {
                while (drIt.hasNext()) {
                    DataRecord dr = drIt.next();
                    // do something with the DataRecord
                    System.out.println("Data Record: " + dr.getHeader());
                }
            } finally {
                drIt.close();
            }
        }
    }

    public static void main(String[] args) {
        new FDSNDataSelect().run();
    }
}
