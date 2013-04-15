package edu.sc.seis.seisFile.fdsnws.dataSelectWS;

import edu.sc.seis.seisFile.MSeedQueryClient;
import edu.sc.seis.seisFile.MSeedQueryReader;
import edu.sc.seis.seisFile.QueryParams;
import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.StringMSeedQueryReader;
import edu.sc.seis.seisFile.dataSelectWS.BulkDataSelectReader;
import edu.sc.seis.seisFile.dataSelectWS.Client;
import edu.sc.seis.seisFile.dataSelectWS.DataSelectReader;


public class Client extends MSeedQueryClient {


    protected Client(String[] args) throws SeisFileException {
        super(args);
        String url = DataSelectReader.DEFAULT_WS_URL;
        int timeoutSec = 120;
        for (int i = 0; i < args.length; i++) {
            if (i < args.length-1) {
                if (args[i].equals("-u")) {
                    url = args[i+1];
                } else if (i < args.length-1 && args[i].equals("--timeout")) {
                    timeoutSec = Integer.parseInt(args[i + 1]);
                }
            }
            if (args[i].equals("--bulk")) {
                bulk = true;
                if (null == url || url.equals(DataSelectReader.DEFAULT_WS_URL)) {
                    url = BulkDataSelectReader.DEFAULT_WS_URL;
                }
            }
        }
        if (bulk) {
            reader = new BulkDataSelectReader(url, timeoutSec*1000);
        } else {
            reader = new DataSelectReader(url, timeoutSec*1000);
        }
        reader.setTimed(params.isTimed());
        
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
        + " "+QueryParams.getStandardHelpOptions()+"[-u url][--bulk][--timeout sec]";
    }

    boolean bulk = false;
    
}
