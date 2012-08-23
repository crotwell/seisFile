package edu.sc.seis.seisFile.mseed;


public class Blockette8 extends ControlRecordLengthBlockette {

    public Blockette8(byte[] info) {
        super(info);
    }

    @Override
    public int getType() {
        return 8;
    }
    
    public String getStationIdentifier() {
        return Utility.extractString(info, 13, 5);
    }
    
    public String getLocationIdentifier() {
        return Utility.extractString(info, 18, 2);
    }
    
    public String getChannelIdentifier() {
        return Utility.extractString(info, 20, 3);
    }
    
    public String getNetworkIdentifier() {
        return Utility.extractString(info, info.length-2, 2);
    }

    public String getBeginningOfVolume() {
        return Utility.extractVarString(info, 23, 22);
    }

    public String getEndOfVolume() {
        return Utility.extractVarString(info, 23+getBeginningOfVolume().length()+1, 22);
    }

    public String getStationInformationEffectiveDate() {
        return Utility.extractVarString(info, 23+getBeginningOfVolume().length()+1+getEndOfVolume().length()+1, 22);
    }

    public String getChannelInformationEffectiveDate() {
        return Utility.extractVarString(info, 23+getBeginningOfVolume().length()+1+getEndOfVolume().length()+1+getStationInformationEffectiveDate().length()+1, 22);
    }

    @Override
    public String getName() {
        return "Telemetry Volume Identifier Blockette";
    }
}
