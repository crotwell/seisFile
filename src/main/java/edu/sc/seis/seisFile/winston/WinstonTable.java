package edu.sc.seis.seisFile.winston;

import java.text.ParseException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.sc.seis.seisFile.TimeUtils;

public class WinstonTable {
    
    protected WinstonTable(WinstonSCNL database, String tableName) throws ParseException {
        super();
        this.database = database;
        String[] s = tableName.split("\\$\\$");
        if (s[1].startsWith("H")) {
            s[1] = s[1].substring(1);
        }
        int[] dates = parseDate(s[1]);
        year = dates[0];
        month = dates[1];
        day = dates[2];
        
    }
    
    protected WinstonTable(WinstonSCNL database, int year, int month, int day) {
        super();
        this.database = database;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public WinstonSCNL getDatabase() {
        return database;
    }
    
    public String getTableName() {
        return internalGetTableName(false);
    }
    
    public String getHeliTableName() {
        return internalGetTableName(true);
    }
    
    public String getDateString() {
        String m = ""+getMonth();
        if (getMonth()<10) {
            m = "0"+m;
        }
        String d = ""+getDay();
        if (getDay()<10) {
            d = "0"+d;
        }
        return getYear()+"_"+m+"_"+d;
    }

    protected String internalGetTableName(boolean isHeliTable) {
        String heli = isHeliTable?"H":"";
        return getDatabase().concatSCNL()+"$$"+heli+getDateString();
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    protected int[] parseDate(String ymd) throws ParseException {
        Matcher m = ymdPattern.matcher(ymd);
        if (m.matches()) {
        return new int[] {Integer.parseInt(m.group(1)),Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3))};
        } else {
            throw new IllegalArgumentException("Cannot parse '"+ymd+"' as yyyy_MM_dd");
        }
    }
    
    WinstonSCNL database;

    int year;

    int month;

    int day;

    private static final Pattern ymdPattern = Pattern.compile("(\\d{4})_(\\d{2})_(\\d{2})");
    private static final DateTimeFormatter ymdFormat = DateTimeFormatter.ofPattern("yyyy_MM_dd").withZone(TimeUtils.TZ_UTC);
}
