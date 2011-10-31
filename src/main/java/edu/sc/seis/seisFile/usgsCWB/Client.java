package edu.sc.seis.seisFile.usgsCWB;

import edu.sc.seis.seisFile.MSeedQueryReader;

public class Client extends edu.sc.seis.seisFile.MSeedQueryClient {

    protected Client(MSeedQueryReader reader) {
        super(reader);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        Client client = new Client(new CWBReader());
        client.readData(args);
    }

    @Override
    public String getHelp() {
        return "java "
        + Client.class.getName()
        + " [-n net][-s sta][-l loc][-c chan][-b yyyy-MM-ddTHH:mm:ss.SSS][-d seconds][-h host][-p port][-o outfile][-m maxpackets][--verbose][--version][--help]";
    }
    
}
