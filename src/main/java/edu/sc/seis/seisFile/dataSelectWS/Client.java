package edu.sc.seis.seisFile.dataSelectWS;

import edu.sc.seis.seisFile.MSeedQueryClient;
import edu.sc.seis.seisFile.MSeedQueryReader;
import edu.sc.seis.seisFile.QueryParams;
import edu.sc.seis.seisFile.SeisFileException;


public class Client extends MSeedQueryClient {


    protected Client(String[] args) throws SeisFileException {
        super(args);
        String url = DataSelectReader.DEFAULT_WS_URL;
        int timeoutSec = 120;
        for (int i = 0; i < args.length; i++) {
            if (i < args.length-1) {
                if (args[i].equals("-u")) {
                    url = args[i+1];
                } else if (args[i].equals("--timeout")) {
                    timeoutSec = Integer.parseInt(args[i + 1]);
                }
            }
        }
        reader = new DataSelectReader(url, timeoutSec*1000);
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
        + " "+QueryParams.getStandardHelpOptions()+"[-u url][--timeout sec]";
    }
   
}
