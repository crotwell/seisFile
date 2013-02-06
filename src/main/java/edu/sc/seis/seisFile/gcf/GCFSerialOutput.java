package edu.sc.seis.seisFile.gcf;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GCFSerialOutput {

    public GCFSerialOutput() throws Exception {
        connect(serial);
    }

    public void writeGCF(Date startTime, int[] data) throws IOException {
        boolean isSerial = true;
        List<String> orientations = new ArrayList<String>();
        orientations.add("Z");
        orientations.add("N");
        orientations.add("E");
        for (String orient : orientations) {
            
        GCFBlock block = GCFBlock.mockGCF(startTime, data, isSerial);
        block.header.streamId = block.header.streamId.substring(0, 4)+orient+block.header.streamId.charAt(5);
        SerialTransportLayer serialLayer;
        try {
            serialLayer = new SerialTransportLayer(seqNum, block, isSerial);
        } catch(GCFFormatException e) {
            // can't happen
            throw new RuntimeException("Should not happen, isSerial is "+isSerial);
        }
        serialLayer.write(out);
        seqNum = (seqNum + 1) % 256;
        }
    }
    
    void connect(String portName) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException,
            IOException {
        CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        if (portIdentifier.isCurrentlyOwned()) {
            System.out.println("Error: Port is currently in use");
        } else {
            CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
            if (commPort instanceof SerialPort) {
                SerialPort serialPort = (SerialPort)commPort;
                serialPort.setSerialPortParams(19200,
                                               SerialPort.DATABITS_8,
                                               SerialPort.STOPBITS_1,
                                               SerialPort.PARITY_NONE);
                in = serialPort.getInputStream();
                out = new DataOutputStream(serialPort.getOutputStream());
            } else {
                System.out.println("Error: Only serial ports are handled by this example.");
            }
        }
    }

    int seqNum = 0;

    InputStream in;

    DataOutputStream out;

    String serial = "/dev/ttyS0";

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        GCFSerialOutput fake = new GCFSerialOutput();
        int[] fakeData = new int[200];
        Date roundNow = new Date((new Date().getTime() / 1000) * 1000);
        for (int i = 0; i < fakeData.length; i++) {
            fakeData[i] = 10 * i;
            if (i % 20 > 10) {
                fakeData[i] *= -1;
            }
        }
        while (true) {
            System.out.println("Send " + roundNow);
            fake.writeGCF(roundNow, fakeData);
            roundNow = new Date(roundNow.getTime() + 2000);
            Thread.sleep(2000);
        }
    }
}
