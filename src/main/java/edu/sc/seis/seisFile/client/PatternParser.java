package edu.sc.seis.seisFile.client;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.StringParser;

public abstract class PatternParser extends StringParser {

    public PatternParser(String re, String[] fields) {
        this.re = Pattern.compile(re);
        this.fields = fields;
    }

    public abstract String getErrorMessage(String arg);

    public Object parse(String arg) throws ParseException {
        Matcher m = re.matcher(arg);
        if(!m.matches()) {
            throw new ParseException(getErrorMessage(arg));
        }
        Map box = new HashMap();
        for(int i = 0; i < fields.length; i++) {
            box.put(fields[i], m.group(i + 1));
        }
        return box;
    }

    private Pattern re;

    private String[] fields;
}
