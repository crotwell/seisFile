package edu.sc.seis.seisFile.client;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import picocli.CommandLine.ITypeConverter;

public abstract class PatternParser {

    public PatternParser(String re, String[] fields) {
        this.re = Pattern.compile(re);
        this.fields = fields;
    }

    public abstract String getErrorMessage(String arg);

    public Map<String,String> parse(String arg) {
        Matcher m = re.matcher(arg);
        if(!m.matches()) {
            throw new java.lang.IllegalArgumentException(getErrorMessage(arg));
        }
        Map<String,String> box = new HashMap();
        for(int i = 0; i < fields.length; i++) {
            box.put(fields[i], m.group(i + 1));
        }
        return box;
    }

    private Pattern re;

    private String[] fields;
}
