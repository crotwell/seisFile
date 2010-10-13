package edu.sc.seis.seisFile.mseed;


public class Blockette8 extends ControlRecordLengthBlockette {

    public Blockette8(byte[] info) {
        super(info);
    }

    @Override
    public int getType() {
        return 8;
    }

    @Override
    public String getName() {
        return "Telemetry Volume Identifier Blockette";
    }
}
