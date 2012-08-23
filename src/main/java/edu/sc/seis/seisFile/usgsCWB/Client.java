package edu.sc.seis.seisFile.usgsCWB;

import edu.sc.seis.seisFile.MSeedQueryClient;
import edu.sc.seis.seisFile.QueryParams;
import edu.sc.seis.seisFile.SeisFileException;

public class Client extends MSeedQueryClient {

    protected Client(String[] args) throws SeisFileException {
        super(args);
        String host = CWBReader.DEFAULT_HOST;
        int port = CWBReader.DEFAULT_PORT;
        int timeoutSec = 120;
        for (int i = 0; i < args.length; i++) {
            if (i < args.length-1) {
                if (args[i].equals("-h")) {
                    host = args[i + 1];
                } else if (args[i].equals("-p")) {
                    port = Integer.parseInt(args[i + 1]);
                } else if (args[i].equals("--timeout")) {
                    timeoutSec = Integer.parseInt(args[i + 1]);
                }
            }
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
