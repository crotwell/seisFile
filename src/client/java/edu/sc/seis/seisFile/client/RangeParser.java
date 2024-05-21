package edu.sc.seis.seisFile.client;

import java.util.Map;


public class RangeParser extends PatternParser {

    public RangeParser(String defaultMax) {
        super(POSITIVE_DECIMAL_NUMBER_RE + "-?"
                + POSITIVE_DECIMAL_NUMBER_RE + "?", new String[] {"min", "max"});
        this.defaultMax = defaultMax;
    }
    
    public Map<String,String> parse(String arg) {
        Map<String, String> result = super.parse(arg);
        if(result.get("max") == null){
            result.put("max", defaultMax);
        }
        return result;
    }
    
    private String defaultMax;

    public static final String POSITIVE_DECIMAL_NUMBER_RE = "(\\d+\\.?\\d*)";

    public String getErrorMessage(String arg) {
        return "A range is formatted like 2.0-7.1, not '" + arg + "'";
    }
}
