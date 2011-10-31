package edu.sc.seis.seisFile.mseed;

import java.io.IOException;


public class DefaultBlocketteFactory implements BlocketteFactory {

    public Blockette parseBlockette(int type, byte[] bytes, boolean swapBytes) throws IOException, SeedFormatException {
        switch(type){
            case 5:
                return new Blockette5(bytes);
            case 8:
                return new Blockette8(bytes);
            case 10:
                return new Blockette10(bytes);
            case 100:
                return new Blockette100(bytes, swapBytes);
            case 200:
                return new Blockette200(bytes, swapBytes);
            case 1000:
                return new Blockette1000(bytes, swapBytes);
            case 1001:
                return new Blockette1001(bytes, swapBytes);
            case 2000:
                return new Blockette2000(bytes, swapBytes);
            default:
                if (type < 100) {
                    return new BlocketteUnknown(bytes, type, swapBytes);
                } else {
                    return new DataBlocketteUnknown(bytes, type, swapBytes);
                }
        }
    }
}
