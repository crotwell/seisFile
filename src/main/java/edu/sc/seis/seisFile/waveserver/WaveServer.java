package edu.sc.seis.seisFile.waveserver;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.sc.seis.seisFile.winston.TraceBuf2;

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

    public WaveServer(String host, int port) throws UnknownHostException, IOException {
        this.host = host;
        this.port = port;
        socket = new Socket(getHost(), getPort());
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
    }

    public List<MenuItem> getMenu() throws IOException {
        List<MenuItem> result = new ArrayList<MenuItem>();
        String nextReqId = getNextRequestId();
        out.println("MENU: " + nextReqId + " SCNL");
        String all = in.readLine(); // newline ends reply
        String[] sections = all.split("  "); // double space separates entries
        String returnReqId = sections[0];
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

    public List<TraceBuf2> getTraceBuf(String network,
                                       String station,
                                       String location,
                                       String channel,
                                       Date start,
                                       Date end) throws IOException {
        List<TraceBuf2> ans = new ArrayList<TraceBuf2>();
        String nextReqId = getNextRequestId();
        DecimalFormat df = new DecimalFormat("0.0###"); 
        String cmd = "GETSCNLRAW: " + nextReqId + " " + station + " " + channel + " " + network + " "
                + (location == null ? "--" : location) + " " + df.format(start.getTime() / 1000.0) + " " + df.format(end.getTime() / 1000.0);
        if(isVerbose()) {
            System.out.println("send cmd: "+cmd);
        }
        out.println(cmd);
        out.flush();
        in.mark(64);
        int nRead = 0;
        while(in.available() > 0 && nRead < 64) {
            System.out.print((char)in.read());
            nRead++;
        }
        in.reset();
        String all = in.readLine(); // newline ends ascii part of reply
        if(isVerbose()) {
            System.out.println("response: "+all);
        }
        String[] splitLine = all.split(" ");
        int byteLengthIndex = 10;
        if ("F".equals(splitLine[6])) {
            int numBytes = Integer.parseInt(splitLine[byteLengthIndex]);
            int totSamples = 0;
            int totSize = 0;
            while (totSize < numBytes) {
                if (verbose) {
                    System.out.println("Read next traceBuf2: "+totSize + "<" + numBytes);
                }
                byte[] headerByte = new byte[64];
                in.readFully(headerByte);
                String dataType = TraceBuf2.extractDataType(headerByte);
                boolean swapBytes = TraceBuf2.isSwapBytes(dataType);
                int numSamples = TraceBuf2.extractNumSamples(headerByte, swapBytes);
                totSamples += numSamples;
                int sampSize = TraceBuf2.getSampleSize(dataType);
                byte[] dataBuf = new byte[numSamples * sampSize];
                in.readFully(dataBuf);
                byte[] allTraceBuf = new byte[headerByte.length + dataBuf.length];
                System.arraycopy(headerByte, 0, allTraceBuf, 0, headerByte.length);
                System.arraycopy(dataBuf, 0, allTraceBuf, headerByte.length, dataBuf.length);
                TraceBuf2 tb = new TraceBuf2(allTraceBuf);
                totSize += tb.getSize();
                if (isVerbose()) {
                    System.out.println("TraceBuf received: "+tb);
                }
                ans.add(tb);
            }
        }
        return ans;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    protected Socket getSocket() {
        return socket;
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

    Socket socket;

    PrintWriter out;

    DataInputStream in;

    boolean verbose = false;
}
