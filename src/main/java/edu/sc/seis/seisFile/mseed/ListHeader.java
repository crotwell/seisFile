package edu.sc.seis.seisFile.mseed;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

import edu.sc.seis.seisFile.BuildVersion;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.sac.SacTimeSeries;

public class ListHeader {

    public static void main(String[] args) throws IOException, SeedFormatException {
        String network = "IU";
        String station = "ANMO";
        String location = null;
        String channel = null;
        String filename = null;
        String outFile = null;
        int port = DEFAULT_PORT;
        int maxRecords = -1;
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
                        + ListHeader.class.getName()
                        + " [-n net][-s sta][-l loc][-c chan][-p port][-o outfile][-m maxpackets][--verbose][--version][--help]");
                out.println(" host is formed from sta.net.liss.org if not given directly.");
                out.println(" See www.liss.org for more information.");
                System.exit(0);
            } else {
                filename = args[i];
            }
        }
        if (filename == null) {
            return;
        }
        File f = new File(filename);
        if (!f.exists() || !f.isFile()) {
            out.println("Cannot load " + filename + ", exists=" + f.exists() + " isFile=" + f.isFile());
            return;
        }
        
        // if you wish to customize the blockette creation, for example to add new types of Blockettes, 
        // create an object that implements BlocketteFactory and then
        // SeedRecord.setBlocketteFactory(myBlocketteFactory);
        // see DefaultBlocketteFactory for an example
        DataInputStream inStream = new DataInputStream(new BufferedInputStream(new FileInputStream(filename), 1024));
        int i = 0;
        try {
            while (maxRecords == -1 || i < maxRecords) {
                SeedRecord sr;
                sr = SeedRecord.read(inStream, 4096);
                sr.writeASCII(out, "    ");
                out.flush();
                if (sr instanceof DataRecord) {
                    DataRecord dr = (DataRecord)sr;
                    if ((location == null || location.equals(dr.getHeader().getLocationIdentifier()))
                            && (channel == null || channel.equals(dr.getHeader().getChannelIdentifier()))) {
                        if (dos != null) {
                            dr.write(dos);
                        }
                        if (dos == null || verbose) {
                            // print something to the screen if we are not
                            // saving to disk
                            dr.writeASCII(out, "    ");
                            out.flush();
                        }
                        i++;
                    }
                }
            }
        } catch(EOFException e) {
            // done I guess
        }
        if (dos != null) {
            dos.close();
        }
        inStream.close();
        out.println("Finished: " + new Date());
    }

    public static final int DEFAULT_PORT = 4000;
}
