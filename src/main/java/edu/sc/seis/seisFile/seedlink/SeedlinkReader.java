package edu.sc.seis.seisFile.seedlink;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PushbackInputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;

public class SeedlinkReader {

    /** default of IRIS DMC */
    public SeedlinkReader() throws UnknownHostException, IOException {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }
    
    /** uses the default port of 18000 */
    public SeedlinkReader(String host) throws UnknownHostException, IOException {
        this(host, DEFAULT_PORT);
    }

    public SeedlinkReader(String host, int port) throws UnknownHostException, IOException {
        this(host, port, DEFAULT_TIMEOUT_SECOND);
    }

    public SeedlinkReader(String host, int port, int timeoutSeconds) throws UnknownHostException, IOException {
        this(host, port, timeoutSeconds, false);
    }

    public SeedlinkReader(String host, int port, int timeoutSeconds, boolean verbose) throws UnknownHostException, IOException {
        this.host = host;
        this.port = port;
        this.verbose = verbose;
        this.timeoutSeconds = timeoutSeconds;
        initConnection();
    }
    
    private void initConnection() throws UnknownHostException, IOException {
        socket = new Socket(host, port);
        socket.setSoTimeout(timeoutSeconds*1000);
        out = new BufferedOutputStream(socket.getOutputStream());
        in = new PushbackInputStream(new BufferedInputStream(socket.getInputStream()), 3);
        inData = new DataInputStream(in);
    }

    public boolean hasNext() throws IOException {
        // check for closed connection and server sending END
        if (!isConnected()) {
            return false;
        }
        byte[] startBits = new byte[3];
        startBits[0] = (byte)in.read();
        startBits[1] = (byte)in.read();
        startBits[2] = (byte)in.read();
        String start = new String(startBits);
        if (start.equals("END")) {
            return false;
        } else {
            in.unread(startBits);
            return true;
        }
    }

    public SeedlinkPacket next() throws IOException, SeedlinkException {
        if (isVerbose()) {
            verboseWriter.println("next(): blocking read for " + SeedlinkPacket.PACKET_SIZE + " bytes, available="
                    + in.available());
        }
        // check for END
        if (!hasNext()) {
            throw new SeedlinkException("no more seed link packets from last command");
        }
        byte[] bits = new byte[SeedlinkPacket.PACKET_SIZE];
        inData.readFully(bits);
        SeedlinkPacket slp = new SeedlinkPacket(bits);
        if (isVerbose()) {
            String packetString = "";
            try {
                DataRecord dr = slp.getMiniSeed();
                packetString = " Got a packet: " + slp.getSeqNum() + "  " + dr.getHeader().getNetworkCode() + "  "
                        + dr.getHeader().getStationIdentifier() + "  " + dr.getHeader().getLocationIdentifier() + "  "
                        + dr.getHeader().getChannelIdentifier() + "  " + dr.getHeader().getStartTime();
            } catch(SeedFormatException e) {
                packetString = "SeedFormatExcpetion parsing packet: " + slp.getSeqNum() + e.getMessage();
            }
            verboseWriter.println(packetString);
        }
        return slp;
    }

    /**
     * send an INFO command. The resulting packets can be retrieved with calls
     * to next(), although it seems there is no good way to determine how many
     * packets will be returned or when they have all arrived without parsing
     * the xml. This appears to be a shortcoming of the seedlink protocol. INFO
     * requests should probably not be sent after the end of the handshake as
     * real data packets may arrive causing confusion.
     */
    public void info(String level) throws IOException {
        send("INFO " + level);
    }

    public void endHandshake() throws IOException {
        send("END");
    }

    public void close() {
        try {
            send("BYE");
        } catch (Throwable se) {
            // oh well, already closed
        }
        try {
            in.close();
            out.close();
        } catch (Throwable se) {
            // oh well, already closed
        }
        try {
            socket.close();
        } catch (Throwable se) {
            // oh well, already closed
        }
        socket = null;
        in = null;
        out = null;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public void reconnect() throws IOException, SeedlinkException {
        close();
        List<String> oldSent = sentCommands;
        sentCommands = new ArrayList<String>();
        initConnection();
        for (String cmd : oldSent) {
            sendCmd(cmd);
        }
        endHandshake();
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
        while (next != '\r') {
            while (next != '\r' && in.available() > 0) {
                // only do the verbose printing when either hit end of line or
                // no more chars available
                buf.append((char)next);
                next = in.read();
                if (next == -1) {
                    // oops, no more bytes in stream
                    throw new SeedlinkException("read returned -1, socket closed???");
                }
            }
            if (isVerbose()) {
                verboseWriter.println(buf);
            }
        }
        // next should now be a \n, so just eat it
        next = in.read();
        if (next != '\n') {
            throw new SeedlinkException("Got \\r but not followed by \\n buf: '"+buf.toString()+"' next: " + next);
        }
        return buf.toString();
    }

    /**
     * sends a seedlink modifier command, generally should be limited to
     * STATION, SELECT FETCH and DATA. TIME may work but has not been tested.
     */
    public void sendCmd(String cmd) throws IOException, SeedlinkException {
        internalSendCmd(cmd);
        sentCommands.add(cmd);
    }

    protected void internalSendCmd(String cmd) throws IOException, SeedlinkException {
        send(cmd);
        String line = readLine();
        if (!line.equals("OK")) {
            throw new SeedlinkException("Command " + cmd + " did not return OK");
        }
    }

    void send(String cmd) throws IOException {
        if (isVerbose()) {
            verboseWriter.println("send '" + cmd + "'");
        }
        out.write((cmd + "\r").getBytes());
        out.flush();
    }

    PushbackInputStream in;

    DataInputStream inData;

    BufferedOutputStream out;

    private Socket socket;

    boolean verbose = false;

    String host;

    int port;
    
    int timeoutSeconds;
    
    List<String> sentCommands = new ArrayList<String>();

    private PrintWriter verboseWriter;

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
        if (verboseWriter == null) {
            verboseWriter = new PrintWriter(System.out);
        }
    }

    public PrintWriter getVerboseWriter() {
        return verboseWriter;
    }

    public void setVerboseWriter(PrintWriter verboseWriter) {
        this.verboseWriter = verboseWriter;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public static final String DEFAULT_HOST = "rtserve.iris.washington.edu";

    public static final int DEFAULT_PORT = 18000;
    
    public static final int DEFAULT_TIMEOUT_SECOND = 120;

    public static final String INFO_ID = "ID";

    public static final String INFO_CAPABILITIES = "CAPABILITIES";

    public static final String INFO_STATIONS = "STATIONS";

    public static final String INFO_STREAMS = "STREAMS";

    public static final String INFO_GAPS = "GAPS";

    public static final String INFO_CONNECTIONS = "CONNECTIONS";

    public static final String INFO_ALL = "ALL";
}
