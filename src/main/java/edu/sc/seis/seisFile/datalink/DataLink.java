package edu.sc.seis.seisFile.datalink;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PushbackInputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Philip Crotwell
 * University of South Carolina, 2019
 * http://www.seis.sc.edu
 */
public class DataLink {


    /** default of IRIS DMC */
    public DataLink() throws DataLinkException {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    /** uses the default port of 18000 */
    public DataLink(String host) throws DataLinkException {
        this(host, DEFAULT_PORT);
    }

    public DataLink(String host, int port) throws DataLinkException {
        this(host, port, DEFAULT_TIMEOUT_SECOND);
    }

    public DataLink(String host, int port, int timeoutSeconds) throws DataLinkException {
        this(host, port, timeoutSeconds, false);
    }

    public DataLink(String host, int port, int timeoutSeconds, boolean verbose) throws DataLinkException {
        this.host = host;
        this.port = port;
        setVerbose(verbose);
        this.timeoutSeconds = timeoutSeconds;
        this.clientIdNum = 42;
        this.username = "unknown";
        initConnection();
    }


    private void initConnection() throws DataLinkException {
        try {
            verbose("initConnection to "+host+":"+port);
            socket = new Socket(host, port);
            socket.setSoTimeout(timeoutSeconds*1000);
            out = new BufferedOutputStream(socket.getOutputStream());
            in = new PushbackInputStream(new BufferedInputStream(socket.getInputStream()), 3);
            inData = new DataInputStream(in);
            mode = QUERY_MODE;
            verbose("Connection made");
            sendId();
            while (availableBytes() < 3) {
                verbose("not bytes available "+availableBytes());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }
            verbose("readPacket");
            DataLinkResponse dlr = readPacket();
            if (dlr.getKey().equals("ID") && dlr.headerSplit(1).equals("DataLink")) {
                this.serverId = dlr.getHeaderString();
            } else {
                verbose("Not ID response: "+dlr.getKey()+"  "+dlr.headerSplit(1)+"  "+dlr.getHeaderString());
            }
        } catch(UnknownHostException e) {
            throw new DataLinkException(e);
        } catch(IOException e) {
            throw new DataLinkException(e);
        }
    }

    /**
     * Read a packet.
     * This method blocks until input data is available, the end of the stream
     * is detected, or an exception is thrown.
     * @return packet if available.
     * @throws IOException if an I/O Exception occurs.
     * @throws DataLinkException if not connected
     * @see #readPacket()
     */
    public DataLinkResponse readPacket() throws IOException, DataLinkException {
        // check for closed connection and server sending END
        if (!isConnected()) {
            throw new DataLinkException("closed");
        }
        byte[] startBits = new byte[3];
        if (isVerbose()) {
            verboseWriter.println("readPacket(): blocking read for " + startBits.length + " bytes, available="
                    + availableBytes());
        }
        startBits[0] = (byte)in.read();
        startBits[1] = (byte)in.read();
        if (startBits[0] != 'D' || startBits[1] != 'L') {
            throw new DataLinkException("Expected DL as first two bytes but was :"+startBits[0]+" "+startBits[1]);
        }
        int headerSize = (byte)in.read();
        byte[] headerBytes = new byte[headerSize];
        inData.readFully(headerBytes);
        String headerStr = new String(headerBytes);
        DataLinkHeader header = new DataLinkHeader(headerStr);
        DataLinkResponse out = null;
        byte[] rawData = new byte[header.getDataSize()];
        inData.readFully(rawData);
        if (header.getKey().startsWith(PACKET)) {
            out = new DataLinkPacket(header, rawData);
        } else if (header.isMessageType()) {
            out = new DataLinkMessage(header, new String(rawData));
        } else {
            throw new DataLinkException("Unknown packet type: "+header.getKey());
        }
        return out;
    }

    public void stream() throws DataLinkException {
      if (this.mode.equals(STREAM_MODE)) {return;}
      this.sendDLCommand(STREAM, null);
      this.mode = STREAM_MODE;
    }

    public void endStream() throws DataLinkException {
      if (this.mode.equals(QUERY_MODE)) {return;}
      this.mode = QUERY_MODE;
      this.sendDLCommand(ENDSTREAM, null);

    }

    public void match(String matchRegEx) throws DataLinkException {
        if (this.mode == STREAM_MODE) {
            endStream();
        }
        this.matchRegEx = matchRegEx;
        sendDLCommand(MATCH, matchRegEx);
    }

    public void reject(String rejectRegEx) throws DataLinkException {
        if (this.mode == STREAM_MODE) {
            endStream();
        }
        this.rejectRegEx = rejectRegEx;
        sendDLCommand(REJECT, rejectRegEx);
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
        return in.available() > 3;
    }

    /** Returns available() from the underlying InputStream, in bytes
     *
     */
    public int availableBytes() throws IOException {
        return in.available();
    }

    public void close() {
        try {
            // just in case, doesn't hurt if not streaming
            endStream();
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

    /**
     * Would be really nice to keep state and reconnect plus backfill, but...
     *
     * @throws IOException
     * @throws DataLinkException
     */
    public void reconnect() throws IOException, DataLinkException {
        String prevMode = mode;
        close();
        initConnection();
        if (matchRegEx != null) {
            sendDLCommand(MATCH, matchRegEx);
        }
        if (rejectRegEx != null) {
            sendDLCommand(REJECT, rejectRegEx);
        }
        if (prevMode == STREAM_MODE) {
            stream();
        }
    }


/** encodes command with optional data section as
 * a string. This works for client generated commands
 * but not for a PACKET, which would have binary data.
 * PACKET is what client receives, but usually never
 * sends if it does not generate data.
 * @throws DataLinkException */
  public byte[] encodeDLCommand(String command, String dataString) throws DataLinkException {
    int cmdLen = command.length();
    int len = 3+command.length();
    String lenStr = "";
    if (dataString != null && dataString.length() > 0) {
      lenStr = ""+dataString.length();
      len+=lenStr.length()+1;
      cmdLen += lenStr.length()+1;
      len+=dataString.length();
    }
    if (len > 256) throw new DataLinkException("command string too long: "+len);

    byte[] packet = new byte[len];
    packet[0] = 'D'; // ascii D
    packet[1] = 'L'; // ascii L
    packet[2] = (byte)cmdLen;
    byte[] commandBytes = command.getBytes();
    System.arraycopy(commandBytes, 0, packet, 3, commandBytes.length);
    int i = 3+commandBytes.length;
    byte SPACE = ' ';
    if (dataString != null && dataString.length() > 0) {
      packet[i] =  SPACE; // ascii space
      i++;
      byte[] lenStrBytes = lenStr.getBytes();
      System.arraycopy(lenStrBytes, 0, packet, i, lenStrBytes.length);
      i += lenStrBytes.length;
      byte[] dataStringBytes = dataString.getBytes();
      System.arraycopy(dataStringBytes, 0, packet, i, dataStringBytes.length);
    }
    return packet;
  }



  /**
   * Sends a DL Command and awaits the response, either OK or ERROR.
   * @param command
   * @param dataString
   * @throws DataLinkException
   */
  public void sendDLCommand(String command, String dataString) throws DataLinkException {
      if ( ! this.mode.equals(QUERY_MODE)) {
          throw new DataLinkException("Cannot send command unless in QUERY_MODE, use endStream() first.");
      }
      if (isVerbose()) {
          verboseWriter.println("send '" + command+" | "+(dataString != null ? dataString : "") + "'");
      }
      byte[] rawPacket = this.encodeDLCommand(command, dataString);
    if (this.socket != null && this.out!= null) {
      try {
        this.out.write(rawPacket);
        this.out.flush();
    } catch (IOException e) {
        throw new DataLinkException(e);
    }
    } else {
      throw new DataLinkException("Socket has been closed.");
    }
  }

  public DataLinkResponse awaitDLCommand(String command, String dataString) throws DataLinkException {
      sendDLCommand(command, dataString);

      DataLinkResponse resp;
      try {
          resp = readPacket();
      } catch (IOException e) {
          throw new DataLinkException("Unable to get response to command: "+command, e);
      }
      if (resp.getKey().equals(OK)) {
          return resp;
      } else if (resp.getKey().equals(ERROR)) {
          return resp;
      } else {
          throw new DataLinkException("Unknown response, was expecting OK or ERROR");
      }
  }

  public void sendId() throws DataLinkException {
      verbose("sendId");
      sendDLCommand("ID seisFile:"+this.username+":"+this.clientIdNum+":java", null);
  }


    private PrintWriter verboseWriter;

    public void verbose(String message) {
        if (isVerbose()) {
            getVerboseWriter().println(message);
        }
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
        if (verbose && verboseWriter == null) {
            verboseWriter = new PrintWriter(System.out, true);
        }
    }

    public PrintWriter getVerboseWriter() {
        if (verboseWriter == null) {
            verboseWriter = new PrintWriter(System.out);
        }
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

    Socket socket;
    BufferedOutputStream out;
    PushbackInputStream in;
    DataInputStream inData;

    String url;
    String mode;
    String serverId;
    int clientIdNum;
    String username;

    String matchRegEx = null;
    String rejectRegEx = null;

    PacketHandler packetHandler;

    boolean verbose = false;

    String host;

    int port;

    int timeoutSeconds = DEFAULT_TIMEOUT_SECOND;


    public static final String IRIS_HOST = "rtserve.iris.washington.edu";
    public static final int IRIS_PORT = 18000;

    public static final String EEYORE_HOST = "eeyore.seis.sc.edu";
    public static final int EEYORE_PORT = 6383;

    public static final String DEFAULT_HOST = EEYORE_HOST;
    public static final int DEFAULT_PORT = EEYORE_PORT;

    public static final int DEFAULT_TIMEOUT_SECOND = 20;

    public static final String DATALINK_PROTOCOL = "1.0";
    public static final String QUERY_MODE = "QUERY";
    public static final String STREAM_MODE = "STREAM";
    public static final int MAX_PROC_NUM = 65534; // Math.pow(2, 16)-2;
    public static final String USER_BROWSER = "browser";

    public static final String ID = "ID";
    public static final String OK = "OK";
    public static final String ERROR = "ERROR";
    public static final String INFO = "INFO";
    public static final String MATCH = "MATCH";
    public static final String REJECT = "REJECT";
    public static final String PACKET = "PACKET";
    public static final String STREAM = "STREAM";
    public static final String ENDSTREAM = "ENDSTREAM";
    public static final String MSEED_TYPE = "MSEED";


}
