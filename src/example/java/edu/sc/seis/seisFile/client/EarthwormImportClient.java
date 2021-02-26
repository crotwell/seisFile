package edu.sc.seis.seisFile.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

import edu.sc.seis.seisFile.earthworm.EarthwormEscapeOutputStream;
import edu.sc.seis.seisFile.earthworm.EarthwormHeartbeater;
import edu.sc.seis.seisFile.earthworm.EarthwormImport;
import edu.sc.seis.seisFile.earthworm.EarthwormMessage;
import edu.sc.seis.seisFile.earthworm.TraceBuf2;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

@Command(name="earthwormImportClient", versionProvider=edu.sc.seis.seisFile.client.VersionProvider.class)
public class EarthwormImportClient extends AbstractClient {

    
    
    public EarthwormImportClient() {
        // TODO Auto-generated constructor stub
    }


    @Spec
    protected CommandSpec spec;

    @Option(names = { "--host" }, description="host to connect to", defaultValue="localhost")
    protected String host;


    @Option(names = { "--port" }, description="port to connect to", defaultValue="19000")
    protected Integer port;
    
    /** just for testing, prints a message for each tracebuf received. */
    @Override
    public Integer call() throws Exception {
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
                                
                            }
                        } else {
                            lastStartTimeMap.put(key, traceBuf2.getStartTime());
                        }
                        lastEndTimeMap.put(key, traceBuf2.getPredictedNextStartTime());
                        System.out.println("TraceBuf: "+traceBuf2);
                    } else if (message.getMessageType() == EarthwormMessage.MESSAGE_TYPE_HEARTBEAT) {
                        System.out.println("Heartbeat received: "+new String(message.getData()));
                    }
                    Thread.sleep(1);
                } catch(IOException e) {
                    // remote closed connection?
                    System.out.println("Remote connection closed.");
                    heartbeater.setOutStream(null);
                    outStream.close();
                    break;
                }
            }
            
        } catch (IOException e) {
            throw new IOException("Unable to bind to '"+host+"' at port "+port, e);
        }
        return 0;
    }

    public static void main(String... args) { // bootstrap the application
        System.exit(new CommandLine(new EarthwormImportClient()).execute(args));
    }
}
