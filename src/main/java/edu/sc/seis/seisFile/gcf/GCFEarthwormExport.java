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
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;

public class GCFEarthwormExport implements Runnable {

    public GCFEarthwormExport(String serial,
                              Map<String,
                              String[]> sysId_StreamIdToSCNL,
                              BufferingEarthwormExport export) {
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
        int port = 3001;
        int module = 999;
        int institution = 999;
        int heartbeat = 30;
        int buffer = 1000;
        String serial = "/dev/ttyS0";
        String propsFilename = null;
        Properties props = new Properties();
        for (int i = 0; i < args.length; i++) {
            if (i < args.length-1) {
                if (args[i].equals("--module")) {
                    module = Integer.parseInt(args[i + 1]);
                    i++;
                } else if (args[i].equals("--inst")) {
                    institution = Integer.parseInt(args[i + 1]);
                    i++;
                } else if (args[i].equals("--heartbeat")) {
                    heartbeat = Integer.parseInt(args[i + 1]);
                    i++;
                } else if (args[i].equals("--serial")) {
                    serial = args[i + 1];
                    i++;
                } else if (args[i].equals("--buffer")) {
                    buffer = Integer.parseInt(args[i + 1]);
                    i++;
                } else if (args[i].equals("-p")) {
                    propsFilename = args[i + 1];
                    i++;
                }
            }
        }
        BufferingEarthwormExport export = new BufferingEarthwormExport(port,
                                                                       module,
                                                                       institution,
                                                                       "heartbeat",
                                                                       heartbeat,
                                                                       buffer,
                                                                       50);
        if (propsFilename != null) {
            props.load(new BufferedReader(new FileReader(propsFilename)));
        }
        Map<String, String[]> sysId_StreamIdToSCNL = new HashMap<String, String[]>();
        for (String key : props.stringPropertyNames()) {
            String[] scnl = props.getProperty(key).split("\\.");
            if (scnl.length != 4) {
                System.err.println("error with property "+key+"="+props.getProperty(key));
            }
            sysId_StreamIdToSCNL.put(key, scnl);
        }
          
        GCFEarthwormExport gcfExport = new GCFEarthwormExport(serial, sysId_StreamIdToSCNL, export);
        gcfExport.run();
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GCFEarthwormExport.class);
}
