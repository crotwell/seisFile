package edu.sc.seis.seisFile.seedlink;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.Date;

import edu.sc.seis.seisFile.BuildVersion;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;

/**
 * Added support for an info output file and specifying a start and end time.
 */
public class Client {

    public static void printHelp(PrintWriter out) {
        out.println("java "
                    + Client.class.getName()
                    + " [-n net][-s sta][-l loc][-c chan][-h host][-p port][-o outfile][-m maxpackets][--timeout seconds][--verbose][--version][--help]"
                    + " [-iout info outfile][-start start time [-end end time]]");
    }
    
    public static void main(String[] args) throws UnknownHostException, IOException, SeedlinkException,
            SeedFormatException {
    	final String EMPTY = SeedlinkReader.EMPTY;
        String network = "TA";
        String station = "*";
        String location = EMPTY;
        String channel = "BHZ";
        String outFile = null;
        String host = SeedlinkReader.DEFAULT_HOST;
        String start = EMPTY;
        String end = EMPTY;
        int port = SeedlinkReader.DEFAULT_PORT;
        int maxRecords = 10;
        int timeoutSeconds = SeedlinkReader.DEFAULT_TIMEOUT_SECOND;
        String infoType = EMPTY;
        String ioutFile = EMPTY;
        boolean verbose = false;
        DataOutputStream dos = null;
        PrintWriter out = new PrintWriter(System.out, true);
        for (int i = 0; i < args.length; i++) {
            try {
            if (args[i].equals("-n")) {
                network = args[i + 1]; i++;
            } else if (args[i].equals("-s")) {
                station = args[i + 1]; i++;
            } else if (args[i].equals("-l")) {
                location = args[i + 1]; i++;
            } else if (args[i].equals("-c")) {
                channel = args[i + 1]; i++;
            } else if (args[i].equals("-i")) {
                infoType = args[i + 1]; i++;
            } else if (args[i].equals("-h")) {
                host = args[i + 1]; i++;
            } else if (args[i].equals("-p")) {
                port = Integer.parseInt(args[i + 1]); i++;
            } else if (args[i].equals("-o")) {
                outFile = args[i + 1]; i++;
                dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
            } else if (args[i].equals("-m")) {
                maxRecords = Integer.parseInt(args[i + 1]); i++;
                if (maxRecords < -1) {
                    maxRecords = -1;
                }
            } else if (args[i].equals("--timeout")) {
                timeoutSeconds = Integer.parseInt(args[i + 1]); i++;
            } else if (args[i].equals("--verbose")) {
                verbose = true;
            } else if (args[i].equals("--version")) {
                out.println(BuildVersion.getDetailedVersion());
                System.exit(0);
            } else if (args[i].equals("--help")) {
                printHelp(out);
                System.exit(0);
            } else if (args[i].equals("-start")) {
                start = args[i + 1]; i++;
            } else if (args[i].equals("-end")) {
                end = args[i + 1]; i++;
            } else if (args[i].equals("-iout")) {
            	ioutFile = args[i + 1]; i++;
            } else {
            	System.out.println("Unknown argument " + args[i]);
            }
            } catch(ArrayIndexOutOfBoundsException ex) {
                out.println("Argument requires parameter "+args[i]);
                printHelp(out);
                System.exit(1);
            } catch(Throwable ex) {
                // bad arg, so print help
                out.println("Bad argument "+args[i]);
                printHelp(out);
                System.exit(1);
            }
        }
        SeedlinkReader reader = new SeedlinkReader(host, port, timeoutSeconds, verbose);
        if (verbose) {
            reader.setVerboseWriter(out);
            String[] lines = reader.sendHello();
            out.println("line 1 :" + lines[0]);
            out.println("line 2 :" + lines[1]);
            out.flush();
        }
		if (infoType.length() != 0 || ioutFile.length() != 0)
		{
        	if (infoType.length() == 0) {
        		infoType = SeedlinkReader.INFO_STREAMS;
        	}
        	String infoString = reader.getInfoString(infoType);
        	if (ioutFile == null) {
        		out.print(infoString);
        	} else {
        		PrintWriter pw = null;
        		try {
        			pw = new PrintWriter(ioutFile);
        			pw.print(infoString);
        		}
        		finally {
        			if (pw != null) {
        				pw.close();
        			}
        		}
        	}
        }
        if (maxRecords != 0) {
        reader.select(network, station, location, channel);
        reader.startData(start, end);
        int i = 0;
        try {
            while ((maxRecords == -1 || i < maxRecords) && reader.isConnected()) {
                SeedlinkPacket slp = reader.next();
                DataRecord dr = slp.getMiniSeed();
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
        } catch(EOFException e) {
            // done I guess
        }
        }
        if (dos != null) {
            dos.close();
        }
        reader.close();
        out.println("Finished: " + new Date());
    }
}
