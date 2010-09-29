package edu.sc.seis.seisFile.seedlink;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;


public class Client {

    public static void main(String[] args) throws UnknownHostException, IOException, SeedlinkException, SeedFormatException {
        String network = "TA";
        String station = "*";
        String location = "";
        String channel = "BHZ";
        int maxRecords = 10;
        boolean verbose = false;
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-n")) {
                network = args[i+1];
            } else if (args[i].equals("-s")) {
                station = args[i+1];
            } else if (args[i].equals("-l")) {
                location = args[i+1];
            } else if (args[i].equals("-c")) {
                channel = args[i+1];
            } else if (args[i].equals("-m")) {
                maxRecords = Integer.parseInt(args[i+1]);
                if (maxRecords < 1) {maxRecords = 1;}
            } else if (args[i].equals("--verbose")) {
                verbose = true;
            }
        }
        List<String> config = new ArrayList<String>();
        config.add("STATION "+station+" "+network);
        config.add("SELECT "+location+channel+".D");
        config.add("DATA");
        SeedlinkReader reader = new SeedlinkReader(config, "rtserve.iris.washington.edu", 18000, verbose);
        int i=0;
        DataRecord firstdr = null;
        Date firstDate = null;
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("mini.seed")));
        SeedlinkPacket slp = null;
        while(i<maxRecords) {
            slp = reader.next();
            if (firstdr == null) {
                firstdr = reader.next().getMiniSeed();
                firstDate = new Date();
            }
            DataRecord dr = slp.getMiniSeed();
            dr.write(dos);
            i++;
        }
        dos.close();
        DataRecord lastdr = slp.getMiniSeed();
        System.out.println(firstDate+"  "+firstdr.getHeader().getEndTime());
        System.out.println(new Date()+"  "+lastdr.getHeader().getEndTime());
        reader.close();
    }
}
