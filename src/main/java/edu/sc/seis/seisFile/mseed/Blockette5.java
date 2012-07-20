package edu.sc.seis.seisFile.mseed;

import java.io.PrintWriter;



public class Blockette5 extends ControlRecordLengthBlockette {

    public Blockette5(byte[] info) {
        super(info);
    }

    @Override
    public int getType() {
        return 5;
    }

    @Override
    public String getName() {
        return "Field Volume Identifier Blockette";
    }

    public String getBeginningOfVolume() {
        return Utility.extractVarString(info, 13, 22);
    }
    
    public void writeASCII(PrintWriter out)  {
        super.writeASCIINoNewline(out);
        out.println(" beg vol="+getBeginningOfVolume());
    }
}
