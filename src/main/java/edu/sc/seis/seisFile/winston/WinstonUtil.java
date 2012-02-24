package edu.sc.seis.seisFile.winston;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.sc.seis.seisFile.syncFile.SyncFile;
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
        ResultSet rs = conn.createStatement().executeQuery("SHOW DATABASES");
        while (rs.next()) {
            String s = rs.getString(1);
            if (s.startsWith(getPrefix()) && !s.equals(getPrefix() + "ROOT")) {
                out.add(new WinstonSCNL(s, getPrefix()));
            }
        }
        return out;
    }

    public void useDatabase(WinstonSCNL channel) throws SQLException {
        conn.createStatement().execute("use " + channel.getDatabaseName());
    }

    public List<WinstonTable> listDayTables(WinstonSCNL channel) throws SQLException {
        List<WinstonTable> out = new ArrayList<WinstonTable>();
        useDatabase(channel);
        ResultSet rs = conn.createStatement().executeQuery("SHOW TABLES");
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
        for (WinstonTable wt : out) {
            if (wt.getYear() < startYear || wt.getYear() > endYear) {
                out.remove(wt);
            } else if (wt.getYear() == startYear) {
                if (wt.getMonth() < startMonth || (wt.getMonth() == startMonth && wt.getDay() < startDay)) {
                    out.remove(wt);
                }
            } else if (wt.getYear() == endYear) {
                if (wt.getMonth() > endMonth || (wt.getMonth() == endMonth && wt.getDay() > endDay)) {
                    out.remove(wt);
                }
            }
        }
        return out;
    }

    public SyncFile calculateSyncForDay(WinstonTable table) throws SQLException {
        SyncFile out = new SyncFile("Winston " + table.getTableName());
        useDatabase(table.getDatabase());
        SyncLine defaultSyncLine = new SyncLine(table.getDatabase().getNetwork(),
                                                table.getDatabase().getStation(),
                                                table.getDatabase().getLocId(),
                                                table.getDatabase().getChannel());
        ResultSet rs = conn.createStatement().executeQuery("select st, et, sr from " + table.getTableName()
                + " order by st");
        while (rs.next()) {
            out.addLine(new SyncLine(defaultSyncLine,
                                     j2KSecondsToDate(rs.getDouble(1)),
                                     j2KSecondsToDate(rs.getDouble(2)),
                                     rs.getFloat(3)),
                        true);
        }
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

    public Connection createConnection() throws SQLException {
        try {
            Class.forName(driver).newInstance();
        } catch(Exception e) {
            SQLException sql = new SQLException("Cannot create driver: " + driver);
            sql.initCause(e);
            throw sql;
        }
        Connection conn = DriverManager.getConnection(getDatabaseURL(), getUsername(), getPassword());
        return conn;
    }

    public void close() throws SQLException {
        if (conn != null) {
            conn.close();
            conn = null;
        }
    }

    Connection conn;

    String databaseURL;

    String username;

    String password;

    String prefix = "W_";

    String driver = MYSQL_DRIVER;

    public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";

    public static final long Y1970_TO_Y2000_SECONDS = 946728000l;
}
