package edu.sc.seis.seisFile.mseed;

import java.io.DataInput;
import java.io.IOException;


public class ControlRecord extends SeedRecord {
    
    public ControlRecord(ControlHeader header) {
        super(header);
    }
    
    /** 
     * Reads the next control record from the stream. If the record continues, ie a
     * blockette is too big to fit in the record, then the following record will be read
     * recursively and combined with the current.
     * @param inStream
     * @param header
     * @param defaultRecordSize
     * @return
     * @throws IOException
     * @throws SeedFormatException
     */
    public static ControlRecord readControlRecord(DataInput inStream,
                                               ControlHeader header,
                                               int defaultRecordSize)
            throws IOException, SeedFormatException {
        ControlRecord controlRec = readSingleControlRecord(inStream, header, defaultRecordSize, null);
        
        if (controlRec.getLastPartialBlockette() != null) {
            ContinuedControlRecord continuationCR = new ContinuedControlRecord(controlRec);
            ControlRecord nextPartialRecord = controlRec;
            while(nextPartialRecord.getLastPartialBlockette() != null) {
                ControlHeader nextHeader = ControlHeader.read(inStream);
                if (nextHeader  instanceof DataHeader) {
                    throw new SeedFormatException("Control record continues, but next record is a DataRecord. curr="+header.toString()
                                              +"  next="+nextHeader.toString()); 
                }
                nextPartialRecord = readSingleControlRecord(inStream, nextHeader, nextPartialRecord.getRecordSize(), nextPartialRecord.getLastPartialBlockette());
                continuationCR.addContinuation(nextPartialRecord);
            }
            controlRec = continuationCR;
        }
        return controlRec;
    }
    
    
    public static ControlRecord readSingleControlRecord(DataInput inStream,
                                               ControlHeader header,
                                               int defaultRecordSize,
                                               PartialBlockette partialBlockette)
            throws IOException, SeedFormatException {
        
        /*
         * Assert.isTrue(header.getDataBlocketteOffset()>= header.getSize(),
         * "Offset to first blockette must be larger than the header size");
         */
        int recordSize = defaultRecordSize;
        ControlRecord controlRec = new ControlRecord(header);
        byte[] readBytes;
        int currOffset = header.getSize();
        if (partialBlockette != null && header.isContinuation()) {
            // need to pull remaining bytes of continued blockette
            Blockette b;
            int length = partialBlockette.getTotalSize()-partialBlockette.getSoFarSize();
            if (recordSize == 0 || length+currOffset < recordSize) {
                readBytes = new byte[length];
            } else {
                // not enought bytes to fill blockette, continues in next record
                readBytes = new byte[recordSize - currOffset];
            }
            inStream.readFully(readBytes);
            currOffset+= readBytes.length;
            b = new PartialBlockette(partialBlockette.getType(), readBytes, partialBlockette.getSwapBytes(), partialBlockette.getSoFarSize(), partialBlockette.getTotalSize());
            // assuming here that a record length blockette (5, 8, 10) will not be split across records
            controlRec.addBlockette(b);
            
        }
        String typeStr;
        byte[] typeBytes = new byte[3];
        if (recordSize == 0 || currOffset < recordSize-3) {
            inStream.readFully(typeBytes);
            typeStr = new String(typeBytes);
            currOffset+= typeBytes.length;
        } else {
            typeStr = THREESPACE;
        }
        while ( ! typeStr.equals(THREESPACE) && (recordSize == 0 || currOffset <= recordSize-7)) {
            int type = Integer.parseInt(typeStr.trim());
            byte[] lengthBytes = new byte[4];
            inStream.readFully(lengthBytes);
            String lengthStr = new String(lengthBytes);
            currOffset+= lengthBytes.length;
            int length = Integer.parseInt(lengthStr.trim());
            if (recordSize == 0 || length+currOffset-7 < recordSize) {
                readBytes = new byte[length-7];
            } else {
                // not enough bytes left in record to fill blockette
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
            if (recordSize == 0 || currOffset < recordSize-3) {
                inStream.readFully(typeBytes);
                typeStr = new String(typeBytes);
                currOffset+= typeBytes.length;
            } else {
                typeStr = THREESPACE;
            }
        }
        if(recordSize == 0 ) {
            if (defaultRecordSize == 0) {
                // no default
                throw new SeedFormatException("No blockettes 5, 8 or 10 to indicated record size and no default set");
            } else {
                // otherwise use default
                recordSize = defaultRecordSize;
            }
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
