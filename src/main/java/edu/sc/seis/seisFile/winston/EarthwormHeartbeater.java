package edu.sc.seis.seisFile.winston;

import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/** sends heartbeats on the EarthwormEscapeOutputStream. Synchronized on the outStream 
 * and so any other thread that writes to the same  EarthwormEscapeOutputStream must also
 * synchronize on the outStream.
 * 
 * Setting the outStream to null will disable heartbeats, and setting it back to a non-null
 * will enable them.
 * @author crotwell
 *
 */
public class EarthwormHeartbeater extends TimerTask {

    public EarthwormHeartbeater(EarthwormEscapeOutputStream outStream,
                                int heartbeatSeconds,
                                String heartbeatMessage,
                                int institution,
                                int module) {
        super();
        this.outStream = outStream;
        this.heartbeatSeconds = heartbeatSeconds;
        this.heartbeatMessage = heartbeatMessage;
        Timer heartbeater = new Timer(true);
        heartbeater.schedule(this, 100, heartbeatSeconds * 1000);
    }

    public EarthwormEscapeOutputStream getOutStream() {
        return outStream;
    }

    public void setOutStream(EarthwormEscapeOutputStream outStream) {
        this.outStream = outStream;
    }

    public int getHeartbeatSeconds() {
        return heartbeatSeconds;
    }

    public String getHeartbeatMessage() {
        return heartbeatMessage;
    }

    
    public boolean isVerbose() {
        return verbose;
    }

    
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public void run() {
        try {
            heartbeat();
        } catch(IOException e) {
            System.err.println("IOException with heartbeat, closing connection." + e);
            try {
                outStream.close();
                outStream = null;
            } catch(IOException e1) {
                // oh well...
            }
        }
    }

    public void heartbeat() throws IOException {
        if (outStream == null) {
            return;
        }
        synchronized(outStream) {
            outStream.startTransmit();
            outStream.writeThreeChars(institution);
            outStream.writeThreeChars(module);
            outStream.writeThreeChars(EarthwormMessage.MESSAGE_TYPE_HEARTBEAT);
            outStream.write(heartbeatMessage.getBytes());
            outStream.endTransmit();
            outStream.flush();
        }
        if (verbose) {
            System.out.println("Heartbeat sent: "+new Date()+" "+heartbeatMessage+"  "+heartbeatSeconds);
        }
    }

    EarthwormEscapeOutputStream outStream;

    int heartbeatSeconds;

    String heartbeatMessage;

    int institution;

    int module;
    
    boolean verbose = false;
}
