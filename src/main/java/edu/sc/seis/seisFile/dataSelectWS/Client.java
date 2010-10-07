package edu.sc.seis.seisFile.dataSelectWS;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import edu.sc.seis.seisFile.BuildVersion;
import edu.sc.seis.seisFile.mseed.DataRecord;


public class Client {

    public static void main(String[] args) {
        String network = "IU";
        String station = "ANMO";
        String location = null;
        String channel = null;
        String outFile = null;
        GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        cal.add(cal.MINUTE, -10);
        Date begin = cal.getTime();
        Float duration = 600;
        DataSelectReader dsReader = new DataSelectReader();
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
            } else if (args[i].equals("-u")) {
                dsReader = new DataSelectReader(args[i + 1]);
            } else if (args[i].equals("-b")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                begin = dateFormat.parse(args[i + 1]);
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
                out.println("java "
                        + Client.class.getName()
                        + " [-n net][-s sta][-l loc][-c chan][-b yyyy-MM-ddTHH:mm:ss.SSS][-d seconds][-u url][-o outfile][-m maxpackets][--verbose][--version][--help]");
                System.exit(0);
            }
        }
        
        URL requestURL = dsReader.createQuery(network, station, location, channel, begin, duration);
        List<DataRecord> data = dsReader.read(requestURL);
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
        if (dos != null) {
            dos.close();
        }
        out.println("Finished: " + new Date());
    }
   
}
