package edu.sc.seis.seisFile.winston;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;
import java.util.zip.DataFormatException;

import org.apache.log4j.BasicConfigurator;

import edu.sc.seis.seisFile.QueryParams;
import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.earthworm.EarthwormExport;
import edu.sc.seis.seisFile.earthworm.TraceBuf2;
import edu.sc.seis.seisFile.syncFile.SyncFile;
import edu.sc.seis.seisFile.syncFile.SyncFileCompare;
import edu.sc.seis.seisFile.syncFile.SyncFileReader;
import edu.sc.seis.seisFile.syncFile.SyncLine;

public class WinstonExport {

    public static void main(String[] args) {
        BasicConfigurator.configure();
        try {
            WinstonExport wE = new WinstonExport(args);
            wE.doit();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private int chunkSeconds = 86400;

    void doit() throws IOException, SeisFileException, URISyntaxException, SQLException, DataFormatException {
        WinstonUtil winstonUtil = new WinstonUtil(winstonConfig);
        SyncFile remoteSyncFile = SyncFile.load(new File(syncfile));
        HashMap<WinstonSCNL, SyncFile> remoteSFMap = splitByChannel(remoteSyncFile, winstonUtil.getPrefix());
        logger.info("Remote sync file (" + syncfile + ") " + remoteSFMap.keySet().size() + " channels.");
        EarthwormExport exporter = setUpExport();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        SyncFile sentSync = new SyncFile("winston sent to dmc", sdf.format(new Date()));
        for (WinstonSCNL scnl : remoteSFMap.keySet()) {
            SyncFile localSF = pullLocalSyncFile(scnl,
                                                 params.getBegin(),
                                                 params.getEnd(),
                                                 winstonUtil);
            SyncFileCompare sfCompare = new SyncFileCompare(localSF, remoteSFMap.get(scnl));
            localSF.saveToFile(scnl.getNSLCWithDots()+"_local.sync");
            SyncFile hereNotThere = sfCompare.getInAnotB();
            hereNotThere.saveToFile(scnl.getNSLCWithDots()+"_InlocalNotRemote.sync");
            logger.info(scnl + " here not there synclines=" + hereNotThere.size());
            for (SyncLine sl : hereNotThere) {
                Date s = new Date(sl.getStartTime().getTime()+1); // 1 millisecond past sync start to avoid duplicates
                Date end = new Date(sl.getEndTime().getTime()-1); // 1 millisecond before sync end to avoid duplicates
                if (end.before(params.getBegin()) || s.after(params.getEnd()) || end.getTime()-s.getTime() < minGapMillis) {
                    continue;
                }
                // stay within cmd line dates
                if (s.before(params.getBegin())) {
                    s = params.getBegin();
                }
                if (end.after(params.getEnd())) {
                    end = params.getEnd();
                }
                while (s.before(end)) {
                    Date e = new Date(s.getTime() + chunkSeconds * 1000);
                    if (e.after(end)) {
                        e = end;
                    }
                    Date lastEnd = e;
                    List<TraceBuf2> tbList = winstonUtil.extractData(scnl, s, e);
                    System.out.println("    Extract " + tbList.size() + " from " + sdf.format(s) + " to "
                            + sdf.format(e));
                    for (TraceBuf2 traceBuf2 : tbList) {
                        Date tbStart = traceBuf2.getStartDate();
                        Date tbEnd = traceBuf2.getEndDate();
                        // check vs start and end to avoid sending data remote
                        // already has
                        // check vs s to avoid sending same packet twice
                        if (!(tbStart.before(s) || tbEnd.after(end))) {
                            exporter.exportWithRetry(traceBuf2);
                            sentSync.addLine(new SyncLine(sl, tbStart, tbEnd), true);
                            if (tbEnd.after(lastEnd)) {
                                lastEnd = tbEnd;
                            }
                        }
                    }
                    s = lastEnd;
                }
            }
        }
        sentSync.saveToFile(syncfile.replace(".sync", "")+"_sent.sync");
    }

    public WinstonExport(String[] args) throws FileNotFoundException, IOException, SeisFileException {
        QueryParams defaults = new QueryParams(new String[] {"-n", "*", "-s", "*", "-l", "*", "-c", "*"});
        params = new QueryParams(args, defaults);
        List<String> leftOverArgs = params.getUnknownArgs();
        winstonConfig.put("winston.driver", WinstonUtil.MYSQL_DRIVER);
        winstonConfig.put("winston.prefix", "W");
        winstonConfig.put("winston.url", "jdbc:mysql://localhost/?user=wwsuser&password=");
        Iterator<String> it = leftOverArgs.iterator();
        while (it.hasNext()) {
            String nextArg = it.next();
            if (nextArg.equals("--heartbeatverbose")) {
                heartbeatVerbose = true;
            } else if (it.hasNext()) {
                // arg with value
                if (nextArg.equals("-p")) {
                    winstonConfig.load(new BufferedReader(new FileReader(it.next())));
                } else if (nextArg.equals("-u")) {
                    winstonConfig.put("winston.url", it.next());
                } else if (nextArg.equals("--syncfile")) {
                    syncfile = it.next();
                } else if (nextArg.equals("--export")) {
                    doExport = true;
                    exportPort = Integer.parseInt(it.next());
                } else if (nextArg.equals("--chunk")) {
                    chunkSeconds = Integer.parseInt(it.next());
                } else if (nextArg.equals("--module")) {
                    module = Integer.parseInt(it.next());
                } else if (nextArg.equals("--inst")) {
                    institution = Integer.parseInt(it.next());
                } else if (nextArg.equals("--heartbeat")) {
                    heartbeat = Integer.parseInt(it.next());
                } else if (nextArg.equals("--heartbeatText")) {
                    heartbeatText = it.next();
                } else if (nextArg.equals("--mingapmillis")) {
                    minGapMillis = Integer.parseInt(it.next());
                } else {
                    throw new IllegalArgumentException("Unknown argument: " + nextArg);
                }
            } else {
                throw new IllegalArgumentException("Unknown argument: " + nextArg);
            }
        }
    }

    HashMap<WinstonSCNL, List<Date>> syncFileMinMax(File remoteSyncFile, String winstonPrefix) throws IOException {
        HashMap<WinstonSCNL, List<Date>> out = new HashMap<WinstonSCNL, List<Date>>();
        SyncFileReader reader = new SyncFileReader(remoteSyncFile);
        SyncLine sl;
        while (reader.hasNext()) {
            sl = reader.next();
            WinstonSCNL scnl = new WinstonSCNL(sl.getSta(), sl.getChan(), sl.getNet(), sl.getLoc(), winstonPrefix);
            if (!out.containsKey(scnl)) {
                out.put(scnl, Arrays.asList(new Date[] {sl.getStartTime(), sl.getEndTime()}));
            } else {
                List<Date> dates = out.get(scnl);
                if (dates.get(0).after(sl.getStartTime())) {
                    dates.set(0, sl.getStartTime());
                }
                if (dates.get(1).before(sl.getEndTime())) {
                    dates.set(1, sl.getEndTime());
                }
            }
        }
        return out;
    }

    SyncFile pullLocalSyncFile(WinstonSCNL scnl, Date begin, Date end, WinstonUtil winstonUtil) throws SQLException {
        Calendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("GMT"));
        cal.setTime(begin);
        int startYear = cal.get(Calendar.YEAR);
        int startMonth = cal.get(Calendar.MONTH) + 1; // why are Calendar
                                                      // months zero based,
                                                      // but days are one
                                                      // based???
        int startDay = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(end);
        int endYear = cal.get(Calendar.YEAR);
        int endMonth = cal.get(Calendar.MONTH) + 1; // why are Calendar
                                                    // months zero based,
                                                    // but days are one
                                                    // based???
        int endDay = cal.get(Calendar.DAY_OF_MONTH);
        SyncFile sf = winstonUtil.calculateSyncBetweenDates(scnl,
                                                            startYear,
                                                            startMonth,
                                                            startDay,
                                                            endYear,
                                                            endMonth,
                                                            endDay,
                                                            winstonUtil.getDatabaseURL());
        return sf;
    }

    public HashMap<WinstonSCNL, SyncFile> splitByChannel(SyncFile syncFile, String winstonPrefix) {
        HashMap<WinstonSCNL, SyncFile> out = new HashMap<WinstonSCNL, SyncFile>();
        for (SyncLine sl : syncFile) {
            WinstonSCNL scnl = new WinstonSCNL(sl.getSta(), sl.getChan(), sl.getNet(), sl.getLoc(), winstonPrefix);
            if (!out.containsKey(scnl)) {
                SyncFile sf = new SyncFile(syncFile.getDccName() + " " + scnl);
                out.put(scnl, sf);
            }
            out.get(scnl).addLine(sl);
        }
        return out;
    }

    EarthwormExport setUpExport() throws IOException {
        EarthwormExport exporter = new EarthwormExport(exportPort, module, institution, heartbeatText, heartbeat);
        if (heartbeatVerbose) {
            exporter.getHeartbeater().setVerbose(heartbeatVerbose);
        }
        if (params.isVerbose()) {
            exporter.setVerbose(true);
            System.out.println("Waiting for client connect, port: " + exportPort);
        }
        exporter.waitForClient();
        return exporter;
    }

    int minGapMillis = 100;
    
    boolean doExport = false;

    int exportPort = -1;

    String syncfile;

    boolean heartbeatVerbose = false;

    int heartbeat = WinstonClient.DEFAULT_HEARTBEAT;

    String heartbeatText = "alive";

    int module = WinstonClient.DEFAULT_MODULE;

    int institution = WinstonClient.DEFAULT_INSTITUTION;

    QueryParams params;

    Properties winstonConfig = new Properties();

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(WinstonExport.class);
}
