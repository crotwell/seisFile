package edu.sc.seis.seisFile.fdsnws;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.StringParser;

public class LevelParser extends StringParser {

    public Object parse(String arg) throws ParseException {
        if(arg.equals(FDSNStationQueryParams.LEVEL_NETWORK) 
                || arg.equals(FDSNStationQueryParams.LEVEL_STATION) 
                || arg.equals(FDSNStationQueryParams.LEVEL_CHANNEL) 
                || arg.equals(FDSNStationQueryParams.LEVEL_RESPONSE)) {
            return arg;
        }
        throw new ParseException("The repsonse format can be one of "
                                 +LEVEL_LIST+", not '" + arg + "'");
    }
    
    public static FlaggedOption createFlaggedOption() {
        return new FlaggedOption(FDSNStationQueryParams.LEVEL,
                                 new LevelParser(),
                                 null,
                                 false,
                                 'L',
                                 FDSNStationQueryParams.LEVEL,
                "The level of output.  Can be one of");
    }
    
    public static final String LEVEL_LIST = " "+FDSNStationQueryParams.LEVEL_NETWORK
            +", "+FDSNStationQueryParams.LEVEL_STATION
            +", "+FDSNStationQueryParams.LEVEL_CHANNEL
            +" or "+FDSNStationQueryParams.LEVEL_RESPONSE;
}

