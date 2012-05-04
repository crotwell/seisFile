package edu.sc.seis.seisFile.usgsCWB;

import edu.sc.seis.seisFile.QueryParams;
import edu.sc.seis.seisFile.SeisFileException;

public class Client extends edu.sc.seis.seisFile.MSeedQueryClient {

    protected Client(String[] args) throws SeisFileException {
        super(args);
        reader = new CWBReader();
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
        + " "+QueryParams.getStandardHelpOptions()+"[-h host][-p port]";
    }
    
}
