package edu.sc.seis.seisFile.dataSelectWS;

import edu.sc.seis.seisFile.MSeedQueryClient;
import edu.sc.seis.seisFile.MSeedQueryReader;


public class Client extends MSeedQueryClient {


    protected Client(MSeedQueryReader reader) {
        super(reader);
    }
    
    

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        DataSelectReader reader = new DataSelectReader();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-u")) {
                reader = new DataSelectReader(args[i+1]);
            }
        }
        Client client = new Client(reader);
        client.readData(args);
    }

    @Override
    public String getHelp() {
        return "java "
        + Client.class.getName()
        + " [-n net][-s sta][-l loc][-c chan][-b yyyy-MM-ddTHH:mm:ss.SSS][-d seconds][-u url][-o outfile][-m maxpackets][--verbose][--version][--help]";
    }
   
}
