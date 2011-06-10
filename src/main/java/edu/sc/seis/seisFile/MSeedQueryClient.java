package edu.sc.seis.seisFile;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import edu.sc.seis.seisFile.dataSelectWS.DataSelectException;
import edu.sc.seis.seisFile.dataSelectWS.DataSelectReader;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.usgsCWB.CWBReader;
import edu.sc.seis.seisFile.usgsCWB.Client;


public abstract class MSeedQueryClient {
    
    protected MSeedQueryClient(MSeedQueryReader reader) {
        this.reader = reader;
    }

    public void readData(String[] args) throws SeisFileException, IOException {
        String network = "IU";
        String station = "ANMO";
        String location = "00";
        String channel = "BHZ";
        String outFile = null;
        GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        cal.add(cal.MINUTE, -10);
        Date begin = cal.getTime();
        Float duration = 600f;
        int maxRecords = 10;
        boolean verbose = false;
        DataOutputStream dos = null;
        PrintWriter out = new PrintWriter(System.out, true);
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-n")) {
                network = args[i + 1];
            } else if (args[i].equals("-s")) {
                station = args[i + 1];
            } else if (args[i].equals("-l")) {
                location = args[i + 1];
            } else if (args[i].equals("-c")) {
                channel = args[i + 1];
            } else if (args[i].equals("-b")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSz");
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                String beginStr = args[i+1].trim();
                if (beginStr.matches(".+\\d")) {
                    beginStr = beginStr+" GMT";
                }
                try {
                    System.out.println("Date string: "+beginStr);
                    begin = dateFormat.parse(beginStr);
                } catch(ParseException e) {
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
                    try {
                        begin = dateFormat.parse(beginStr);
                    } catch(ParseException ee) {
                        throw new SeisFileException("Illegal date format, should be:  yyyy-MM-dd'T'HH:mm:ss or yyyy-MM-dd'T'HH:mm:ss.SSS", ee);
                    }
                }
                System.out.println("Date: "+begin);
            } else if (args[i].equals("-d")) {
                duration = Float.parseFloat(args[i + 1]);
            } else if (args[i].equals("-o")) {
                outFile = args[i + 1];
                dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
            } else if (args[i].equals("-m")) {
                maxRecords = Integer.parseInt(args[i + 1]);
                if (maxRecords < -1) {
                    maxRecords = -1;
                }
            } else if (args[i].equals("--verbose")) {
                verbose = true;
            } else if (args[i].equals("--version")) {
                out.println(BuildVersion.getDetailedVersion());
                System.exit(0);
            } else if (args[i].equals("--help")) {
                out.println(getHelp());
                System.exit(0);
            }
        }
        
        String request = reader.createQuery(network, station, location, channel, begin, duration);
        if (verbose) {
            out.println("Request: "+request);
        }
        List<DataRecord> data = reader.read(request);
        for (DataRecord dr : data) {
            if (dos != null) {
                dr.write(dos);
            }
            if (dos == null || verbose) {
                // print something to the screen if we are not saving to
                // disk
                dr.writeASCII(out, "    ");
                out.flush();
            }
        }
        if (verbose && data.size() == 0) {
            out.println("No Data.");
            out.flush();
        }
        if (dos != null) {
            dos.close();
        }
        out.println("Finished: " + new Date());
    }
    
    public abstract String getHelp(); 
    
    MSeedQueryReader reader;
}
