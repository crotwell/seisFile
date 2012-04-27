package edu.sc.seis.seisFile.waveserver;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import edu.sc.seis.seisFile.BuildVersion;
import edu.sc.seis.seisFile.QueryParams;
import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.winston.TraceBuf2;

public class WaveServerClient {

    public WaveServerClient(String[] args) throws SeisFileException {
        params = new QueryParams(args);
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--steim1")) {
                doSteim1 = true;
            } else if (args[i].equals("--menu")) {
                doMenu = true;
            } else if (i < args.length - 1) {
                // arg with value
                if (args[i].equals("--recLen")) {
                    recordSize = Integer.parseInt(args[i + 1]);
                } else if (args[i].equals("-h")) {
                    host = args[i + 1];
                } else if (args[i].equals("-p")) {
                    port = Integer.parseInt(args[i + 1]);
                }
            }
        }
        if (params.getOutFile() == null) {
            params.setOutFile("output.mseed");
        }
    }

    public static void main(String[] args) throws Exception {
        WaveServerClient client = new WaveServerClient(args);
        client.readData();
    }

    public void readData() throws IOException, SeedFormatException {
        if (params.isPrintHelp()) {
            System.out.println(getHelp());
            return;
        } else if (params.isPrintVersion()) {
            System.out.println("Version: " + BuildVersion.getDetailedVersion());
            return;
        } else if (!doMenu
                && (params.getNetwork() == null || params.getStation() == null || params.getChannel() == null)) {
            System.out.println(BuildVersion.getDetailedVersion() + " one of scnl is null: n=" + params.getNetwork()
                    + " s=" + params.getStation() + " l=" + params.getLocation() + " c=" + params.getChannel());
            System.out.println("LocId null is ok for scn, but needed for scnl");
            return;
        }
        WaveServer ws = new WaveServer(host, port);
        if (params.isVerbose()) {
            ws.setVerbose(params.isVerbose());
        }
        if (doMenu) {
            List<MenuItem> ans = ws.getMenu();
            Date now = new Date();
            for (MenuItem item : ans) {
                long latency = (now.getTime() - item.getEndDate().getTime()) / 1000;
                System.out.println(item + "  " + latency + " sec");
            }
        } else {
            DataOutputStream dos = params.getDataOutputStream();
            List<TraceBuf2> tbList = ws.getTraceBuf(params.getNetwork(),
                                                    params.getStation(),
                                                    params.getLocation(),
                                                    params.getChannel(),
                                                    params.getBegin(),
                                                    params.getEnd());
            for (TraceBuf2 traceBuf2 : tbList) {
                if (params.isVerbose()) {
                    System.out.println("tracebuf2 " + traceBuf2.getNetwork() + "." + traceBuf2.getStation() + "."
                            + traceBuf2.getLocId() + "." + traceBuf2.getChannel() + " " + traceBuf2.getStartDate()
                            + " " + traceBuf2.getNumSamples());
                }
                DataRecord mseed = traceBuf2.toMiniSeed(recordSize, doSteim1);
                mseed.write(dos);
            }
            dos.close();
        }
    }

    public String getHelp() {
        return "java "
                + WaveServerClient.class.getName()
                + " [-h host][-p port][--menu][-n net][-s sta][-l loc][-c chan][-b yyyy-MM-dd[THH:mm:ss.SSS]][-e yyyy-MM-dd[THH:mm:ss.SSS]][-d seconds][-o outfile][-m maxpackets][--steim1][--recLen len(8-12)][--verbose][--version][--help]";
    }

    QueryParams params;

    String host = "eeyore.seis.sc.edu";

    int port = 16022;

    int recordSize = 12;

    boolean doSteim1 = false;

    boolean doMenu = false;
}
