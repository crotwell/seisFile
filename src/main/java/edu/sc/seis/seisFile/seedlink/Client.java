package edu.sc.seis.seisFile.seedlink;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.sc.seis.seisFile.BuildVersion;
import edu.sc.seis.seisFile.mseed.Blockette;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;


public class Client {

    public static void main(String[] args) throws UnknownHostException, IOException, SeedlinkException, SeedFormatException {
        String network = "TA";
        String station = "*";
        String location = "";
        String channel = "BHZ";
        String outFile = null;
        String host = SeedlinkReader.DEFAULT_HOST;
        int port = SeedlinkReader.DEFAULT_PORT;
        int maxRecords = 10;
        String infoType = "";
        boolean verbose = false;
        DataOutputStream dos = null;
        PrintWriter out = new PrintWriter(System.out, true);
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-n")) {
                network = args[i+1];
            } else if (args[i].equals("-s")) {
                station = args[i+1];
            } else if (args[i].equals("-l")) {
                location = args[i+1];
            } else if (args[i].equals("-c")) {
                channel = args[i+1];
            } else if (args[i].equals("-i")) {
                infoType = args[i+1];
            } else if (args[i].equals("-h")) {
                host = args[i+1];
            } else if (args[i].equals("-p")) {
                port = Integer.parseInt(args[i+1]);
            } else if (args[i].equals("-o")) {
                outFile = args[i+1];
                dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
            } else if (args[i].equals("-m")) {
                maxRecords = Integer.parseInt(args[i+1]);
                if (maxRecords < -1) {maxRecords = -1;}
            } else if (args[i].equals("--verbose")) {
                verbose = true;
            } else if (args[i].equals("--version")) {
                out.println(BuildVersion.getDetailedVersion());
                System.exit(0);
            } else if (args[i].equals("--help")) {
                out.println("java "+Client.class.getName()+" [-n net][-s sta][-l loc][-c chan][-h host][-p port][-o outfile][-m maxpackets][--verbose][--version][--help]");
                System.exit(0);
            }
        }
        SeedlinkReader reader = new SeedlinkReader(host, port, verbose);
        if (verbose) {
            reader.setVerboseWriter(out);
            String[] lines = reader.sendHello();
            out.println("line 1 :"+lines[0]);
            out.println("line 2 :"+lines[1]);
            out.flush();
        }
        if (infoType != null && infoType.length() != 0) {
            reader.info(infoType);
            SeedlinkPacket infoPacket;
            // ID only returns 1 packet, others might return more, careful
            // especially if data is flowing at the same time
            do {
                infoPacket = reader.next(); 
                infoPacket.getMiniSeed().writeASCII(out, "    ");
                out.println("    "+new String(infoPacket.getMiniSeed().getData()));
            } while( infoPacket.isInfoContinuesPacket());
        }
        
        reader.sendCmd("STATION "+station+" "+network);
        reader.sendCmd("SELECT "+location+channel+".D");
        // use one of DATA and FETCH
        // DATA for realtime
        // FETCH to start flow at a specific time
        // DMC only goes back 48 hours so update date to something more recent
       // reader.sendCmd("FETCH 0 2010,09,30,12,00,00"); 
        reader.sendCmd("DATA");
        reader.endHandshake(); // let 'er rip
        int i=0;
        try {
            while((maxRecords == -1 || i<maxRecords) && reader.isConnected()) {
                SeedlinkPacket slp = reader.next();
                DataRecord dr = slp.getMiniSeed();
                if (dos != null) {
                    dr.write(dos);
                }
                if (dos == null || verbose) {
                    // print something to the screen if we are not saving to disk
                    dr.writeASCII(out, "    ");
                    out.flush();
                }
                i++;
            }
        } catch (EOFException e) {
            // done I guess
        }
        if (dos != null) { dos.close(); }
        reader.close();
        out.println("Finished: "+new Date());
    }
}
