package edu.sc.seis.seisFile.mseed;



public class Blockette10 extends ControlRecordLengthBlockette {

    public Blockette10(byte[] info) {
        super(info);
    }

    @Override
    public int getType() {
        return 10;
    }

    @Override
    public String getName() {
        return "Volume Identifier Blockette";
    }
    
    
}
