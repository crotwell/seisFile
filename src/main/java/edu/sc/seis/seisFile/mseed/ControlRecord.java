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
                                               int defaultRecordSize,
                                               SeedRecord priorRecord)
            throws IOException, SeedFormatException {
        
        /*
         * Assert.isTrue(header.getDataBlocketteOffset()>= header.getSize(),
         * "Offset to first blockette must be larger than the header size");
         */
        int recordSize = defaultRecordSize;
        ControlRecord controlRec = new ControlRecord(header);
        byte[] readBytes;
        int currOffset = header.getSize();
        if (priorRecord != null && header.isContinuation()) {
            // need to pull remaining bytes of continued blockette
            PartialBlockette pb = priorRecord.getLastPartialBlockette();
            if (pb != null) {
                Blockette b;
                int length = pb.getTotalSize()-pb.getSoFarSize();
                if (length+currOffset < recordSize) {
                    readBytes = new byte[length];
                } else {
                    readBytes = new byte[recordSize - currOffset];
                }
                inStream.readFully(readBytes);
                currOffset+= readBytes.length;
                b = new PartialBlockette(pb.getType(), readBytes, pb.getSwapBytes(), pb.getSoFarSize(), pb.getTotalSize());

                if (b instanceof ControlRecordLengthBlockette) {
                    recordSize = ((ControlRecordLengthBlockette)b).getLogicalRecordLength();
                }
                controlRec.addBlockette(b);
                if (defaultRecordSize != 0 && currOffset > recordSize-7) {
                    // have used up rest of record
                    controlRec.RECORD_SIZE = recordSize;
                    // read garbage between blockettes and end
                    byte[] garbage = new byte[recordSize - currOffset];
                    if(garbage.length != 0) {
                        inStream.readFully(garbage);
                    }
                    return controlRec;
                }
            }
        }
        String typeStr;
        byte[] typeBytes = new byte[3];
        if (defaultRecordSize == 0 || currOffset < recordSize-3) {
            inStream.readFully(typeBytes);
            typeStr = new String(typeBytes);
            currOffset+= typeBytes.length;
        } else {
            typeStr = THREESPACE;
        }
        while ( ! typeStr.equals(THREESPACE) && (defaultRecordSize == 0 || currOffset <= recordSize-7)) {
            int type = Integer.parseInt(typeStr.trim());
            byte[] lengthBytes = new byte[4];
            inStream.readFully(lengthBytes);
            String lengthStr = new String(lengthBytes);
            currOffset+= lengthBytes.length;
            int length = Integer.parseInt(lengthStr.trim());
            if (length+currOffset-7 < recordSize) {
                readBytes = new byte[length-7];
            } else {
                readBytes = new byte[recordSize - currOffset];
            }
            inStream.readFully(readBytes);
            currOffset+= readBytes.length;
            byte[] fullBlocketteBytes = new byte[7+readBytes.length]; // less than length in case of continuation
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
                             readBytes.length);
            Blockette b;
            if (length == fullBlocketteBytes.length) {
                b = SeedRecord.getBlocketteFactory().parseBlockette(type,
                                             fullBlocketteBytes,
                                             true);
            } else {
                //special case for partial blockette continued in next record
                b = new PartialBlockette(type, fullBlocketteBytes, true, 0, length);
            }
            if (b instanceof ControlRecordLengthBlockette) {
                recordSize = ((ControlRecordLengthBlockette)b).getLogicalRecordLength();
            }

            controlRec.addBlockette(b);
            if (defaultRecordSize == 0 || currOffset < recordSize-3) {
                inStream.readFully(typeBytes);
                typeStr = new String(typeBytes);
                currOffset+= typeBytes.length;
            } else {
                typeStr = THREESPACE;
            }
        }
        if(recordSize == 0 && defaultRecordSize == 0) {
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
    
    public static final String THREESPACE = "   ";
}
