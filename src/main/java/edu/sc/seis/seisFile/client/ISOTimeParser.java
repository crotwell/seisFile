package edu.sc.seis.seisFile.client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.StringParser;



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
    
    public static String format(Date d) {
        DateFormat passcalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        passcalFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return passcalFormat.format(d);
    }
    
    public static String formatForParsing(Date d) {
        DateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        isoFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return isoFormat.format(d);
    }
    
    public Date getDate(String arg) throws ParseException {
        if (arg.equals("now")) {
            return new Date();
        }
        if (arg.equals(YESTERDAY)) {
            return yesterday();
        }
        Matcher m = datePattern.matcher(arg);
        if(!m.matches()) {
            throw new ParseException("A time must be formatted as YYYY[[[[[-MM]-DD]Thh]:mm]:ss] like 2006-11-19 or 2006-11-19T06:34:21, not '"
                    + arg + "'");
        }
        Calendar cal = createCalendar(Integer.parseInt(m.group(1)),
                                              extract(m, 2),
                                              extract(m, 3), // 4 is the T
                                              extract(m, 5),
                                              extract(m, 6),
                                              extract(m, 7),
                                              ceiling);
        return cal.getTime();
    }
    /**
     * Creates a calendar in the given year. Year must be specified, but all
     * other fields can be -1 if unknown. If -1, they're either the greatest of
     * least value of the calendar's current state depending on the value of
     * ceiling.
     */
    public static Calendar createCalendar(int year,
                                          int month,
                                          int day,
                                          int hour,
                                          int minute,
                                          int second,
                                          boolean ceiling) {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        cal.set(Calendar.YEAR, year);
        fillInField(Calendar.MONTH, month - 1, ceiling, cal);
        fillInField(Calendar.DAY_OF_MONTH, day, ceiling, cal);
        fillInField(Calendar.HOUR_OF_DAY, hour, ceiling, cal);
        fillInField(Calendar.MINUTE, minute, ceiling, cal);
        fillInField(Calendar.SECOND, second, ceiling, cal);
        fillInField(Calendar.MILLISECOND, -1, ceiling, cal);
        return cal;
    }

    public static void fillInField(int field, int value, boolean ceiling, Calendar cal) {
        if(value >= 0) {
            cal.set(field, value);
        } else if(ceiling) {
            cal.set(field, cal.getActualMaximum(field));
        } else {
            cal.set(field, cal.getActualMinimum(field));
        }
    }
    
    private static int extract(Matcher m, int i) {
        if(m.group(i) == null) {
            return -1;
        }
        return Integer.parseInt(m.group(i));
    }


    public static FlaggedOption createYesterdayParam(String name,
                                                     String helpMessage,
                                                     boolean ceiling) {
        return createParam(name, YESTERDAY, helpMessage, ceiling);
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

    public Date yesterday() {
        Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        return cal.getTime();
    }

    public static final String ISO_TIME_RE = "(\\-?\\d{4})-?(\\d{2})?-?(\\d{2})?('T')?(\\d{2})?:?(\\d{2})?:?(\\d{2})?";

    private static Pattern datePattern = Pattern.compile(ISO_TIME_RE);
    
    public static final String FIRST_SEISMOGRAM = "1889-04-17";

    private static String YESTERDAY = "yesterday";
}
