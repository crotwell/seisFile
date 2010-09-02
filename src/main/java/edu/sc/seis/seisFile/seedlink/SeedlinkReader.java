package edu.sc.seis.seisFile.seedlink;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;


public class SeedlinkReader {
    
    public SeedlinkReader(List<String> config) throws UnknownHostException, IOException, SeedlinkException {
        this(config, "rtserve.iris.washington.edu");
    }
    
    /** uses the default port of 18000*/
    public SeedlinkReader(List<String> config, String host) throws UnknownHostException, IOException, SeedlinkException {
        this(config, host, 18000);
    }
    
    public SeedlinkReader(List<String> config, String host, int port) throws UnknownHostException, IOException, SeedlinkException {
        this.host = host;
        this.port = port;
        socket = new Socket(host, port);
        out = new BufferedOutputStream(socket.getOutputStream());
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        reader = new BufferedReader(new InputStreamReader(in));
        String[] lines = sendHello();
        System.out.println("line 1 :"+lines[0]);
        System.out.println("line 2 :"+lines[1]);
        for (String command : config) {
            sendCmd(command);
        }
        sendCmd("DATA");
        endHandshake();
    }
    
    public SeedlinkPacket next() throws IOException {
        byte[] bits = new byte[SeedlinkPacket.PACKET_SIZE];
        in.readFully(bits);
        return new SeedlinkPacket(bits);
    }
    
    void endHandshake() throws IOException {
        out.write(("END\r").getBytes());
        out.flush();
        reader = null;
    }
    
    public void close() throws IOException {
        out.write("BYE\r".getBytes());
        out.flush();
        in.close();
        out.close();
        socket.close();
    }
    
    String[] sendHello() throws IOException, SeedlinkException {
        out.write(("HELLO\r").getBytes());
        out.flush();
        String[] lines = new String[2];
        lines[0] = reader.readLine();
        lines[1] = reader.readLine();
        return lines;
    }
    
    void sendCmd(String cmd) throws IOException, SeedlinkException {
        out.write((cmd+"\r").getBytes());
        out.flush();
        String line = reader.readLine();
        if ( ! line.equals("OK")) {
            throw new SeedlinkException("Command "+cmd+" did not return OK");
        }
    }
    
    BufferedReader reader;
    DataInputStream in;
    BufferedOutputStream out;
    private Socket socket;
    String host;
    
    int port;
}
