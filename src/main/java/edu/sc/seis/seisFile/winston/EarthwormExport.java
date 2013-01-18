package edu.sc.seis.seisFile.winston;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

public class EarthwormExport {

    public EarthwormExport(int port, 
                           int module, 
                           int institution, 
                           final String heartbeatMessage, 
                           final int heartbeatSeconds) throws UnknownHostException,
            IOException {
        this.port = port;
        this.module = module;
        this.institution = institution;
        this.heartbeatMessage = heartbeatMessage;
        initSocket();
        heartbeater = new EarthwormHeartbeater(null, heartbeatSeconds, heartbeatMessage, institution, module);
    }
    
    /** mostly just for testing */
    EarthwormExport(EarthwormEscapeOutputStream outStream, int module, int institution) {
        this.outStream = outStream;
        this.module = module;
        this.institution = institution;
        heartbeater = new EarthwormHeartbeater(outStream, 10, heartbeatMessage, institution, module);
    }

    public void export(TraceBuf2 traceBuf) throws IOException {
        if (outStream == null) {
            waitForClient();
        }
        traceBufSent++;
        if (traceBuf.getSize() > TraceBuf2.MAX_TRACEBUF_SIZE) {
            List<TraceBuf2> split = traceBuf.split(TraceBuf2.MAX_TRACEBUF_SIZE);
            if (verbose) {
                System.out.println("TraceBuf too large: "+traceBuf.getSize()+" split into "+split.size()+" pieces.");
            }
            splitTraceBufSent++;
            for (TraceBuf2 splitTB : split) {
                writeTraceBuf(splitTB);
            }
        } else {
            writeTraceBuf(traceBuf);
        }
    }
    
    void writeTraceBuf(TraceBuf2 tb) throws IOException {
        synchronized(outStream) {
            outStream.startTransmit();
            outStream.writeThreeChars(institution);
            outStream.writeThreeChars(module);
            outStream.writeThreeChars(EarthwormMessage.MESSAGE_TYPE_TRACEBUF2);
            DataOutputStream dos = new DataOutputStream(outStream);
            tb.write(dos);
            dos.flush();
            outStream.endTransmit();
            outStream.flush();
        }
    }

    void initSocket() throws UnknownHostException, IOException {
        if (serverSocket != null) {
            serverSocket.close();
        }
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(10*1000);
    }
    
    public void waitForClient() throws IOException {
        while(true) {
            try {
                heartbeater.setOutStream(null);
                clientSocket = serverSocket.accept(); // block until client connects
                inStream = new BufferedInputStream(clientSocket.getInputStream());
                outStream = new EarthwormEscapeOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
                heartbeater.setOutStream(outStream);
                heartbeater.heartbeat();
                if (verbose) {
                    System.out.println("accept connection from "+clientSocket.getInetAddress()+":"+clientSocket.getPort());
                }
                return;
            } catch(SocketTimeoutException e) {
                // try again...
                closeClient();
            }
        }
    }
    
    public void closeClient() {
        heartbeater.setOutStream(null);
        if (inStream != null) {
            try {
                inStream.close();
            } catch(IOException e) {
            }
        }
        if (outStream != null) {
            try {
                outStream.close();
            } catch(IOException e) {
            }
        }
        if (clientSocket != null) {
            try {
                clientSocket.close();
            } catch(IOException e) {
            }
        }
        clientSocket = null;
        inStream = null;
        outStream = null;
    }

    public void closeSocket() {
        closeClient();
        try {
            serverSocket.close();
        } catch(IOException e) {
        }
        serverSocket = null;
    }
    
    public int getNumTraceBufSent() {
        return traceBufSent;
    }
    
    public int getNumSplitTraceBufSent() {
        return splitTraceBufSent;
    }
    
    int getNextSeqNum() {
        if (seqNum == 999) {
            seqNum = 0;
        }
        return seqNum++;
    }

    public static void main(String[] args) throws Exception {
        // testing
        EarthwormExport exporter = new EarthwormExport(10002, 43, 255, "heartbeat", 5);
        exporter.waitForClient();
        int[] data = new int[14000];
        for (int i = 0; i < data.length; i++) {
            data[i] = i%100;
        }
        TraceBuf2 tb = new TraceBuf2(1,
                                     data.length,
                                     WinstonUtil.Y1970_TO_Y2000_SECONDS,
                                     1,
                                     "XXX",
                                     "SS",
                                     "HHZ",
                                     "00",
                                     data);
        for (int i = 0; i < 10; i++) {
            if (exporter.inStream.available() > 0) {
                byte[] b = new byte[1024];
                exporter.inStream.read(b);
                String s = new String(b);
                System.out.println("In: "+s);
            }
            Thread.sleep(1000);

            boolean notSent = true;
            while(notSent) {
                try {
                    exporter.export(tb);
                    notSent = false;
                    tb.startTime += data.length;
                    tb.endTime += data.length;
                    System.out.println("Set tb "+tb);
                } catch(IOException e) {
                    exporter.closeClient();
                    exporter.waitForClient();
                }
            }
        }
        System.out.println("Done");
    }

    int traceBufSent = 0;
    
    int splitTraceBufSent = 0;
    
    private String heartbeatMessage = "heartbeat";
    
    int module;

    int institution;

    EarthwormEscapeOutputStream outStream;
    
    BufferedInputStream inStream;

    int seqNum = 0;

    ServerSocket serverSocket;

    Socket clientSocket = null;
    
    EarthwormHeartbeater heartbeater = null;

    int port;

    public boolean verbose = true;
    
    public static final byte ESC = 27;

    public static final byte STX = 2;

    public static final byte ETX = 3;

    public static final String SEQ_CODE = "SQ:";

    public void setVerbose(boolean b) {
        verbose = b;
    }
    
}
