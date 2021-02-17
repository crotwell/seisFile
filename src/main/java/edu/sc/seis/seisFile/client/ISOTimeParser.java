package edu.sc.seis.seisFile.client;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.sc.seis.seisFile.TimeUtils;
import picocli.CommandLine.ITypeConverter;



public class ISOTimeParser  implements ITypeConverter<Instant>{

    public Instant convert(String value) throws Exception {
        return getDate(value);
    }
    

    public ISOTimeParser() {
        this(true);
    }
    
    public ISOTimeParser(boolean ceiling){
        this.ceiling = ceiling;
    }

    public Object parse(String arg) {
        return getDate(arg);
    }
    
    public String parseDate(String arg) {
        return format(getDate(arg));
    }
    
    public static String format(Instant d) {
        DateTimeFormatter passcalFormat = TimeUtils.createFormatter("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return passcalFormat.format(d);
    }
    
    public static String formatForParsing(Instant d) {
        DateTimeFormatter isoFormat = TimeUtils.createFormatter("yyyy-MM-dd'T'HH:mm:ss");
        return isoFormat.format(d);
    }
    
    public Instant getDate(String arg) {
        if (arg.equals("now")) {
            return Instant.now();
        }
        if (arg.equals(YESTERDAY)) {
            return yesterday();
        }
        Matcher m = datePattern.matcher(arg);
        if(!m.matches()) {
            throw new java.lang.IllegalArgumentException("A time must be formatted as YYYY[[[[[-MM]-DD]Thh]:mm]:ss] like 2006-11-19 or 2006-11-19T06:34:21, not '"
                    + arg + "'");
        }
        int year = extract(m, 1, 1970);
        int month = extract(m, 2, ceiling ? 12 : 1);
        YearMonth ym = YearMonth.of(year, month); 
        int dayOfMonth = extract(m, 3, ceiling ? ym.lengthOfMonth() : 1);
        int hour = extract(m, 5, ceiling ? 23 : 0);
        int minute = extract(m, 6, ceiling ? 59 : 0);
        int second = extract(m, 7, ceiling ? 59 : 0);
        int nanoOfSecond = extract(m, 8, ceiling ? ((int)TimeUtils.NANOS_IN_SEC) - 1 : 0);
        return ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond, TimeUtils.TZ_UTC)
                .toInstant();
    }
    
    private static int extract(Matcher m, int i, int defaultValue) {
        if(m.group(i) == null) {
            return defaultValue;
        }
        return Integer.parseInt(m.group(i));
    }

    private boolean ceiling;

    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    public Instant yesterday() {
        ZonedDateTime now = Instant.now().atZone(TimeUtils.TZ_UTC);
        return ZonedDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 0, 0, 0, 0, TimeUtils.TZ_UTC)
                .minus(TimeUtils.ONE_DAY)
                .toInstant();
    }

    public static final String ISO_TIME_RE = "(\\-?\\d{4})-?(\\d{2})?-?(\\d{2})?(T)?(\\d{2})?:?(\\d{2})?:?(\\d{2})?\\.?(\\d)?(Z)?";

    private static Pattern datePattern = Pattern.compile(ISO_TIME_RE);
    
    public static final String FIRST_SEISMOGRAM = "1889-04-17";

    private static String YESTERDAY = "yesterday";
}
