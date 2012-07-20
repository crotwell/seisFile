package edu.sc.seis.seisFile.mseed;

import java.io.IOException;
import java.io.PrintWriter;



public abstract class ControlRecordLengthBlockette extends ControlBlockette implements RecordLengthBlockette {

    public ControlRecordLengthBlockette(byte[] info) {
        super(info);
    }
    
    public String getVersionOfFormat() {
        return Utility.extractString(info, 7, 4);
    }

    public int getLogicalRecordLengthByte() {
        return Utility.extractInteger(info, 11, 2);
    }

    public int getLogicalRecordLength() {
        if (getLogicalRecordLengthByte() < 31) {
            return (0x01 << getLogicalRecordLengthByte());
        } else {
            throw new RuntimeException("Data Record Length exceeds size of int");
        }
    }
    

    public void writeASCII(PrintWriter out) {
        writeASCIINoNewline(out);
        out.println();
    }
    
    public void writeASCIINoNewline(PrintWriter out)  {
        out.print("Blockette"+getType()+" record length="+getLogicalRecordLength()+" ("+getLogicalRecordLengthByte()+") "+new String(info));
    }
}
