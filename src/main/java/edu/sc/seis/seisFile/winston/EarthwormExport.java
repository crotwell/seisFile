package edu.sc.seis.seisFile.winston;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class EarthwormExport {

    public EarthwormExport(int port, int module, int institution, final String heartbeatMessage, int heartbeatSeconds) throws UnknownHostException,
            IOException {
        this.port = port;
        this.module = module;
        this.institution = institution;
        initSocket();
        Timer heartbeater = new Timer(true);
        heartbeater.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    if (outStream != null) {
                        heartbeat(heartbeatMessage);
                    }
                } catch(IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            }, 100, heartbeatSeconds*1000);
    }

    public synchronized void heartbeat(String message) throws IOException {
        outStream.startTransmit();
        
        writeThreeChars(outStream, institution);
        writeThreeChars(outStream, module);
        writeThreeChars(outStream, 3);
        outStream.write(message.getBytes());
        outStream.endTransmit();
        outStream.flush();
    }

    public synchronized void export(TraceBuf2 traceBuf) throws IOException {
        if (traceBuf.getSize() > TraceBuf2.MAX_TRACEBUF_SIZE) {
            List<TraceBuf2> split = traceBuf.split(TraceBuf2.MAX_TRACEBUF_SIZE);
            for (TraceBuf2 splitTB : split) {
                writeTraceBuf(splitTB);
            }
        } else {
            writeTraceBuf(traceBuf);
        }
    }
    
    void writeTraceBuf(TraceBuf2 tb) throws IOException {
        outStream.startTransmit();
        writeThreeChars(outStream, institution);
        writeThreeChars(outStream, module);
        writeThreeChars(outStream, MESSAGE_TYPE_TRACEBUF2);
       // writeSeqNum(outStream, getNextSeqNum());
        DataOutputStream dos = new DataOutputStream(outStream);
        tb.write(dos);
        dos.flush();
        outStream.endTransmit();
        outStream.flush();
    }

    void writeThreeChars(OutputStream out, int val) throws IOException {
        String s = numberFormat.format(val);
        out.write(s.charAt(0));
        out.write(s.charAt(1));
        out.write(s.charAt(2));
    }

    void writeSeqNum(OutputStream out, int seqNum) throws UnknownHostException, IOException {
        out.write(SEQ_CODE.getBytes());
        writeThreeChars(out, seqNum);
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
                clientSocket = serverSocket.accept(); // block until client connects
                inStream = new BufferedInputStream(clientSocket.getInputStream());
                outStream = new EarthwormEscapeStream(new BufferedOutputStream(clientSocket.getOutputStream()));
                return;
            } catch(SocketTimeoutException e) {
                // try again...
                closeClient();
            }
        }
    }
    
    public void closeClient() {
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
    
    int getNextSeqNum() {
        if (seqNum == 999) {
            seqNum = 0;
        }
        return seqNum++;
    }

    public static void main(String[] args) throws Exception {
        // testing
        EarthwormExport exporter = new EarthwormExport(16005, 43, 255, "heartbeat", 5);
        exporter.waitForClient();
        int[] data = new int[10];
        for (int i = 0; i < data.length; i++) {
            data[i] = i%100;
        }
        TraceBuf2 tb = new TraceBuf2(1,
                                     data.length,
                                     WinstonUtil.Y1970_TO_Y2000_SECONDS,
                                     1,
                                     "JSC",
                                     "CO",
                                     "HHZ",
                                     "00",
                                     data);
        for (int i = 0; i < 100; i++) {
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
    
    

    int module;

    int institution;

    EarthwormEscapeStream outStream;
    
    BufferedInputStream inStream;

    int seqNum = 0;

    ServerSocket serverSocket;

    Socket clientSocket = null;

    int port;

    DecimalFormat numberFormat = new DecimalFormat("000");

    public static final byte ESC = 27;

    public static final byte STX = 2;

    public static final byte ETX = 3;

    public static final byte MESSAGE_TYPE_TRACEBUF2 = 19;

    public static final String SEQ_CODE = "SQ:";
    
}
