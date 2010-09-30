package edu.sc.seis.seisFile.seedlink;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
        boolean verbose = false;
        DataOutputStream dos = null;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-n")) {
                network = args[i+1];
            } else if (args[i].equals("-s")) {
                station = args[i+1];
            } else if (args[i].equals("-l")) {
                location = args[i+1];
            } else if (args[i].equals("-c")) {
                channel = args[i+1];
            } else if (args[i].equals("-h")) {
                host = args[i+1];
            } else if (args[i].equals("-p")) {
                port = Integer.parseInt(args[i+1]);
            } else if (args[i].equals("-o")) {
                outFile = args[i+1];
                dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
            } else if (args[i].equals("-m")) {
                maxRecords = Integer.parseInt(args[i+1]);
                if (maxRecords < 1) {maxRecords = 1;}
            } else if (args[i].equals("--verbose")) {
                verbose = true;
            } else if (args[i].equals("--version")) {
                System.out.println(BuildVersion.getDetailedVersion());
                System.exit(0);
            } else if (args[i].equals("--help")) {
                System.out.println("java "+Client.class.getName()+" [-n net][-s sta][-l loc][-c chan][-h host][-p port][-o outfile][-m maxpackets][--verbose][--version][--help]");
                System.exit(0);
            }
        }
        List<String> config = new ArrayList<String>();
        config.add("STATION "+station+" "+network);
        config.add("SELECT "+location+channel+".D");
        config.add("DATA");
        SeedlinkReader reader = new SeedlinkReader(config, host, port, verbose);
        int i=0;
        Writer out = new OutputStreamWriter(System.out);
        while(i<maxRecords) {
            SeedlinkPacket slp = reader.next();
            DataRecord dr = slp.getMiniSeed();
            if (dos != null) {
                dr.write(dos);
            }
            if (verbose) {
                dr.writeASCII(out, "    ");
                out.flush();
            }
            i++;
        }
        if (dos != null) { dos.close(); }
        reader.close();
        System.out.println("Finished: "+new Date());
    }
}
