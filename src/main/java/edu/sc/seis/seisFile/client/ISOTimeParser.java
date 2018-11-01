package edu.sc.seis.seisFile.client;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.StringParser;

import edu.sc.seis.seisFile.TimeUtils;



public class ISOTimeParser extends StringParser {

    public ISOTimeParser(boolean ceiling){
        this.ceiling = ceiling;
    }

    public Object parse(String arg) throws ParseException {
        return getDate(arg);
    }
    
    public String parseDate(String arg) throws ParseException {
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
    
    public Instant getDate(String arg) throws ParseException {
        if (arg.equals("now")) {
            return Instant.now();
        }
        if (arg.equals(YESTERDAY)) {
            return yesterday();
        }
        Matcher m = datePattern.matcher(arg);
        if(!m.matches()) {
            throw new ParseException("A time must be formatted as YYYY[[[[[-MM]-DD]Thh]:mm]:ss] like 2006-11-19 or 2006-11-19T06:34:21, not '"
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


    public static FlaggedOption createRequiredParam(String name,
                                                     String helpMessage,
                                                     boolean ceiling) {
        return new FlaggedOption(name,
                                 new ISOTimeParser(ceiling),
                                 null,
                                 true,
                                 name.charAt(0),
                                 name,
                                 helpMessage);
    }

    public static FlaggedOption createYesterdayParam(String name,
                                                     String helpMessage,
                                                     boolean ceiling) {
        return createParam(name, YESTERDAY, helpMessage, ceiling);
    }

    public static FlaggedOption createParam(String name,
                                                     String helpMessage,
                                                     boolean ceiling) {
        return createParam(name, null, helpMessage, ceiling);
    }

    public static FlaggedOption createParam(String name,
                                            String defaultTime,
                                            String helpMessage,
                                            boolean ceiling) {
        return new FlaggedOption(name,
                                 new ISOTimeParser(ceiling),
                                 defaultTime,
                                 false,
                                 name.charAt(0),
                                 name,
                                 helpMessage);
    }

    private boolean ceiling;

    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    public Instant yesterday() {
        ZonedDateTime now = Instant.now().atZone(TimeUtils.TZ_UTC);
        return ZonedDateTime.of(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), 0, 0, 0, 0, TimeUtils.TZ_UTC)
                .minus(TimeUtils.ONE_DAY)
                .toInstant();
    }

    @Deprecated
    public static String getISOString(int year,
                                      int jday,
                                      int hour,
                                      int minute,
                                      float second) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat xxFormat = new DecimalFormat("00", symbols);
        DecimalFormat xxxFormat = new DecimalFormat("000", symbols);
        DecimalFormat floatFormat = new DecimalFormat("00.000#", symbols);
        return xxxFormat.format(year) + xxxFormat.format(jday) + "J"
                + xxFormat.format(hour) + xxFormat.format(minute)
                + floatFormat.format(second) + "Z";
    }

    public static final String ISO_TIME_RE = "(\\-?\\d{4})-?(\\d{2})?-?(\\d{2})?(T)?(\\d{2})?:?(\\d{2})?:?(\\d{2})?\\.?(\\d)?";

    private static Pattern datePattern = Pattern.compile(ISO_TIME_RE);
    
    public static final String FIRST_SEISMOGRAM = "1889-04-17";

    private static String YESTERDAY = "yesterday";
}
