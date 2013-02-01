package edu.sc.seis.seisFile.gcf;

import edu.sc.seis.seisFile.earthworm.BufferingEarthwormExport;
import edu.sc.seis.seisFile.earthworm.TraceBuf2;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;

public class GCFEarthwormExport implements Runnable {

    public GCFEarthwormExport(String serial, Map<String, String[]> sysId_StreamIdToSCNL, BufferingEarthwormExport export) {
        this.serial = serial;
        this.convert = new Convert(sysId_StreamIdToSCNL);
        this.export = export;
    }

    public void run() {
        while (true) {
            try {
                if (in == null) {
                    connect(serial);
                    // clean the buffer so hopefully we start at the beginning of a packet
                    while(in.available()>0) {
                        in.read();
                    }
                }
                SerialTransportLayer stl = SerialTransportLayer.read(in);
                System.out.println("Serial "+stl.getHeader().getBlockSeqNum());
                if (stl.getPayload() instanceof GCFBlock) {
                    TraceBuf2 tb = convert.toTraceBuf((GCFBlock)stl.getPayload());
                    export.offer(tb);
                }
            } catch(Exception e) {
                logger.error("Unable to connect to " + serial, e);
                if (in != null) {
                    try {
                        in.close();
                    } catch(IOException e1) {
                        // oh well
                    }
                }
                in = null;
                serialPort.close();
                try {
                    Thread.sleep(2000);
                } catch(InterruptedException e1) {}
            }
        }
    }

    void connect(String portName) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException,
            IOException {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Port is currently in use");
        } else {
            System.out.println("Connecting");
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
            if (commPort instanceof SerialPort) {
                serialPort = (SerialPort)commPort;
                serialPort.setSerialPortParams(19200,
                                               SerialPort.DATABITS_8,
                                               SerialPort.STOPBITS_1,
                                               SerialPort.PARITY_NONE);
                in = new BufferedInputStream(serialPort.getInputStream());
                out = new DataOutputStream(serialPort.getOutputStream());
            } else {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
    }
    SerialPort serialPort;
    int seqNum = 0;

    BufferedInputStream in;

    DataOutputStream out;

    String serial = "/dev/ttyS0";

    Convert convert;

    BufferingEarthwormExport export;

    public static void main(String[] args) throws IOException {
        BasicConfigurator.configure();
        BufferingEarthwormExport export = new BufferingEarthwormExport(3001, 999, 999, "heartbeat", 30, 100, 50);
        Map<String, String[]> sysId_StreamIdToSCNL = new HashMap<String, String[]>();
        sysId_StreamIdToSCNL.put(GCFBlock.MOCK_SYSID+"_"+GCFBlock.MOCK_STREAMID, new String[] {"TEST", "ENZ", "XX", "00"});  
          
        GCFEarthwormExport gcfExport = new GCFEarthwormExport("/dev/ttyUSB0", sysId_StreamIdToSCNL, export);
        gcfExport.run();
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GCFEarthwormExport.class);
}
