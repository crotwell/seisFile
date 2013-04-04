package edu.sc.seis.seisFile.earthworm;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import edu.sc.seis.seisFile.syncFile.SyncFileWriter;
import edu.sc.seis.seisFile.syncFile.SyncLine;


public class EarthwormImport {
    
    public EarthwormImport(InputStream in) {
        this.in = in;
    }
    
    public EarthwormMessage nextMessage() throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int index = 0;
        int nextByte = in.read();
        boolean isEscapedByte = false;
        while(nextByte != EarthwormExport.STX) {
            nextByte = in.read();
        }
        // got a start, now read data
        nextByte = in.read();
        if (nextByte == EarthwormExport.ESC) {
            isEscapedByte = true;
            nextByte = in.read();
        }
        
        while( isEscapedByte || nextByte != EarthwormExport.ETX) {
            buffer[index] = (byte)nextByte;
            index++;
            isEscapedByte = false;
            nextByte = in.read();
            if (nextByte == EarthwormExport.ESC) {
                isEscapedByte = true;
                nextByte = in.read();
            }
        }
        byte[] tbBuf = new byte[index];
        System.arraycopy(buffer, 0, tbBuf, 0, tbBuf.length);
        return new EarthwormMessage(tbBuf);
    }
    
    InputStream in;
    
    int BUFFER_SIZE = 4096*2;
    
    /** just for testing, prints a message for each tracebuf received. */
    public static void main(String[] args) throws Exception {
        if (args.length != 2 || args.length != 4) {
            System.out.println("Usage: earthwormImpor [--sync syncfile][-h host][-p port]");
            return;
        }
        List<String> unknownArgs = new ArrayList<String>();
        String argHost = "localhost";
        int argPort = 19000;
        String argSyncfile = null;
        for (int i = 0; i < args.length; i++) {
            if (i < args.length-1) {
                if (args[i].equals("-h")) {
                    argHost = args[i+1];
                    i++;
                    continue;
                } else if (args[i].equals("-p")) {
                    argPort = Integer.parseInt(args[i+1]);
                    i++;
                    continue;
                } else if (args[i].equals("--sync")) {
                    argSyncfile = args[i+1];
                    i++;
                    continue;
                }
            }
            unknownArgs.add(args[i]);
        }
        final String host = argHost;
        final int port = argPort;
        final String syncfile = argSyncfile;
        final String heartbeatMessage = "heartbeat";
        final int heartbeatSeconds = 10;
        final int institution = 2;
        final int module = 99;
        HashMap<String, Double> lastStartTimeMap = new HashMap<String, Double>();
        HashMap<String, Double> lastEndTimeMap = new HashMap<String, Double>();
        try {
            Socket s = new Socket(host, port);
            final BufferedInputStream in = new BufferedInputStream(s.getInputStream());
            final EarthwormEscapeOutputStream outStream = new EarthwormEscapeOutputStream(new BufferedOutputStream(s.getOutputStream()));
            EarthwormHeartbeater heartbeater = new EarthwormHeartbeater(null, heartbeatSeconds, heartbeatMessage, institution, module);

            heartbeater.setOutStream(outStream);
            heartbeater.heartbeat();
            
            SyncFileWriter syncWriter = null;
            if (syncfile != null) {
                syncWriter = new SyncFileWriter("ewimport", syncfile);
            }
            
            EarthwormImport ewImport = new EarthwormImport(in);
            while(true) {
                EarthwormMessage message;
                try {
                    message = ewImport.nextMessage();
                    if (message.getMessageType() == EarthwormMessage.MESSAGE_TYPE_TRACEBUF2) {
                        TraceBuf2 traceBuf2 = new TraceBuf2(message.getData());
                        String key = traceBuf2.formatNSLCCodes();
                        if (lastEndTimeMap.containsKey(key)) {
                            if (Math.abs(traceBuf2.getStartTime() - lastEndTimeMap.get(key)) > 1/traceBuf2.getSampleRate()) {
                                System.out.println("GAP: "+(traceBuf2.getStartTime() - lastEndTimeMap.get(key)));
                                if (syncWriter != null) {
                                    syncWriter.appendLine(new SyncLine(traceBuf2.getNetwork(), 
                                                                       traceBuf2.getStation(),
                                                                       traceBuf2.getLocId(),
                                                                       traceBuf2.getChannel(),
                                                                       new Date(Math.round(1000*lastStartTimeMap.get(key))),
                                                                       new Date(Math.round(1000*lastEndTimeMap.get(key))),
                                                                       0f, 0f));
                                    lastStartTimeMap.put(key, traceBuf2.getStartTime());
                                }
                            }
                        } else {
                            lastStartTimeMap.put(key, traceBuf2.getStartTime());
                        }
                        lastEndTimeMap.put(key, traceBuf2.getPredictedNextStartTime());
                        System.out.println("TraceBuf: "+traceBuf2);
                    } else if (message.getMessageType() == EarthwormMessage.MESSAGE_TYPE_HEARTBEAT) {
                        System.out.println("Heartbeat received: "+new String(message.data));
                    }
                    Thread.sleep(1);
                } catch(IOException e) {
                    e.printStackTrace();
                    heartbeater.setOutStream(null);
                    outStream.close();
                    break;
                }
            }
        } catch (IOException e) {
            throw new IOException("Unable to bind to '"+host+"' at port "+port, e);
        }
    }
}
