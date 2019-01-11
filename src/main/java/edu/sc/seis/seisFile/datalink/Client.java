package edu.sc.seis.seisFile.datalink;

import java.io.IOException;

public class Client {

    public static void main(String[] args) throws DataLinkException, IOException {
        Client c = new Client();
        c.run();
    }

    public void run() throws DataLinkException, IOException {
        DataLink dl = new DataLink(DataLink.EEYORE_HOST, DataLink.EEYORE_PORT, 20, true);
        System.out.println("Server ID: "+dl.serverId);
        dl.match("CO_BIRD_00_HHZ");
        dl.stream();
        DataLinkResponse response = null;
        for(int i=0; i<10; i++) {
            response = dl.readPacket();
            if (response instanceof DataLinkPacket) {
                handlePacket((DataLinkPacket)response);
            } else {
                System.out.println("Response: "+response.getKey());
            }
        }
        dl.endStream();
        response = dl.readPacket();
        while (response != null && response instanceof DataLinkPacket) {
            handlePacket((DataLinkPacket)response);
            response = dl.readPacket();
        }
        if ( ! response.getKey().equals(DataLink.ENDSTREAM)) {
            System.err.println("Expected ENDSTREAM, but got "+response.getKey());
        }
        dl.close();
    }
   
    
    public static void handlePacket(DataLinkPacket packet) {

        System.out.println(" Packet: "+packet.streamId+"  "+packet.hppacketstart);
    }

}
