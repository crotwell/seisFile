package edu.sc.seis.seisFile.client;

import java.io.IOException;

import edu.sc.seis.seisFile.earthworm.EarthwormExport;
import edu.sc.seis.seisFile.earthworm.TraceBuf2;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name="earthwormExportServer",
         description="Example client to export fake TraceBuf2 packets over an earthworm export socket.",  
         versionProvider=edu.sc.seis.seisFile.client.VersionProvider.class)
public class EarthwormExportServer extends AbstractClient {

    @Option(names = "--port", description = "port to listen on, defaults to ${DEFAULT-VALUE}", defaultValue="10002")
    protected int port;
    @Option(names = "--heart", description = "heartbeat interval in seconds, defaults to ${DEFAULT-VALUE}", defaultValue="5")
    protected int heartbeatSec = 5;
    @Option(names = "--heartmessage", description = "heartbeat message, defaults to ${DEFAULT-VALUE}", defaultValue="heartbeat")
    protected String heartbeatMsg = "heartbeat";

    @Option(names = "--module", description = "earthworm module number, defaults to ${DEFAULT-VALUE}", defaultValue="43")
    protected int module = 43;
    @Option(names = "--inst", description = "earthworm institution number, defaults to ${DEFAULT-VALUE}", defaultValue="255")
    protected int inst = 255;

    @Override
    public Integer call() throws Exception {
        // testing
        EarthwormExport exporter = new EarthwormExport(port, module, inst, heartbeatMsg, heartbeatSec);
        exporter.waitForClient();
        int[] data = new int[14000];
        for (int i = 0; i < data.length; i++) {
            data[i] = i%100;
        }
        long Y1970_TO_Y2000_SECONDS = 946728000L;
        int pin = 1;
        double sampleRate = 1;
        TraceBuf2 tb = new TraceBuf2(pin,
                                     data.length,
                                     Y1970_TO_Y2000_SECONDS,
                                     sampleRate,
                                     "XXX",
                                     "SS",
                                     "HHZ",
                                     "00",
                                     data);
        for (int i = 0; i < 10; i++) {
            
            byte[] b = exporter.readResponseBytes();
            String s = new String(b);
            System.out.println("In: "+s);
            Thread.sleep(1000);

            boolean notSent = true;
            while(notSent) {
                try {
                    exporter.export(tb);
                    notSent = false;
                    tb = new TraceBuf2(pin,
                                       data.length,
                                       tb.getStartTime() + data.length/sampleRate,
                                       sampleRate,
                                       "XXX",
                                       "SS",
                                       "HHZ",
                                       "00",
                                       data);
                    System.out.println("Sent tb "+tb);
                } catch(IOException e) {
                    exporter.closeClient();
                    exporter.waitForClient();
                }
            }
        }
        System.out.println("Done");
        return 0;
    }


    public static void main(String... args) { // bootstrap the application
        System.exit(new CommandLine(new EarthwormExportServer()).execute(args));
    }
}
