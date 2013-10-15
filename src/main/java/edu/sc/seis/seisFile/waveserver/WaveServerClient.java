package edu.sc.seis.seisFile.waveserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import edu.sc.seis.seisFile.BuildVersion;
import edu.sc.seis.seisFile.MSeedQueryClient;
import edu.sc.seis.seisFile.QueryParams;
import edu.sc.seis.seisFile.SeisFileException;

public class WaveServerClient extends MSeedQueryClient {

    public WaveServerClient(String[] args) throws SeisFileException {
        super(args);
        List<String> unknownArgs = params.getUnknownArgs();
        List<String> reallyUnknownArgs = new ArrayList<String>();
        for (int i = 0; i < unknownArgs.size(); i++) {
            if (unknownArgs.get(i).equals("--steim1")) {
                doSteim1 = true;
            } else if (unknownArgs.get(i).equals("--menu")) {
                doMenu = true;
            } else if (i < unknownArgs.size() - 1) {
                // arg with value
                if (unknownArgs.get(i).equals("--recLen")) {
                    recordSize = Integer.parseInt(unknownArgs.get(i + 1));
                    i++;
                } else if (unknownArgs.get(i).equals("-h") || unknownArgs.get(i).equals("--help")) {
                    host = unknownArgs.get(i + 1);
                    i++;
                } else if (unknownArgs.get(i).equals("-p")) {
                    port = Integer.parseInt(unknownArgs.get(i + 1));
                    i++;
                } else {
                    reallyUnknownArgs.add(unknownArgs.get(i));
                }
            } else {
                reallyUnknownArgs.add(unknownArgs.get(i));
            }
        }
        if (reallyUnknownArgs.size() != 0) {
            String s = "";
            for (String a : reallyUnknownArgs) {
                s += " "+a;
            }
            System.out.println("Unknown args: "+s);
            System.out.println(getHelp());
            System.exit(-1);
        }
        if (params.getOutFile() == null) {
            params.setOutFile("output.mseed");
        }
        reader = new WaveServer(host, port);
        ((WaveServer)reader).setDoSteim1(doSteim1);
        ((WaveServer)reader).setRecordSize(recordSize);
        
    }

    public static void main(String[] args) throws Exception {
        WaveServerClient client = new WaveServerClient(args);
        client.readData();
    }

    public void readData() throws IOException, SeisFileException {
        if (!doMenu
                && (params.getNetwork() == null || params.getStation() == null || params.getChannel() == null)) {
            System.out.println(BuildVersion.getDetailedVersion() + " one of scnl is null: n=" + params.getNetwork()
                    + " s=" + params.getStation() + " l=" + params.getLocation() + " c=" + params.getChannel());
            System.out.println("LocId null is ok for scn, but needed for scnl");
            return;
        }
        if (doMenu) {
            List<MenuItem> ans = ((WaveServer)reader).getMenu();
            Date now = new Date();
            for (MenuItem item : ans) {
                long latency = (now.getTime() - item.getEndDate().getTime()) / 1000;
                System.out.println(item + "  " + latency + " sec");
            }
        } else {
            super.readData();
        }
    }

    public String getHelp() {
        return "java "
                + WaveServerClient.class.getName()
                + " "+QueryParams.getStandardHelpOptions()+"[-h host][-p port][--menu][--steim1][--recLen len(8-12)]";
    }

    String host = "eeyore.seis.sc.edu";

    int port = 16022;

    int recordSize = 12;

    boolean doSteim1 = false;

    boolean doMenu = false;
}
