package edu.sc.seis.seisFile.winston;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

import edu.sc.seis.seisFile.BuildVersion;
import edu.sc.seis.seisFile.QueryParams;
import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.syncFile.SyncFileWriter;

public class WinstonClient {

    protected WinstonClient(String[] args) throws SeisFileException, FileNotFoundException, IOException {
        params = new QueryParams(args);
        winstonConfig.put("winston.driver", WinstonUtil.MYSQL_DRIVER);
        winstonConfig.put("winston.prefix", "W");
        winstonConfig.put("winston.url", "jdbc:mysql://localhost/?user=wwsuser");
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--sync")) {
                doSync = true;
            } else if (args[i].equals("--steim1")) {
                doSteim1 = true;
            } else if (i < args.length - 1) {
                // arg with value
                if (args[i].equals("-p")) {
                    winstonConfig.load(new BufferedReader(new FileReader(args[i + 1])));
                } else if (args[i].equals("-u")) {
                    winstonConfig.put("winston.url", args[i + 1]);
                } else if (args[i].equals("--recLen")) {
                    recordSize = Integer.parseInt(args[i + 1]);
                }
            }
        }
        if (!doSync && params.getOutFile() == null) {
            params.setOutFile("output.mseed");
        }
    }

    QueryParams params;

    Properties winstonConfig = new Properties();

    boolean doSync = false;

    int recordSize = 12;

    boolean doSteim1 = false;

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        WinstonClient client = new WinstonClient(args);
        client.readData();
    }

    public void readData() throws SeisFileException, SQLException, DataFormatException, FileNotFoundException,
            IOException, URISyntaxException {
        if (params.isPrintHelp()) {
            System.out.println(getHelp());
            return;
        } else if (params.isPrintVersion()) {
            System.out.println("Version: " + BuildVersion.getDetailedVersion());
            return;
        } else if (params.getNetwork() == null || params.getStation() == null || params.getChannel() == null) {
            System.out.println(BuildVersion.getDetailedVersion() + " one of scnl is null: n=" + params.getNetwork()
                    + " s=" + params.getStation() + " l=" + params.getLocation() + " c=" + params.getChannel());
            System.out.println("LocId null is ok for scn, but needed for scnl");
            return;
        }
        if (params.isVerbose()) {
            WinstonUtil.setVerbose(true);
        }
        WinstonUtil winston = new WinstonUtil(getDbURL(),
                                              getUser(),
                                              getPassword(),
                                              winstonConfig.getProperty("winston.prefix"));
        List<WinstonSCNL> allChannels = winston.listChannelDatabases();
        Pattern staPattern = Pattern.compile(params.getStation());
        Pattern chanPattern = Pattern.compile(params.getChannel());
        Pattern netPattern = Pattern.compile(params.getNetwork());
        Pattern locPattern = Pattern.compile(params.getLocation());
        for (WinstonSCNL scnl : allChannels) {
            if (staPattern.matcher(scnl.getStation()).matches() &&
                    chanPattern.matcher(scnl.getChannel()).matches() &&
                    netPattern.matcher(scnl.getNetwork()).matches() &&
                    locPattern.matcher(scnl.getLocId()).matches()) {
                processChannel(winston, scnl);
            }
        }
    }

    void processChannel(WinstonUtil winston, WinstonSCNL channel) throws SeisFileException, SQLException,
            DataFormatException, FileNotFoundException, IOException, URISyntaxException {
        if (doSync) {
            Calendar cal = new GregorianCalendar();
            cal.setTimeZone(TimeZone.getTimeZone("GMT"));
            cal.setTime(params.getBegin());
            int startYear = cal.get(Calendar.YEAR);
            int startMonth = cal.get(Calendar.MONTH) + 1; // why are Calendar
                                                          // months zero based,
                                                          // but days are one
                                                          // based???
            int startDay = cal.get(Calendar.DAY_OF_MONTH);
            cal.setTime(params.getEnd());
            int endYear = cal.get(Calendar.YEAR);
            int endMonth = cal.get(Calendar.MONTH) + 1; // why are Calendar
                                                        // months zero based,
                                                        // but days are one
                                                        // based???
            int endDay = cal.get(Calendar.DAY_OF_MONTH);
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(params.getDataOutputStream())));
            SyncFileWriter syncOut = new SyncFileWriter("winston", out);
            winston.writeSyncBetweenDates(channel, startYear, startMonth, startDay, endYear, endMonth, endDay, syncOut);
            syncOut.close();
        } else {
            List<TraceBuf2> tbList = winston.extractData(channel, params.getBegin(), params.getEnd());
            for (TraceBuf2 traceBuf2 : tbList) {
                DataRecord mseed = traceBuf2.toMiniSeed(recordSize, doSteim1);
                mseed.write(params.getDataOutputStream());
            }
        }
        winston.close();
        params.getDataOutputStream().close();
    }

    String getDbURL() {
        return winstonConfig.getProperty("winston.url");
    }

    String getUser() throws URISyntaxException, SeisFileException {
        return getUrlQueryParam("user");
    }

    String getPassword() throws URISyntaxException, SeisFileException {
        return getUrlQueryParam("password");
    }

    String getUrlQueryParam(String name) throws SeisFileException, URISyntaxException {
        String[] urlParts = getDbURL().split("\\?")[1].split("\\&");
        for (int i = 0; i < urlParts.length; i++) {
            if (urlParts[i].startsWith(name + "=")) {
                return urlParts[i].substring((name + "=").length());
            }
        }
        throw new SeisFileException("Unable to find '" + name + "' query param in database url: " + getDbURL());
    }

    public String getHelp() {
        return "java "
                + WinstonClient.class.getName()
                + " [-p <winston.config file>][-u databaseURL][-n net][-s sta][-l loc][-c chan][-b yyyy-MM-dd[THH:mm:ss.SSS]][-e yyyy-MM-dd[THH:mm:ss.SSS]][-d seconds][-o outfile][-m maxpackets][--sync][--steim1][--recLen len(8-12)][--verbose][--version][--help]";
    }
}
