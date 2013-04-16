package edu.sc.seis.seisFile.client;

import java.util.Map;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.ParseException;

public class RangeParser extends PatternParser {

    public RangeParser(String defaultMax) {
        super(POSITIVE_DECIMAL_NUMBER_RE + "-?"
                + POSITIVE_DECIMAL_NUMBER_RE + "?", new String[] {"min", "max"});
        this.defaultMax = defaultMax;
    }
    
    public Object parse(String arg) throws ParseException {
        Map result = (Map)super.parse(arg);
        if(result.get("max") == null){
            result.put("max", defaultMax);
        }
        return result;
    }

    public static FlaggedOption createParam(String name,
                                            String defaultMin,
                                            String defaultMax,
                                            String helpMessage) {
        return createParam(name, defaultMin, defaultMax, helpMessage, name.charAt(0));
    }

    public static FlaggedOption createParam(String name,
                                            String defaultMin,
                                            String defaultMax,
                                            String helpMessage,
                                            char shortFlag) {
        return new FlaggedOption(name,
                                 new RangeParser(defaultMax),
                                 defaultMin + "-" + defaultMax,
                                 false,
                                 shortFlag,
                                 name,
                                 helpMessage);
    }
    
    private String defaultMax;

    public static final String POSITIVE_DECIMAL_NUMBER_RE = "(\\d+\\.?\\d*)";

    public String getErrorMessage(String arg) {
        return "A range is formatted like 2.0-7.1, not '" + arg + "'";
    }
}
