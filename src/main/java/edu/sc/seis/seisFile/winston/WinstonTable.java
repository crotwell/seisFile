package edu.sc.seis.seisFile.winston;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

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
        month = dates[1]+1; // why do months start at 0 and days start at 1???
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

    protected String internalGetTableName(boolean isHeliTable) {
        String m = ""+getMonth();
        if (getMonth()<10) {
            m = "0"+m;
        }
        String d = ""+getDay();
        if (getDay()<10) {
            d = "0"+d;
        }
        String heli = isHeliTable?"H":"";
        return getDatabase().concatSCNL()+"$$"+heli+getYear()+"_"+m+"_"+d;
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
        synchronized(ymdFormat) {
         Date d = ymdFormat.parse(ymd);   
         Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone("GMT"));
         cal.setTime(d);
         return new int[] {cal.get(cal.YEAR), cal.get(cal.MONTH), cal.get(cal.DAY_OF_MONTH)};
        }
    }
    WinstonSCNL database;

    int year;

    int month;

    int day;
    
    private static final SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyy_MM_dd");
}
