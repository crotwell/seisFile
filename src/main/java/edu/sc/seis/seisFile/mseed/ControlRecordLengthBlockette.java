package edu.sc.seis.seisFile.mseed;

import java.io.IOException;
import java.io.PrintWriter;



public abstract class ControlRecordLengthBlockette extends ControlBlockette implements RecordLengthBlockette {

    public ControlRecordLengthBlockette(byte[] info) {
        super(info);
    }

    @Override
    public int getLogicalRecordLengthByte() {
        byte[] subbytes = new byte[2];
        System.arraycopy(info, 11, subbytes, 0, 2);
        return Integer.parseInt(new String(subbytes));
    }

    @Override
    public int getLogicalRecordLength() {
        if (getLogicalRecordLengthByte() < 31) {
            return (0x01 << getLogicalRecordLengthByte());
        } else {
            throw new RuntimeException("Data Record Length exceeds size of int");
        }
    }
    

    public void writeASCII(PrintWriter out) throws IOException {
        out.println("Blockette"+getType()+" record length="+getLogicalRecordLength()+" ("+getLogicalRecordLengthByte()+")");
    }
}
