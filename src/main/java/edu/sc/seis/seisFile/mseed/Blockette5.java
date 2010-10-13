package edu.sc.seis.seisFile.mseed;



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
}
