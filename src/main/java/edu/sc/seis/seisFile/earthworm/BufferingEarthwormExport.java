package edu.sc.seis.seisFile.earthworm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BufferingEarthwormExport {

    public BufferingEarthwormExport(int port,
                                    int module,
                                    int institution,
                                    final String heartbeatMessage,
                                    final int heartbeatSeconds,
                                    int bufferSize,
                                    int sleepMillis) throws IOException {
        export = new EarthwormExport(port, module, institution, heartbeatMessage, heartbeatSeconds);
        export.setVerbose(true);
        maxSize = bufferSize;
        this.sleepMillis = sleepMillis;
        buffer = Collections.synchronizedList(new ArrayList<>(maxSize));
        exportThread = new Thread(new Runnable() {

            @Override
            public void run() {
                TraceBuf2 tb = null;
                while (true) {
                    try {
                    if (! export.isConnected()) {
                        try {
                            export.waitForClient();
                        } catch(IOException e1) {
                            logger.error("Problem waiting for client connection, retry", e1);
                        }
                    } else {
                        if (tb == null) {
                            tb = pop(); // next tb
                        }
                        if (tb != null) {
                            logger.info("Try to send "+tb);
                            export.export(tb);
                            sentCount++;
                            tb = null;
                        }
                        if (export.inStream.available() > 0) {
                            // should really look for heartbeats
                            byte[] availableInBytes = new byte[export.inStream.available()];
                            export.inStream.read(availableInBytes);
                            logger.info("possible heartbeat: "+availableInBytes[0]+" "+availableInBytes[1]+" "+availableInBytes[2]);
                        }
                        Thread.sleep(getSleepMillis());
                    }
                    } catch (Throwable t) {
                        export.closeSocket();
                        logger.error("problem sending " + tb, t);
                    }
                    
                }
            }
        });
        exportThread.setDaemon(true);
        exportThread.setName("exportThread");
        exportThread.start();
    }

    public void offer(TraceBuf2 tb) {
        total++;
        synchronized(buffer) {
            if (buffer.size() == maxSize) {
                pop();
                tossCount++;
            }
            buffer.add(tb);
            logger.info("Offered, "+buffer.size()+" left: "+sentCount+"+"+tossCount+" = "+total);
            buffer.notifyAll();
        }
    }

    public TraceBuf2 pop() {
        synchronized(buffer) {
            while (buffer.isEmpty()) {
                try {
                    buffer.wait();
                } catch(InterruptedException e) {}
            }
            TraceBuf2 out = buffer.remove(0);
            buffer.notifyAll();
            return out;
        }
    }

    
    public int getTotal() {
        return total;
    }

    
    public int getSentCount() {
        return sentCount;
    }

    
    public int getTossCount() {
        return tossCount;
    }

    
    public int getMaxSize() {
        return maxSize;
    }

    Thread exportThread;

    EarthwormExport export;

    List<TraceBuf2> buffer;

    int total = 0;
    
    int sentCount = 0;
    
    int tossCount = 0;
    
    int maxSize;

    long sleepMillis = 50;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(BufferingEarthwormExport.class);

    
    public long getSleepMillis() {
        return sleepMillis;
    }

    
    public void setSleepMillis(long sleepMillis) {
        this.sleepMillis = sleepMillis;
    }
}
