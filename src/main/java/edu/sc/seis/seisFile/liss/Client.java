package edu.sc.seis.seisFile.liss;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import edu.sc.seis.seisFile.BuildVersion;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.SeedRecord;

public class Client {

    public static void main(String[] args) throws IOException, SeedFormatException {
        String network = "IU";
        String station = "ANMO";
        String location = null;
        String channel = null;
        String host = station + "." + network + ".liss.org";
        String outFile = null;
        int port = DEFAULT_PORT;
        int maxRecords = 10;
        boolean verbose = false;
        DataOutputStream dos = null;
        PrintWriter out = new PrintWriter(System.out, true);
        if (args.length == 0) {
            printHelp();
            return;
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-n")) {
                network = args[i + 1];
                host = station + "." + network + ".liss.org";
            } else if (args[i].equals("-s")) {
                station = args[i + 1];
                host = station + "." + network + ".liss.org";
            } else if (args[i].equals("-l")) {
                location = args[i + 1];
            } else if (args[i].equals("-c")) {
                channel = args[i + 1];
            } else if (args[i].equals("-h")) {
                host = args[i + 1];
                String[] s = host.split("\\.");
                if (s.length == 4 && s[2].equalsIgnoreCase("liss") && s[3].equalsIgnoreCase("org")) {
                    station = s[0];
                    network = s[1];
                }
            } else if (args[i].equals("-p")) {
                port = Integer.parseInt(args[i + 1]);
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
                printHelp();
                return;
            }
        }
        if (verbose) {
            out.println("Connect to " + host + ":" + port);
        }
        Socket lissConnect = new Socket(host, port);
        DataInputStream ls = new DataInputStream(new BufferedInputStream(lissConnect.getInputStream(), 1024));
        int i = 0;
        try {
            while ((maxRecords == -1 || i < maxRecords) && lissConnect.isConnected()) {
                SeedRecord sr = SeedRecord.read(ls, 512);
                DataRecord dr;
                if (sr instanceof DataRecord) {
                    dr = (DataRecord)sr;
                } else {
                    System.err.println("None data record found, skipping...");
                    continue;
                }
                if ((location == null || location.equals(dr.getHeader().getLocationIdentifier()))
                        && (channel == null || channel.equals(dr.getHeader().getChannelIdentifier()))) {
                    if (dos != null) {
                        dr.write(dos);
                    }
                    if (dos == null || verbose) {
                        // print something to the screen if we are not saving to
                        // disk
                        dr.writeASCII(out, "    ");
                        out.flush();
                    }
                    i++;
                }
            }
        } catch(EOFException e) {
            // done I guess
        }
        if (dos != null) {
            dos.close();
        }
        lissConnect.close();
        out.println("Finished: " + new Date());
    }
    
    public static void printHelp() {
        PrintWriter out = new PrintWriter(System.out, true);
        out.println("java "
                + Client.class.getName()
                + " [[-n net][-s sta]|[-h host]][-l loc][-c chan][-p port][-o outfile][-m maxpackets][--verbose][--version][--help]");
        out.println(" host is formed from sta.net.liss.org if not given directly.");
        out.println(" See www.liss.org for more information.");
    }

    public static final int DEFAULT_PORT = 4000;
}
