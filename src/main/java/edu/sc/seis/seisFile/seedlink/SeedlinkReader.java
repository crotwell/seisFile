package edu.sc.seis.seisFile.seedlink;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;


public class SeedlinkReader {
    
    public SeedlinkReader(List<String> config) throws UnknownHostException, IOException, SeedlinkException {
        this(config, "rtserve.iris.washington.edu");
    }
    
    /** uses the default port of 18000*/
    public SeedlinkReader(List<String> config, String host) throws UnknownHostException, IOException, SeedlinkException {
        this(config, host, 18000);
    }

    public SeedlinkReader(List<String> config, String host, int port) throws UnknownHostException, IOException, SeedlinkException {
        this(config, host, port, false);
    }
    
    public SeedlinkReader(List<String> config, String host, int port, boolean verbose) throws UnknownHostException, IOException, SeedlinkException {
        this.host = host;
        this.port = port;
        this.verbose = verbose;
        if (verbose) { System.out.println("configure connection to "+host+" at port="+port); }
        socket = new Socket(host, port);
        out = new BufferedOutputStream(socket.getOutputStream());
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        if (verbose) {
            String[] lines = sendHello();
            System.out.println("line 1 :"+lines[0]);
            System.out.println("line 2 :"+lines[1]);
        }
        for (String command : config) {
            sendCmd(command);
        }
        endHandshake();
    }
    
    public SeedlinkPacket next() throws IOException {
        if (verbose) { System.out.println("next(): blocking read for "+SeedlinkPacket.PACKET_SIZE+" bytes, available="+in.available()); }
        byte[] bits = new byte[SeedlinkPacket.PACKET_SIZE];
        in.readFully(bits);
        SeedlinkPacket slp = new SeedlinkPacket(bits);
        if (verbose) {
            DataRecord dr;
            try {
                dr = slp.getMiniSeed();
                System.out.println(" Got a packet: "+slp.getSeqNum()+
                                   "  "+ dr.getHeader().getNetworkCode()+
                                   "  "+ dr.getHeader().getStationIdentifier()+
                                   "  "+ dr.getHeader().getLocationIdentifier()+
                                   "  "+ dr.getHeader().getChannelIdentifier()+
                                   "  "+ dr.getHeader().getStartTime());
            } catch(SeedFormatException e) {
                e.printStackTrace();
            }
        }
        return slp;
    }
    
    void endHandshake() throws IOException {
        send("END");
    }
    
    public void close() throws IOException {
        send("BYE");
        in.close();
        out.close();
        socket.close();
    }
    
    String[] sendHello() throws IOException, SeedlinkException {
        send("HELLO");
        String[] lines = new String[2];
        lines[0] = readLine();
        lines[1] = readLine();
        return lines;
    }
    
    String readLine() throws IOException, SeedlinkException {
        StringBuffer buf = new StringBuffer();
        int next = in.read();
        while (next !=  '\r') {
            buf.append((char)next);
            if (verbose) { System.out.println(buf); }
            next = in.read();
            if (next == -1) {
                // oops, no more bytes in stream
                throw new SeedlinkException("read returned -1, socket closed???");
            }
        }
        //next should now be a \n, so just eat it
        next = in.read();
        if(next != '\n') {
            throw new SeedlinkException("Got \r but not followed by \n :"+next);
        }
        return buf.toString();
    }
    
    void sendCmd(String cmd) throws IOException, SeedlinkException {
        send(cmd);
        String line = readLine();
        if ( ! line.equals("OK")) {
            throw new SeedlinkException("Command "+cmd+" did not return OK");
        }
    }
    
    void send(String cmd) throws IOException {
        if (verbose) { System.out.println("send '"+cmd+"'"); }
        out.write((cmd+"\r").getBytes());
        out.flush();
    }
    
    DataInputStream in;
    BufferedOutputStream out;
    private Socket socket;
    
    boolean verbose = false;
    
    String host;
    
    int port;
}
