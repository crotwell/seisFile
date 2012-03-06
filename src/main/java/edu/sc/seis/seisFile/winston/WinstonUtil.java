package edu.sc.seis.seisFile.winston;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import edu.sc.seis.seisFile.syncFile.SyncFile;
import edu.sc.seis.seisFile.syncFile.SyncFileWriter;
import edu.sc.seis.seisFile.syncFile.SyncLine;

public class WinstonUtil {

    public WinstonUtil(String databaseURL, String username, String password, String prefix) {
        this(databaseURL, username, password, prefix, MYSQL_DRIVER);
    }

    public WinstonUtil(String databaseURL, String username, String password, String prefix, String driverClassname) {
        super();
        this.driver = driverClassname;
        this.databaseURL = databaseURL;
        this.username = username;
        this.password = password;
        this.prefix = prefix;
    }

    public WinstonSCNL createWinstonSCNL(String station, String channel, String network, String locId) {
        return new WinstonSCNL(station, channel, network, locId, prefix);
    }

    public WinstonTable createWinstonTable(WinstonSCNL database, int year, int month, int day) {
        return new WinstonTable(database, year, month, day);
    }

    public List<WinstonSCNL> listChannelDatabases() throws SQLException {
        List<WinstonSCNL> out = new ArrayList<WinstonSCNL>();
        ResultSet rs = getConnection().createStatement().executeQuery("SHOW DATABASES");
        while (rs.next()) {
            String s = rs.getString(1);
            if (s.startsWith(getPrefix()+"_") && !s.equals(getPrefix() + "_ROOT")) {
                out.add(new WinstonSCNL(s, getPrefix()));
            }
        }
        return out;
    }

    public void useDatabase(WinstonSCNL channel) throws SQLException {
        getConnection().createStatement().execute("use " + channel.getDatabaseName());
    }

    public List<WinstonTable> listDayTables(WinstonSCNL channel) throws SQLException {
        List<WinstonTable> out = new ArrayList<WinstonTable>();
        useDatabase(channel);
        ResultSet rs = getConnection().createStatement().executeQuery("SHOW TABLES");
        while (rs.next()) {
            String s = rs.getString(1);
            if (!s.contains("$$H")) { // skip heli channels as we know how to
                                      // construct their names
                try {
                    out.add(new WinstonTable(channel, s));
                } catch(ParseException e) {
                    // came out of database, so shouldn't happen in a standard
                    // winston database, so ignore
                }
            }
        }
        return out;
    }

    public List<WinstonTable> listTablesBetweenDates(WinstonSCNL channel,
                                                     int startYear,
                                                     int startMonth,
                                                     int startDay,
                                                     int endYear,
                                                     int endMonth,
                                                     int endDay) throws SQLException {
        List<WinstonTable> out = listDayTables(channel);
        Iterator<WinstonTable> it = out.iterator();
        while (it.hasNext()) {
            WinstonTable wt = it.next();
            if (wt.getYear() < startYear || wt.getYear() > endYear) {
                it.remove();
                continue;
            } else {
                if (wt.getYear() == startYear) {
                    if (wt.getMonth() < startMonth || (wt.getMonth() == startMonth && wt.getDay() < startDay)) {
                        it.remove();
                        continue;
                    }
                }
                if (wt.getYear() == endYear) {
                    if (wt.getMonth() > endMonth || (wt.getMonth() == endMonth && wt.getDay() > endDay)) {
                        it.remove();
                        continue;
                    }
                }
            }
        }
        return out;
    }

    public SyncFile calculateSyncBetweenDates(WinstonSCNL channel,
                                              int startYear,
                                              int startMonth,
                                              int startDay,
                                              int endYear,
                                              int endMonth,
                                              int endDay,
                                              String dataCenterName) throws SQLException {
        SyncFile out = new SyncFile(dataCenterName);
        List<WinstonTable> tableList = listTablesBetweenDates(channel,
                                                              startYear,
                                                              startMonth,
                                                              startDay,
                                                              endYear,
                                                              endMonth,
                                                              endDay);
        for (WinstonTable wt : tableList) {
            out = out.concatenate(calculateSyncForDay(wt));
        }
        return out;
    }

    public void writeSyncBetweenDates(WinstonSCNL channel,
                                      int startYear,
                                      int startMonth,
                                      int startDay,
                                      int endYear,
                                      int endMonth,
                                      int endDay,
                                      SyncFileWriter writer) throws SQLException {
        List<WinstonTable> tableList = listTablesBetweenDates(channel,
                                                              startYear,
                                                              startMonth,
                                                              startDay,
                                                              endYear,
                                                              endMonth,
                                                              endDay);
        for (WinstonTable wt : tableList) {
            writer.appendAll(calculateSyncForDay(wt), true);
        }
    }

    public SyncFile calculateSyncForDay(WinstonTable table) throws SQLException {
        SyncFile out = new SyncFile("Winston " + table.getTableName());
        useDatabase(table.getDatabase());
        SyncLine defaultSyncLine = new SyncLine(table.getDatabase().getNetwork(),
                                                table.getDatabase().getStation(),
                                                table.getDatabase().getLocId(),
                                                table.getDatabase().getChannel());
        Statement stmt = getConnection().createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
                                                         java.sql.ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(Integer.MIN_VALUE);
        ResultSet rs = stmt.executeQuery("select st, et, sr from " + table.getTableName() + " order by st");
        while (rs.next()) {
            out.addLine(new SyncLine(defaultSyncLine,
                                     j2KSecondsToDate(rs.getDouble(1)),
                                     j2KSecondsToDate(rs.getDouble(2)),
                                     rs.getFloat(3)),
                        true);
        }
        rs.close();
        stmt.close();
        return out;
    }

    public List<TraceBuf2> extractData(WinstonSCNL channel, Date startTime, Date endTime) throws SQLException,
            DataFormatException {
        List<TraceBuf2> out = new ArrayList<TraceBuf2>();
        Calendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.setTime(startTime);
        int startYear = cal.get(Calendar.YEAR);
        int startMonth = cal.get(Calendar.MONTH);
        int startDay = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(endTime);
        int endYear = cal.get(Calendar.YEAR);
        int endMonth = cal.get(Calendar.MONTH);
        int endDay = cal.get(Calendar.DAY_OF_MONTH);
        List<WinstonTable> tableList = listTablesBetweenDates(channel,
                                                              startYear,
                                                              startMonth,
                                                              startDay,
                                                              endYear,
                                                              endMonth,
                                                              endDay);
        for (WinstonTable wt : tableList) {
            out.addAll(extractData(wt, startTime, endTime));
        }
        return out;
    }

    public List<TraceBuf2> extractData(WinstonTable table, Date startTime, Date endTime) throws SQLException,
            DataFormatException {
        useDatabase(table.getDatabase());
        List<TraceBuf2> out = new ArrayList<TraceBuf2>();
        Statement stmt = getConnection().createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
                                                         java.sql.ResultSet.CONCUR_READ_ONLY);
        stmt.setFetchSize(Integer.MIN_VALUE);
        double y2kStart = dateToJ2kSeconds(startTime);
        double y2kEnd = dateToJ2kSeconds(endTime);
        ResultSet rs = stmt.executeQuery("select tracebuf from " + table.getTableName() + " where (" + y2kStart
                + " <= st AND st <= " + y2kEnd + ") OR (" + y2kStart + " <=et AND et <= " + y2kEnd + ") order by st");
        while (rs.next()) {
            Blob tbBlob = rs.getBlob("tracebuf");
            byte[] tbBytes = tbBlob.getBytes(1, (int)tbBlob.length());
            Inflater decompresser = new Inflater();
            decompresser.setInput(tbBytes, 0, tbBytes.length);
            byte[] result = new byte[TraceBuf2.MAX_TRACEBUF_SIZ]; // should all
                                                                  // fit in once
                                                                  // decomp
                                                                  // cycle
            int resultLength = decompresser.inflate(result);
            if (!decompresser.finished()) {
                throw new RuntimeException("more bytes in Blob than can fit in a TraceBuf2: "
                        + TraceBuf2.MAX_TRACEBUF_SIZ);
            }
            byte[] tbResult = new byte[resultLength];
            System.arraycopy(result, 0, tbResult, 0, tbResult.length);
            out.add(new TraceBuf2(tbResult));
        }
        rs.close();
        stmt.close();
        return out;
    }

    public static Date j2KSecondsToDate(double j2kSeconds) {
        return new java.util.Date((long)(1000 * (j2kSeconds + Y1970_TO_Y2000_SECONDS)));
    }

    public static double dateToJ2kSeconds(Date date) {
        return date.getTime() / 1000 - Y1970_TO_Y2000_SECONDS;
    }

    public String getDatabaseURL() {
        return databaseURL;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPrefix() {
        return prefix;
    }

    Connection getConnection() throws SQLException {
        if (conn == null) {
            createConnection();
        }
        return conn;
    }

    public void createConnection() throws SQLException {
        if (driverClass == null) {
            try {
                Class.forName(driver).newInstance();
            } catch(Exception e) {
                SQLException sql = new SQLException("Cannot create driver: " + driver);
                sql.initCause(e);
                throw sql;
            }
        }
        conn = DriverManager.getConnection(getDatabaseURL(), getUsername(), getPassword());
    }

    public void close() throws SQLException {
        if (conn != null) {
            conn.close();
            conn = null;
        }
    }

    Class driverClass = null;

    Connection conn;

    String databaseURL;

    String username;

    String password;

    String prefix = "W_";

    String driver = MYSQL_DRIVER;

    public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";

    public static final long Y1970_TO_Y2000_SECONDS = 946728000l;
}
