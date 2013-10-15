package edu.sc.seis.seisFile.usgsCWB;

import java.util.ArrayList;
import java.util.List;

import edu.sc.seis.seisFile.MSeedQueryClient;
import edu.sc.seis.seisFile.QueryParams;
import edu.sc.seis.seisFile.SeisFileException;

public class Client extends MSeedQueryClient {

    protected Client(String[] args) throws SeisFileException {
        super(args);
        List<String> unknownArgs = params.getUnknownArgs();
        List<String> reallyUnknownArgs = new ArrayList<String>();
        String host = CWBReader.DEFAULT_HOST;
        int port = CWBReader.DEFAULT_PORT;
        int timeoutSec = 120;
        for (int i = 0; i < unknownArgs.size(); i++) {
            if (i < unknownArgs.size()-1) {
                if (unknownArgs.get(i).equals("-h")) {
                    host = unknownArgs.get(i + 1);
                } else if (unknownArgs.get(i).equals("-p")) {
                    port = Integer.parseInt(unknownArgs.get(i + 1));
                } else if (unknownArgs.get(i).equals("--timeout")) {
                    timeoutSec = Integer.parseInt(unknownArgs.get(i + 1));
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
        reader = new CWBReader(host, port, timeoutSec*1000);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        Client client = new Client(args);
        client.readData();
    }

    @Override
    public String getHelp() {
        return "java "
        + Client.class.getName()
        + " "+QueryParams.getStandardHelpOptions()+"[-h host][-p port][--timeout sec]";
    }
    
}
