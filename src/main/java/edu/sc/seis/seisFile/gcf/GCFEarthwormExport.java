package edu.sc.seis.seisFile.gcf;

import edu.sc.seis.seisFile.earthworm.BufferingEarthwormExport;
import edu.sc.seis.seisFile.earthworm.TraceBuf2;
import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TooManyListenersException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

public class GCFEarthwormExport implements SerialPortEventListener {

    public GCFEarthwormExport(String serial,
                              Map<String,
                              String[]> sysId_StreamIdToSCNL,
                              BufferingEarthwormExport export) {
        this.serial = serial;
        this.convert = new Convert(sysId_StreamIdToSCNL);
        this.export = export;
    }

    @Override
    public void serialEvent(SerialPortEvent arg0) {
        SerialTransportLayer stl;
        try {
            stl = SerialTransportLayer.read(in);
        out.write(0x01); // ack
        out.write(stl.getStreamIdLSB());
        out.flush();
        
        if (stl.getPayload() instanceof GCFBlock) {
            GCFBlock block = (GCFBlock)stl.getPayload();
            TraceBuf2 tb = convert.toTraceBuf((GCFBlock)stl.getPayload());
            export.offer(tb);
        }
        } catch(GCFFormatException e) {
            handleError(e);
        } catch(IOException e) {
            handleError(e);
        }
    }
    
    public void handleError(Throwable t) {
        logger.error("error, reconnecting serial port", t);
        try {
            in.close();
        } catch(IOException e1) {}
        in = null;
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                connect();
                }catch (Throwable tt) {
                    try {Thread.sleep(2000);}catch(Exception e) {}
                    handleError(tt);
                }
            }
        }, 10);
    }

    void connect() throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException,
            IOException, TooManyListenersException {
        if (serialPort != null) {
            serialPort.close();
            try {Thread.sleep(500);}catch(Exception e) {}
        }
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(serial);
        if (portIdentifier.isCurrentlyOwned()) {
            logger.error("Error: Port is currently in use");
        } else {
            logger.info("Connecting");
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
            if (commPort instanceof SerialPort) {
                serialPort = (SerialPort)commPort;
                serialPort.setSerialPortParams(19200,
                                               SerialPort.DATABITS_8,
                                               SerialPort.STOPBITS_1,
                                               SerialPort.PARITY_NONE);
                logger.info("serial in buffer"+serialPort.getInputBufferSize());
                in = new DataInputStream(new BufferedInputStream(serialPort.getInputStream(), 4096));
                out = new DataOutputStream(serialPort.getOutputStream());
                logger.info("connect, clean buffer");
                // clean the buffer so hopefully we start at the beginning of a packet
                while(in.available()>0) {
                    in.read();
                }
                logger.info("serial buffer cleaned");
                serialPort.addEventListener(this);
                serialPort.notifyOnDataAvailable(true);
            } else {
                logger.error("Error: Only serial ports are handled by this example.");
            }
        }
    }
    SerialPort serialPort;
    int seqNum = 0;

    DataInputStream in;

    DataOutputStream out;

    String serial = "/dev/ttyS0";

    Convert convert;

    BufferingEarthwormExport export;
    
    public static final String GCF_CHAN_PROP = "gcf2ew.channel.";

    public static void main(String[] args) throws IOException, NoSuchPortException, PortInUseException, UnsupportedCommOperationException, TooManyListenersException {
        BasicConfigurator.configure();
        int port = 3000;
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
                } else if (args[i].equals("--port")) {
                    port = Integer.parseInt(args[i + 1]);
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
        if (propsFilename != null) {
            props.load(new BufferedReader(new FileReader(propsFilename)));
            PropertyConfigurator.configure(props);
        }
        logger.info("Start: port="+port+" mod="+module+" inst="+institution
                    +" heartbeat="+heartbeat+" serial="+serial+" buffer="+buffer);
        BufferingEarthwormExport export = new BufferingEarthwormExport(port,
                                                                       module,
                                                                       institution,
                                                                       "heartbeat",
                                                                       heartbeat,
                                                                       buffer,
                                                                       50);
        Map<String, String[]> sysId_StreamIdToSCNL = new HashMap<String, String[]>();
        for (String key : props.stringPropertyNames()) {
            if (key.startsWith(GCF_CHAN_PROP)) {
                String sys_streamid = key.substring(GCF_CHAN_PROP.length());
                String[] scnl = props.getProperty(key).split("\\.");
                if (scnl.length != 4) {
                    System.err.println("error with property "+key+"="+props.getProperty(key));
                }
                sysId_StreamIdToSCNL.put(sys_streamid, scnl);
            }
        }
          
        GCFEarthwormExport gcfExport = new GCFEarthwormExport(serial, sysId_StreamIdToSCNL, export);
        gcfExport.connect();
        while(true) {
            try {
                Thread.sleep(10000);
            } catch(InterruptedException e) {}
        }
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GCFEarthwormExport.class);
}
