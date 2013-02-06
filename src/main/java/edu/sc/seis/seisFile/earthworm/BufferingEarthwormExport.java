package edu.sc.seis.seisFile.earthworm;

import java.io.IOException;
import java.net.UnknownHostException;
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
                                    int sleepMillis) throws UnknownHostException, IOException {
        export = new EarthwormExport(port, module, institution, heartbeatMessage, heartbeatSeconds);
        export.setVerbose(true);
        maxSize = bufferSize;
        this.sleepMillis = sleepMillis;
        buffer = Collections.synchronizedList(new ArrayList<TraceBuf2>(maxSize));
        exportThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    TraceBuf2 tb = pop();
                    System.out.println("Try to send "+tb);
                    boolean notSent = true;
                    while(notSent) {
                        try {
                            export.export(tb);
                            notSent = false;
                            while (export.inStream.available() > 0) {
                                // should really look for heartbeats
                                export.inStream.read();
                            }
                            Thread.sleep(getSleepMillis());
                        } catch(Exception e) {
                            export.closeClient();
                            logger.error("problem sending " + tb, e);
                        }
                    }
                }
            }
        });
        exportThread.setDaemon(true);
        exportThread.start();
    }

    public void offer(TraceBuf2 tb) {
        synchronized(buffer) {
            if (buffer.size() == maxSize) {
                pop();
            }
            buffer.add(tb);
            logger.info("Offered, "+buffer.size()+" left");
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
            return out;
        }
    }

    Thread exportThread;

    EarthwormExport export;

    List<TraceBuf2> buffer;

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
