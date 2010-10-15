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

    public String getBeginningTime() {
        return Utility.extractVarString(info, 23, 22);
    }

    public String getEndTime() {
        return Utility.extractVarString(info, 23+getBeginningTime().length()+1, 22);
    }

    public String getVolumeTime() {
        return Utility.extractVarString(info, 23+getBeginningTime().length()+1+getEndTime().length()+1, 22);
    }

    public String getOriginatingOrganization() {
        return Utility.extractVarString(info, 23+getBeginningTime().length()+1+getEndTime().length()+1+getVolumeTime().length()+1, 80);
    }

    public String getLabel() {
        return Utility.extractVarString(info, 23+getBeginningTime().length()+1+getEndTime().length()+1+getVolumeTime().length()+1+getOriginatingOrganization().length()+1, 80);
    }
    
}
