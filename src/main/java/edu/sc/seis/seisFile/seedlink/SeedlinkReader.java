package edu.sc.seis.seisFile.seedlink;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PushbackInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import edu.sc.seis.seisFile.TimeUtils;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;


/**
 * Reader for the seedlink protocol in multistation mode. Protocol documentation can be found at
 * <a href="https://www.seiscomp.de/seiscomp3/doc/applications/seedlink.html">seedlink</a>
 * Note this implementatino assumes multistation mode. It may be possible to use this in single station mode, but
 * this has not been tested.
 */
public class SeedlinkReader {

    /** default of IRIS DMC */
    public SeedlinkReader() throws IOException {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    /** default of IRIS DMC */
    public SeedlinkReader(boolean verbose) throws IOException {
        this(DEFAULT_HOST, DEFAULT_PORT, verbose);
    }
    
    /** uses the default port of 18000 */
    public SeedlinkReader(String host) throws IOException {
        this(host, DEFAULT_PORT);
    }

    /** uses the default port of 18000 */
    public SeedlinkReader(String host, boolean verbose) throws IOException {
        this(host, DEFAULT_PORT, verbose);
    }

    public SeedlinkReader(String host, int port) throws IOException {
        this(host, port, DEFAULT_TIMEOUT_SECOND);
    }

    public SeedlinkReader(String host, int port, boolean verbose) throws IOException {
        this(host, port, DEFAULT_TIMEOUT_SECOND, verbose);
    }

    public SeedlinkReader(String host, int port, int timeoutSeconds) throws IOException {
        this(host, port, timeoutSeconds, false);
    }

    public SeedlinkReader(String host, int port, int timeoutSeconds, boolean verbose) throws IOException {
        this(host, port, timeoutSeconds, verbose, timeoutSeconds);
    }

    public SeedlinkReader(String host, int port, int timeoutSeconds, boolean verbose, int connectTimeoutSeconds) throws IOException {
        setVerbose(verbose);
        this.timeoutSeconds = timeoutSeconds;
        this.connectTimeoutSeconds = connectTimeoutSeconds;
        this.seedlinkState = new SeedlinkState(host, port, sentCommands);
        initConnection();
    }
    public SeedlinkReader(SeedlinkState state) {
        this(state, DEFAULT_TIMEOUT_SECOND, false, DEFAULT_TIMEOUT_SECOND);
    }

    public SeedlinkReader(SeedlinkState state, int timeoutSeconds, boolean verbose, int connectTimeoutSeconds) {
        setVerbose(verbose);
        this.timeoutSeconds = timeoutSeconds;
        this.connectTimeoutSeconds = connectTimeoutSeconds;
        this.sentCommands = state.getCommandList();
        this.lastSeqNum = state.getGlobalLastSequence();
        this.seedlinkState = state;
    }
    
    private void initConnection() throws IOException {
        if (isVerbose()) {
            getVerboseWriter().println("Init SeedLink to "+getHost()+" "+getPort());
        }
        socket = new Socket();
        socket.connect(new InetSocketAddress(getHost(), getPort()), this.connectTimeoutSeconds*1000);
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
        startBits[0] = (byte)in.read();
        startBits[1] = (byte)in.read();
        startBits[2] = (byte)in.read();
        String start = new String(startBits);
        if (start.equals("END")) {
            if (isVerbose()) {
                getVerboseWriter().println("END received");
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
        if (debug) {
            getVerboseWriter().println("readPacket(): blocking read for " + SeedlinkPacket.PACKET_SIZE + " bytes, available="
                    + in.available());
        }
        byte[] bits = new byte[SeedlinkPacket.PACKET_SIZE];
        inData.readFully(bits);
        SeedlinkPacket slp = new SeedlinkPacket(bits);
        lastSeqNum = slp.getSeqNum();
        seedlinkState.updateGlobalSequence(lastSeqNum);
        if (isVerbose()) {
            String packetString = EMPTY;
            try {
                DataRecord dr = slp.getMiniSeed();
                packetString = " Got a packet: " + slp.getSeqNum() + "  " + dr.getHeader().getNetworkCode() + "  "
                        + dr.getHeader().getStationIdentifier() + "  " + dr.getHeader().getLocationIdentifier() + "  "
                        + dr.getHeader().getChannelIdentifier() + "  " + dr.getHeader().getStartTime()
                        +" "+dr.getHeader().getNumSamples();
            } catch(SeedFormatException e) {
                packetString = "SeedFormatExcpetion parsing packet: " + slp.getSeqNum() + e.getMessage();
            }
            getVerboseWriter().println(packetString);
        }
        return slp;
    }

    /**
     * send an INFO command. The resulting packets can be retrieved with calls
     * to next(), checking isInfoContinuesPacket() to see if more packets will
     * be returned. INFO
     * requests should probably not be sent after the end of the handshake as
     * real data packets may arrive causing confusion. GetInfoString() is
     * preferred as it iterates over all returned packets and
     * returns the result as a single String.
     */
    public void info(String level) throws IOException {
        send("INFO " + level);
    }

    /**
     * Ends the handshaking phase and starts data flowing.
     * @throws IOException
     */
    public void endHandshake() throws IOException {
        send("END");
    }

    public void close() {
        if (out != null) {
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

    /**
     * Does a simple reconnect, replaying the original commands and restarting data. This does
     * not resume from the last packet, so data may be lost that arrived during the outage. See
     * resume to reconnect and resume data flow from the last successful packet.
     *
     * @throws IOException
     * @throws SeedlinkException
     */
    public void reconnect() throws IOException, SeedlinkException {
        close();
        initConnection();
        for (String cmd : sentCommands) {
            internalSendCmd(cmd);
        }
        endHandshake();
    }

    /**
     * Resumes the connection at the last successful packet using the global last sequence number. Note that this assumes
     * that sequence numbers are global, not per station. Each DATA or TIME command in the original command
     * sequence is replaced with 'DATA seq' to restart with the last received sequence number.
     * If the server uses per station sequence numbering
     * then the client must keep track of sequence numbers and use resume(SeedlinkState) to supply the sequence numbers.
     *
     * @deprecated use resumeGlobalSequence
     * @throws IOException
     * @throws SeedlinkException
     */
    @Deprecated
    public void resume() throws IOException, SeedlinkException {
        close();
        initConnection();
        String nextSeqCmd = DATA_COMMAND + " " + nextSeq(seedlinkState.getGlobalLastSequence());
        for (String cmd : sentCommands) {
            if (! (cmd.startsWith(DATA_COMMAND) || cmd.startsWith(TIME_COMMAND))) {
                internalSendCmd(cmd);
            } else {
                internalSendCmd(nextSeqCmd);
            }
        }
        endHandshake();
    }

    public static SeedlinkReader resumeGlobalSequence(SeedlinkState seedlinkState) throws IOException, SeedlinkException {
        return resumeGlobalSequence(seedlinkState, false);
    }

    /**
     * Resumes the connection at the last successful packet using the global last sequence number. Note that this assumes
     * that sequence numbers are global, not per station. Each DATA or TIME command in the original command
     * sequence is replaced with 'DATA seq' to restart with the last received sequence number,
     * which should work with wildcards..
     * If the server uses per station sequence numbering
     * then the client must keep track of sequence numbers and use resume(SeedlinkState) to supply the sequence numbers,
     * possibly with custom commands to handle wildcards.
     *
     *
     * @throws IOException
     * @throws SeedlinkException
     */
    public static SeedlinkReader resumeGlobalSequence(SeedlinkState seedlinkState, boolean verbose) throws IOException, SeedlinkException {
        SeedlinkReader reader = new SeedlinkReader(seedlinkState);
        reader.setVerbose(verbose);
        reader.initConnection();
        String nextSeqCmd = DATA_COMMAND + " " + nextSeq(seedlinkState.getGlobalLastSequence());
        for (String cmd : seedlinkState.getCommandList()) {
            if (! (cmd.startsWith(DATA_COMMAND) || cmd.startsWith(TIME_COMMAND))) {
                reader.internalSendCmd(cmd);
            } else {
                reader.internalSendCmd(nextSeqCmd);
            }
        }
        return reader;
    }

    /**
     * Resumes the connection at the last successful packet using the sequence number. Note that this
     * uses a supplied per station last seq number. The map key is of the form
     * NN.SSSSS where NN is the network code and SSSSS is the station code. It is also assumed that there is
     * a DATA or TIME command in the original handshaking phase after each STATION command. Some servers allow
     * multiple STATION commands before a DATA command, but this breaks restarting each station at the
     * last sequence. The caller should call endHandshake() after creationg in order to begin data flow.
     * This does not function correctly if wildcards have been used in the original request as there is no
     * guarantee that all stations/channels that might match the wildcards have actually been seen before the
     * interruption, and so the wildcard commands may need to be repeated. Resuming after STATION comamnds with wildcards
     * should be handled externally by the client.
     *
     * @throws IOException
     * @throws SeedlinkException
     */
    public static SeedlinkReader resume(SeedlinkState seedlinkState, boolean verbose) throws IOException, SeedlinkException {
        for (String cmd : seedlinkState.getCommandList()) {
            if (cmd.startsWith("STATION") && containsWildcard(cmd)) {
                throw new SeedlinkException("Unable to resume from per-station sequence numbers when using STATION wildcards:" +
                        cmd);
            }
        }
        SeedlinkReader reader = new SeedlinkReader(seedlinkState);
        reader.setVerbose(verbose);
        reader.initConnection();
        if (reader.isVerbose()) {
            for (String k : seedlinkState.keySet()) {
                reader.getVerboseWriter().println("Resume: '" + k + "' after " + seedlinkState.getMap().get(k));
            }
        }
        String lastStation = null;
        String lastNet = null;
        for (String cmd : seedlinkState.getCommandList()) {
            if (cmd.startsWith("STATION")) {
                if (lastStation != null) {
                    // repeated STATION without a DATA or TIME
                    // try to send DATA anyway?
                    if (seedlinkState.contains(lastNet, lastStation)) {
                        String resumeCmd = createResumeDataCommand( lastNet,  lastStation, seedlinkState);
                        reader.internalSendCmd(resumeCmd);
                    } else {
                        reader.internalSendCmd(DATA_COMMAND);
                    }
                    lastStation = null;
                    lastNet = null;
                }
                String[] splitCmd = cmd.split(" +");
                lastStation = splitCmd[1];
                lastNet = splitCmd[2];
            }
            if (! (cmd.startsWith(DATA_COMMAND) || cmd.startsWith(TIME_COMMAND))) {
                reader.internalSendCmd(cmd);
            } else {
                if (seedlinkState.contains(lastNet, lastStation)) {
                    String resumeCmd = createResumeDataCommand( lastNet,  lastStation, seedlinkState);
                    reader.internalSendCmd(resumeCmd);
                } else {
                    System.out.println("Last for station not found: "+lastStation);
                    reader.internalSendCmd(DATA_COMMAND);
                }
                lastStation = null;
            }
        }
        return reader;
    }

    public static String createResumeDataCommand(String lastNet, String lastStation, SeedlinkState state) {
        if (state.contains(lastNet, lastStation)) {
            String staSeq = state.getForStation(lastNet, lastStation);
            return DATA_COMMAND + " " + nextSeq(staSeq);
        } else {
            System.out.println("Last for station not found: "+lastStation);
            return DATA_COMMAND;
        }
    }
    public static String nextSeq(String seq) {
        String next = Integer.toHexString(Integer.parseInt(seq, 16) +1).toUpperCase();
        while (next.length()<6) { next = "0"+next;}
        return next;
    }

    public static boolean containsWildcard(String cmd) {
        return cmd.contains("*") || cmd.contains("?");
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
        while (next != '\r' && next != '\n' && in.available() > 0) {
            // only do the verbose printing when either hit end of line or
            // no more chars available
            buf.append((char) next);
            next = in.read();
            if (next == -1) {
                // oops, no more bytes in stream
                throw new SeedlinkException("read returned -1, socket closed??? buf="+buf);
            }
        }
        if (isVerbose()) {
            getVerboseWriter().println("recv '"+buf+"'");
        }

        // next should now be a \n, so just eat it
        // next == -1 means stream closed, so skip unread
        next = in.read();
        if (next != '\n' && next != '\r' && next != -1 ) {
            in.unread(next);
        }
        return buf.toString();
    }

    /**
     * Sends a SeedLink modifier command, generally should be limited to
     * STATION, SELECT, FETCH, DATA and TIME.
     *
     * @param cmd the command.
     * @throws SeedlinkException if a SeedLink error occurs.
     * @throws IOException if an I/O Exception occurs.
     */
    public void sendCmd(String cmd) throws IOException, SeedlinkException {
        internalSendCmd(cmd);
        sentCommands.add(cmd);
    }

    /**
     * Select the stream. This sends a STATION followed by a SELECT command.
     * @deprecated use selectData() or selectTime() instead
     * @param network the network.
     * @param station the station.
     * @param location the location or empty if none.
     * @param channel the channel.
     * @throws SeedlinkException if a SeedLink error occurs.
     * @throws IOException if an I/O Exception occurs.
     */
    @Deprecated
    public void select(String network, String station, String location, String channel) throws SeedlinkException, IOException {
    	select(network, station, location, channel, DATA_TYPE);
    }

    public void selectData(String network, String station, List<String> locchan) throws SeedlinkException, IOException {
        selectDataOfType(network, station, locchan, DATA_TYPE);
    }


    /**
     * Utility function to select the stream. This sends a STATION followed by a SELECT command.
     *
     * @deprecated use selectData() or selectTime() instead
     * @param network the network.
     * @param station the station.
     * @param locchan list of location channel.
     * @param type the data type.
     * @throws SeedlinkException
     * @throws IOException
     */
    public void selectOfType(String network, String station, List<String> locchan, String type) throws SeedlinkException, IOException {
        sendStation(network, station);
        if (locchan.isEmpty()) {
            sendSelect("???");
            sendSelect("?????");
        }
        for (String lc : locchan) {
            sendSelect(lc, type);
        }
    }

    public void selectDataOfType(String network, String station, List<String> locchan, String type) throws SeedlinkException, IOException {
        selectOfType(network, station, locchan, type);
        sendData();
    }

    public void selectData(String network, String station, List<String> locchan, String seqNum) throws SeedlinkException, IOException {
        selectDataOfType(network, station, locchan, seqNum, DATA_TYPE);
    }

    public void selectDataOfType(String network, String station, List<String> locchan, String seqNum, String type) throws SeedlinkException, IOException {
        selectOfType(network, station, locchan, type);
        sendData(seqNum);
        if ( ! (containsWildcard(network) || containsWildcard(station))) {
            seedlinkState.put(network, station, seqNum);
        }
    }

    public void selectData(String network, String station, List<String> locchan, Instant start) throws SeedlinkException, IOException {
        selectDataOfType(network, station, locchan, start, DATA_TYPE);
    }

    public void selectDataOfType(String network, String station, List<String> locchan, Instant start, String type) throws SeedlinkException, IOException {
        selectOfType(network, station, locchan, type);
        sendData(start);
    }

    public void selectTime(String network, String station, List<String> locchan, Instant start) throws SeedlinkException, IOException {
        selectTimeOfType(network, station, locchan, start, null, DATA_TYPE);
    }

    public void selectTime(String network, String station, List<String> locchan, Instant start, Instant end) throws SeedlinkException, IOException {
        selectTimeOfType(network, station, locchan, start, end, DATA_TYPE);
    }

    public void selectTimeOfType(String network, String station, List<String> locchan, Instant start, Instant end, String type) throws SeedlinkException, IOException {
        selectOfType(network, station, locchan, type);
        sendTime(start, end);
    }

    /**
     * Utility function to select the stream. This sends a STATION followed by a SELECT command.
     *
     * @deprecated use selectData() or selectTime() instead
     * @param network the network.
     * @param station the station.
     * @param location the location or empty if none.
     * @param channel the channel.
     * @param type the data type.
     * @throws SeedlinkException
     * @throws IOException
     */
    @Deprecated
    public void select(String network, String station, String location, String channel, String type) throws SeedlinkException, IOException {
        if (channel.isEmpty()) {channel = "???";}
        sendStation(network, station);
        sendSelect(location + channel, type);
    }

    /**
     * Send a STATION command for the given network and station. If either is length zero, a wildcard of "*"
     * is inserted in its place.
     * In the Seedlink protocol, a STATION command should be followed by one or more
     * SELECT commands. See sendSelect().
     *
     * @param network the network.
     * @param station the station.
     * @throws SeedlinkException
     * @throws IOException
     */
    public void sendStation(String network, String station) throws SeedlinkException, IOException {
        if (network.isEmpty()) {network = "*";}
        if (station.isEmpty()) {station = "*";}
        sendCmd("STATION " + station + " " + network);
    }

    /**
     * Send a SELECT command for the given location-channel and with type of 'D'.
     * In the Seedlink protocol, a STATION command should be followed by one or more
     * SELECT commands. See sendStation().
     * @param locationChannel the combined location and channel, eg 00HHZ.
     * @throws SeedlinkException
     * @throws IOException
     */
    public void sendSelect(String locationChannel) throws SeedlinkException, IOException {
        sendSelect( locationChannel, DATA_TYPE);
    }

    /**
     * Send a SELECT command for the given location-channel and type.
     * In the Seedlink protocol, one or more SELECT commands should follow a STATION command. See
     * sendStation().
     * @param locationChannel the combined location and channel, eg 00HHZ.
     * @param type the data type.
     * @throws SeedlinkException
     * @throws IOException
     */
    public void sendSelect(String locationChannel, String type) throws SeedlinkException, IOException {
        if (locationChannel.isEmpty()) {locationChannel = "*";}
        sendCmd("SELECT " + locationChannel + "." + type);
    }

    /**
     * Start the data transfer.
     * @deprecated use repeated selectData() or selectTime() followed by endHandshake()
     * @throws SeedlinkException if a SeedLink error occurs.
     * @throws IOException if an I/O Exception occurs.
     */
    @Deprecated
    public void startData() throws SeedlinkException, IOException {
		startData(EMPTY, EMPTY);
    }


    /**
     * Start the data transfer. Note the DMC only goes back 48 hours.
     * The start and end time format is year,month,day,hour,minute,second,
     * e.g. '2002,08,05,14,00'.
     * @deprecated use repeated selectTime() followed by endHandshake()
     * @param start the start time or null if none.
     * @param end the end time or null if none (ignored if no start time.)
     * @throws SeedlinkException if a SeedLink error occurs.
     * @throws IOException if an I/O Exception occurs.
     */
    @Deprecated
    public void startData(Instant start, Instant end) throws SeedlinkException, IOException {
        DateTimeFormatter seedlinkFormat = TimeUtils.createFormatter("yyyy,MM,dd,HH,mm,ss");
        String startStr = start == null ? "" : seedlinkFormat.format(start);
        String endStr = end == null ? "" : seedlinkFormat.format(end);
        startData(startStr, endStr);
    }
    
    /**
     * Start the data transfer. Note the DMC only goes back 48 hours.
     * The start and end time format is year,month,day,hour,minute,second,
     * e.g. '2002,08,05,14,00'.
     * @deprecated use repeated selectData() followed by endHandshake()
     * @param start the start time or empty string if none.
     * @param end the end time or empty string if none (ignored if no start time.)
     * @throws SeedlinkException if a SeedLink error occurs.
     * @throws IOException if an I/O Exception occurs.
     */
    @Deprecated
    public void startData(String start, String end) throws SeedlinkException, IOException {
    	if (start == null || start.isEmpty()) {
    		sendCmd(DATA_COMMAND);
    	} else if (end == null || end.isEmpty()) {
    		sendCmd(TIME_COMMAND + " " + start);
    	} else {
    		sendCmd(TIME_COMMAND + " " + start + " " + end);
    	}
        endHandshake(); // let 'er rip
    }

    public void sendData() throws SeedlinkException, IOException {
        sendCmd(DATA_COMMAND);
    }

    public void sendData(String seqNum) throws SeedlinkException, IOException {
        sendCmd(DATA_COMMAND+" "+seqNum);
    }

    public void sendData(Instant start) throws SeedlinkException, IOException {
        sendData("000000", start);
    }

    public void sendData(String seqNum, Instant start) throws SeedlinkException, IOException {
        DateTimeFormatter seedlinkFormat = TimeUtils.createFormatter("yyyy,MM,dd,HH,mm,ss");
        String startStr = start == null ? "" : seedlinkFormat.format(start);
        sendCmd(DATA_COMMAND+" "+seqNum+" "+startStr);
    }

    public void sendTime(Instant start) throws SeedlinkException, IOException {
        sendTime(start, null);
    }

    public void sendTime(Instant start, Instant end) throws SeedlinkException, IOException {
        DateTimeFormatter seedlinkFormat = TimeUtils.createFormatter("yyyy,MM,dd,HH,mm,ss");
        String startStr = start == null ? "" : seedlinkFormat.format(start);
        String endStr = end == null ? "" : seedlinkFormat.format(end);
        sendCmd(TIME_COMMAND+" "+startStr+" "+endStr);
    }

    /**
     *
     * @deprecated use repeated selectData(String network, String station, List&lt;String&gt; locchan, int seqNum) followed by endHandshake()
     * @param seqNum
     * @throws SeedlinkException
     * @throws IOException
     */
    @Deprecated
    public void restartData(String seqNum) throws SeedlinkException, IOException {
        lastSeqNum = seqNum;
        seedlinkState.updateGlobalSequence(seqNum);
        sendCmd(DATA_COMMAND+" "+lastSeqNum);
        endHandshake();
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
            getVerboseWriter().println("send '" + cmd + "'");
        }
        out.write((cmd + "\r").getBytes());
        out.flush();
    }

    PushbackInputStream in;

    DataInputStream inData;

    BufferedOutputStream out;

    private Socket socket;

    boolean verbose = false;

    boolean debug = false;
    
    int timeoutSeconds;

    int connectTimeoutSeconds;
    
    List<String> sentCommands = new ArrayList<>();

    SeedlinkState seedlinkState;

    private PrintWriter verboseWriter;

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
        if (verboseWriter == null) {
            verboseWriter = new PrintWriter(System.out, true);
        }
    }

    public PrintWriter getVerboseWriter() {
        if (this.verbose && verboseWriter == null) {
            verboseWriter = new PrintWriter(System.out, true);
        }
        return verboseWriter;
    }

    public void setVerboseWriter(PrintWriter verboseWriter) {
        this.verboseWriter = verboseWriter;
    }

    public String getHost() {
        return seedlinkState.getHost();
    }

    public int getPort() {
        return seedlinkState.getPort();
    }

    /**
     * SeedlinkState is a utility
     * object to allow the client to keep track of the last sequence number for each station and the sequence
     * of commands that affect data that were sent to the server. SeedlinkReader does not automatically
     * keep track of these per station sequence numbers as it requires parsing the miniseed records. Clients that
     * desire this should call SeedlinkState.update(SeedlinkPacket) to update the per station sequence number.
     */
    public SeedlinkState getState() {
        return seedlinkState;
    }

    public String lastSeqNum = "";

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
