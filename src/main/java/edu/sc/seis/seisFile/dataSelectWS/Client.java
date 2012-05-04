package edu.sc.seis.seisFile.dataSelectWS;

import edu.sc.seis.seisFile.MSeedQueryClient;
import edu.sc.seis.seisFile.MSeedQueryReader;
import edu.sc.seis.seisFile.QueryParams;
import edu.sc.seis.seisFile.SeisFileException;


public class Client extends MSeedQueryClient {


    protected Client(String[] args) throws SeisFileException {
        super(args);
        reader = new DataSelectReader();
        for (int i = 0; i < args.length; i++) {
            if (i < args.length-1 && args[i].equals("-u")) {
                reader = new DataSelectReader(args[i+1]);
            }
        }
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
        + " "+QueryParams.getStandardHelpOptions()+"[-u url]";
    }
   
}
