package edu.sc.seis.seisFile.waveserver;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.iris.dmc.seedcodec.B1000Types;
import edu.sc.seis.seisFile.ChannelTimeWindow;
import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.TimeUtils;
import edu.sc.seis.seisFile.earthworm.TraceBuf2;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;

public class WaveServer {

    /**
     * Mainly for testing
     * 
     * @param out
     * @param in
     */
    WaveServer(PrintWriter out, DataInputStream in) {
        this.out = out;
        this.in = in;
    }

    public WaveServer(String host, int port) {
        this(host, port, DEFAULT_TIMEOUT_SECONDS);
    }

    public WaveServer(String host, int port, int timeoutSeconds) {
        this.host = host;
        this.port = port;
        this.timeoutSeconds = timeoutSeconds;
    }

    public List<MenuItem> getMenu() throws IOException {
        List<MenuItem> result = new ArrayList<MenuItem>();
        String nextReqId = getNextRequestId();
        sendCmd("MENU: " + nextReqId + " SCNL");
        String all = new BufferedReader(new InputStreamReader(getIn())).readLine(); // newline ends reply
        String[] sections = all.split("  "); // double space separates entries
        for (int i = 1; i < sections.length; i++) {
            String[] cols = sections[i].split(" +");
            String pin = cols[0];
            String s = cols[1];
            String c = cols[2];
            String n = cols[3];
            String l = "";
            int locOffset = 0;
            if (cols.length == 8) {
                l = cols[4];
                locOffset = 1;
            }
            String start = cols[locOffset + 4];
            String end = cols[locOffset + 5];
            String dataType = cols[locOffset + 6];
            MenuItem item = new MenuItem(n,
                                         s,
                                         l,
                                         c,
                                         Double.parseDouble(start),
                                         Double.parseDouble(end),
                                         Integer.parseInt(pin),
                                         dataType);
            result.add(item);
        }
        return result;
    }

    public List<TraceBuf2> getTraceBuf(ChannelTimeWindow window) throws IOException {
        return getTraceBuf(window.getNetwork(),
                           window.getStation(),
                           window.getLocation(),
                           window.getChannel(),
                           window.getBeginTime(),
                           window.getEndTime());
    }

    public List<TraceBuf2> getTraceBuf(String network,
                                       String station,
                                       String location,
                                       String channel,
                                       Instant start,
                                       Instant end) throws IOException {
        String cmd = createQuery(network, station, location, channel, start, end);
        return getTraceBuf(cmd);
    }
    
    public List<TraceBuf2> getTraceBuf(String cmd) throws IOException {
        List<TraceBuf2> ans = new ArrayList<TraceBuf2>();
        sendCmd(cmd);
        DataInputStream dataIn = getIn();
        String all = dataIn.readLine(); // newline ends ascii part of reply
        if (isVerbose()) {
            System.out.println("response: " + all);
        }
        String[] splitLine = all.split(" ");
        int byteLengthIndex = 10;
        if ("F".equals(splitLine[6])) {
            int numBytes = Integer.parseInt(splitLine[byteLengthIndex]);
            int totSize = 0;
            while (totSize < numBytes) {
                if (verbose) {
                    System.out.println("Read next traceBuf2: read bytes" + totSize + " < " + numBytes+" total bytes");
                }
                byte[] headerByte = new byte[64];
                in.readFully(headerByte);
                String dataType = TraceBuf2.extractDataType(headerByte);
                boolean swapBytes = TraceBuf2.isSwapBytes(dataType);
                int numSamples = TraceBuf2.extractNumSamples(headerByte, swapBytes);
                int sampSize = TraceBuf2.getSampleSize(dataType);
                byte[] dataBuf = new byte[numSamples * sampSize];
                dataIn.readFully(dataBuf);
                byte[] allTraceBuf = new byte[headerByte.length + dataBuf.length];
                System.arraycopy(headerByte, 0, allTraceBuf, 0, headerByte.length);
                System.arraycopy(dataBuf, 0, allTraceBuf, headerByte.length, dataBuf.length);
                TraceBuf2 tb = new TraceBuf2(allTraceBuf);
                totSize += tb.getSize();
                if (isVerbose()) {
                    System.out.println("TraceBuf received: " + tb);
                }
                ans.add(tb);
            }
        }
        return ans;
    }
    
    
    
    void sendCmd(String cmd) throws IOException {
        if (isVerbose()) {
            System.out.println("send cmd: " + cmd);
        }
        PrintWriter socketOut = getOut();
        // manually use newline as println line terminator is os specific 
        socketOut.print(cmd+"\n"); 
        socketOut.flush();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    protected Socket getSocket() throws IOException {
        if (socket == null && host != null) {
            socket = new Socket();
            socket.connect(new InetSocketAddress(getHost(), getPort()), this.connectTimeoutSeconds*1000);
            socket.setSoTimeout(timeoutSeconds * 1000);
        }
        return socket;
    }

    public int getRecordSize() {
        return recordSizeExp;
    }

    public void setRecordSize(int recordSize) {
        if (recordSize > 255) {
            throw new IllegalArgumentException("Record size exponent cannot be larger than 255. 12 is SEED standard 4096.");
        }
        this.recordSizeExp = recordSize;
    }

    public boolean isDoSteim1() {
        return doSteim1;
    }

    public void setDoSteim1(boolean doSteim1) {
        this.doSteim1 = doSteim1;
    }

    public PrintWriter getOut() throws IOException {
        if (out == null && getSocket() != null) {
            out = new PrintWriter(getSocket().getOutputStream(), true);
        }
        return out;
    }

    public DataInputStream getIn() throws IOException {
        if (in == null && getSocket() != null) {
            in = new DataInputStream(new BufferedInputStream(getSocket().getInputStream()));
        }
        return in;
    }

    public static int getDefaultTimeoutSeconds() {
        return DEFAULT_TIMEOUT_SECONDS;
    }

    public String createQuery(String network, String station, String location, String channel, Instant begin, Instant end) {
        String nextReqId = getNextRequestId();
        DecimalFormat df = new DecimalFormat("0.0###", new DecimalFormatSymbols(Locale.US));
        String cmd = "GETSCNLRAW: " + nextReqId + " " + station + " " + channel + " " + network + " "
                + ((location == null || location == "") ? "--" : location) + " " + df.format(TimeUtils.instantToEpochSeconds(begin)) + " "
                + df.format(TimeUtils.instantToEpochSeconds(end));
        return cmd;
    }

    public List<DataRecord> read(String query) throws IOException, SeisFileException, SeedFormatException {
        List<DataRecord> out = new ArrayList<DataRecord>();
        List<TraceBuf2> tbList = getTraceBuf(query);
        for (TraceBuf2 traceBuf2 : tbList) {
            if (verbose) {
                System.out.println("tracebuf2 " + traceBuf2.getNetwork() + "." + traceBuf2.getStation() + "."
                        + traceBuf2.getLocId() + "." + traceBuf2.getChannel() + " " + traceBuf2.getStartDate() + " "
                        + traceBuf2.getNumSamples());
            }
            List<DataRecord> mseed;
            if (doSteim1) {
                mseed = traceBuf2.toMiniSeed(recordSizeExp, B1000Types.STEIM1);
            } else {
                mseed = traceBuf2.toMiniSeedNoCompression(recordSizeExp);
            }
            out.addAll(mseed);
        }
        return out;
    }

    String getNextRequestId() {
        return "" + reqId++;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    int reqId = 1;

    String host;

    int port;

    int recordSizeExp = 12;

    boolean doSteim1 = false;

    Socket socket;

    PrintWriter out;

    DataInputStream in;
    
    int timeoutSeconds = DEFAULT_TIMEOUT_SECONDS;

    int connectTimeoutSeconds = DEFAULT_TIMEOUT_SECONDS;;

    public static final int DEFAULT_TIMEOUT_SECONDS = 60;

    boolean verbose = false;

}
