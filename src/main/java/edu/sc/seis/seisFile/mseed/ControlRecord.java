package edu.sc.seis.seisFile.mseed;

import java.io.DataInput;
import java.io.IOException;
import java.io.PrintWriter;


public class ControlRecord extends SeedRecord {
    
    public ControlRecord(ControlHeader header) {
        super(header);
    }
    
    public static ControlRecord readControlRecord(DataInput inStream,
                                               ControlHeader header,
                                               int defaultRecordSize)
            throws IOException, SeedFormatException {
        /*
         * Assert.isTrue(header.getDataBlocketteOffset()>= header.getSize(),
         * "Offset to first blockette must be larger than the header size");
         */
        ControlRecord controlRec = new ControlRecord(header);
        byte[] readBytes;
        int currOffset = header.getSize();
        String typeStr;
        byte[] typeBytes = new byte[3];
        inStream.readFully(typeBytes);
        typeStr = new String(typeBytes);
        while ( ! typeStr.equals("   ")) {
            int type = Integer.parseInt(typeStr);
            byte[] lengthBytes = new byte[4];
            inStream.readFully(lengthBytes);
            int length = Integer.parseInt(new String(lengthBytes));
            readBytes = new byte[length];
            inStream.readFully(readBytes);
            byte[] fullBlocketteBytes = new byte[length + 7];
            System.arraycopy(typeBytes,
                             0,
                             fullBlocketteBytes,
                             0,
                             3);
            System.arraycopy(lengthBytes,
                             0,
                             fullBlocketteBytes,
                             3,
                             4);
            System.arraycopy(readBytes,
                             0,
                             fullBlocketteBytes,
                             7,
                             length);

            Blockette b = Blockette.parseBlockette(type,
                                                   fullBlocketteBytes,
                                                   true);

            currOffset+=fullBlocketteBytes.length;
            controlRec.blockettes.add(b);
            inStream.readFully(typeBytes);
            typeStr = new String(typeBytes);
            currOffset+= typeBytes.length;
        }
        int recordSize = defaultRecordSize;
        Blockette[] b = controlRec.getBlockettes(5);
        if (b.length == 0) {b = controlRec.getBlockettes(8);}
        if (b.length == 0) {b = controlRec.getBlockettes(10);}
        if (b.length != 0) {
            ControlRecordLengthBlockette rlb = (ControlRecordLengthBlockette)b[0];
            recordSize = rlb.getLogicalRecordLength();
        } else if(defaultRecordSize == 0) {
            // no default
            throw new SeedFormatException("No blockettes 5, 8 or 10 to indicated record size and no default set");
        } else {
            // otherwise use default
            recordSize = defaultRecordSize;
        }
        controlRec.RECORD_SIZE = recordSize;
        // read garbage between blockettes and end
        byte[] garbage = new byte[recordSize - currOffset];
        if(garbage.length != 0) {
            inStream.readFully(garbage);
        }
        return controlRec;
    }

    public void setRecordSize(int recordSize) throws SeedFormatException {
        RECORD_SIZE = recordSize;
    }
}
