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

/**
 * Broke up the 'next' method into 'hasNext' and 'readPacket'.
 * Added 'getInfoString' methods to support getting the SeedLink information string. 
 * Added 'select' and 'startData' methods to support start and end time.
 */
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

    /**
     * Determine if a packet may be available.
     * This method blocks until input data is available, the end of the stream
     * is detected, or an exception is thrown.
     * @return true if a packet may be available, false if end was received.
     * @throws IOException if an I/O Exception occurs.
     * @see #readPacket()
     */
    public boolean hasNext() throws IOException {
        // check for closed connection and server sending END
        if (!isConnected()) {
            return false;
        }
        byte[] startBits = new byte[3];
        if (isVerbose()) {
            verboseWriter.println("hasNext(): blocking read for " + startBits.length + " bytes, available="
                    + in.available());
        }
        startBits[0] = (byte)in.read();
        startBits[1] = (byte)in.read();
        startBits[2] = (byte)in.read();
        String start = new String(startBits);
        if (start.equals("END")) {
            if (isVerbose()) {
                verboseWriter.println("hasNext(): end received");
            }
            return false;
        } else {
            in.unread(startBits);
            return true;
        }
    }

    /** true if there is enough data in the instream to possibly read a data record. 
     * This should not block, unlike hasNext() and next().
     * 
     */
    public boolean available() throws IOException {
        // check for closed connection and server sending END
        if (!isConnected()) {
            return false;
        }
        return in.available() > 256;
    }
    
    /** Returns available() from the underlying InputStream, in bytes
     * 
     */
    public int availableBytes() throws IOException {
        return in.available();
    }
    
	/**
	 * Get the SeedLink information string for streams.
	 * @return the SeedLink information string.
	 * 
	 * @return the SeedLink information string or null if error.
     * @throws IOException if an I/O Exception occurs.
     * @throws SeedlinkException if no packets or there is an error creating the packet.
     * @throws SeedFormatException if there is an error with the SEED format.
	 */
	public String getInfoString() throws IOException, SeedlinkException,
			SeedFormatException {
		return getInfoString(SeedlinkReader.INFO_STREAMS);
	}

	/**
	 * Get the SeedLink information string.
	 * @param infoType the information type.
	 * @return the SeedLink information string.
	 * 
	 * @return the SeedLink information string or null if error.
     * @throws IOException if an I/O Exception occurs.
     * @throws SeedlinkException if no packets or there is an error creating the packet.
     * @throws SeedFormatException if there is an error with the SEED format.
	 */
	public String getInfoString(String infoType) throws IOException, SeedlinkException,
			SeedFormatException {
		return getInfoString(infoType, true);
	}

	/**
	 * Get the SeedLink information string.
	 * @param infoType the information type.
	 * @param addNewlines true to add newlines to support XML parsing, false otherwise.
	 * @return the SeedLink information string.
	 * 
	 * @return the SeedLink information string or null if error.
     * @throws IOException if an I/O Exception occurs.
     * @throws SeedlinkException if no packets or there is an error creating the packet.
     * @throws SeedFormatException if there is an error with the SEED format.
	 */
	public String getInfoString(String infoType, boolean addNewlines) throws IOException, SeedlinkException,
			SeedFormatException {
		SeedlinkPacket infoPacket;
		String s;
		info(infoType);

		StringBuilder infoPacketContents = new StringBuilder();
		// ID only returns 1 packet, others might return more,
		// careful especially if data is flowing at the same time
		do {
			infoPacket = next();
			s = new String(infoPacket.getMiniSeed().getData());
			infoPacketContents.append(s);
		} while (infoPacket.isInfoContinuesPacket());
		String infoString = infoPacketContents.toString();
		if (addNewlines) {
			// add newlines to support XML parsing
			infoString = infoString.replaceAll("><", ">\n<").trim();
		}
		return infoString;
	}

    /**
     * Get the next packet.
     * This method blocks until input data is available, the end of the stream
     * is detected, or an exception is thrown.
     * @return the packet or null if none.
     * @throws IOException if an I/O Exception occurs.
     * @throws SeedlinkException if no packets or there is an error creating the packet.
     * @see #hasNext()
     * @see #readPacket()
     */
    public SeedlinkPacket next() throws IOException, SeedlinkException {
        // check for END
        if (!hasNext()) {
            throw new SeedlinkException("no more seed link packets from last command");
        }
        return readPacket();
    }

    /**
     * Read the next packet.
     * This method should be called after calling the 'hasNext' method.
     * This method blocks until input data is available, the end of the stream
     * is detected, or an exception is thrown.
     * @return the next packet.
     * @throws IOException if an I/O Exception occurs.
     * @throws SeedlinkException if there is an error creating the packet.
     * @see #hasNext()
     */
    public SeedlinkPacket readPacket() throws IOException, SeedlinkException
    {
        if (isVerbose()) {
            verboseWriter.println("readPacket(): blocking read for " + SeedlinkPacket.PACKET_SIZE + " bytes, available="
                    + in.available());
        }
        byte[] bits = new byte[SeedlinkPacket.PACKET_SIZE];
        inData.readFully(bits);
        SeedlinkPacket slp = new SeedlinkPacket(bits);
        if (isVerbose()) {
            String packetString = EMPTY;
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
        initConnection();
        for (String cmd : sentCommands) {
            internalSendCmd(cmd);
        }
        endHandshake();
    }

    public String[] sendHello() throws IOException, SeedlinkException {
        send("HELLO");
        String[] lines = new String[2];
        lines[0] = readLine();
        lines[1] = readLine();
        return lines;
    }

    protected String readLine() throws IOException, SeedlinkException {
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
     * Sends a SeedLink modifier command, generally should be limited to
     * @param cmd the command.
     * STATION, SELECT FETCH, DATA and TIME.
     * @throws SeedlinkException if a SeedLink error occurs.
     * @throws IOException if an I/O Exception occurs.
     */
    public void sendCmd(String cmd) throws IOException, SeedlinkException {
        internalSendCmd(cmd);
        sentCommands.add(cmd);
    }

    /**
     * Select the stream.
     * @param network the network.
     * @param station the station.
     * @param location the location or empty if none.
     * @param channel the channel.
     * @throws SeedlinkException if a SeedLink error occurs.
     * @throws IOException if an I/O Exception occurs.
     */
    public void select(String network, String station, String location, String channel) throws SeedlinkException, IOException {
    	select(network, station, location, channel, DATA_TYPE);
    }

    /**
     * Select the stream.
     * @param network the network.
     * @param station the station.
     * @param location the location or empty if none.
     * @param channel the channel.
     * @param type the data type.
     * @throws SeedlinkException
     * @throws IOException
     */
    public void select(String network, String station, String location, String channel, String type) throws SeedlinkException, IOException {
        sendCmd("STATION " + station + " " + network);
        sendCmd("SELECT " + location + channel + "." + type);
    }
 
    /**
     * Start the data transfer.
     * @throws SeedlinkException if a SeedLink error occurs.
     * @throws IOException if an I/O Exception occurs.
     */
    public void startData() throws SeedlinkException, IOException {
		startData(EMPTY, EMPTY);
    }

    /**
     * Start the data transfer. Note the DMC only goes back 48 hours.
     * The start and end time format is year,month,day,hour,minute,second,
     * e.g. '2002,08,05,14,00'.
     * @param start the start time or empty string if none.
     * @param end the end time or empty string if none (ignored if no start time.)
     * @throws SeedlinkException if a SeedLink error occurs.
     * @throws IOException if an I/O Exception occurs.
     */
    public void startData(String start, String end) throws SeedlinkException, IOException {
    	if (start.length() == 0) {
    		sendCmd(DATA_COMMAND);
    	} else if (end.length() == 0) {
    		sendCmd(TIME_COMMAND + " " + start);
    	} else {
    		sendCmd(TIME_COMMAND + " " + start + " " + end);
    	}
        endHandshake(); // let 'er rip
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

	public static final String EMPTY = "";

    public static final String DATA_TYPE = "D";

    public static final String DATA_COMMAND = "DATA";

    public static final String TIME_COMMAND = "TIME";

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
