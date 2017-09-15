package edu.sc.seis.seisFile.winston;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.iris.dmc.seedcodec.B1000Types;
import edu.sc.seis.seisFile.BuildVersion;
import edu.sc.seis.seisFile.QueryParams;
import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.TimeUtils;
import edu.sc.seis.seisFile.earthworm.EarthwormExport;
import edu.sc.seis.seisFile.earthworm.TraceBuf2;
import edu.sc.seis.seisFile.mseed.DataRecord;

public class WinstonClient {

    protected WinstonClient(String[] args) throws SeisFileException, FileNotFoundException, IOException {
        QueryParams defaults = new QueryParams(new String[] {"-n", "*", "-s", "*", "-l", "*", "-c", "*"});
        params = new QueryParams(args, defaults);
        List<String> leftOverArgs = params.getUnknownArgs();
        winstonConfig.put("winston.driver", WinstonUtil.MYSQL_DRIVER);
        winstonConfig.put("winston.prefix", "W");
        winstonConfig.put("winston.url", "jdbc:mysql://localhost/?user=wwsuser&password=");
        Iterator<String> it = leftOverArgs.iterator();
        while (it.hasNext()) {
            String nextArg = it.next();
            if (nextArg.equals("--sync")) {
                doSync = true;
            } else if (nextArg.equals("--steim1")) {
                if ( ! doSteim2) {
                    // only allow one of steim1 and steim2
                    doSteim1 = true;
                }
            } else if (nextArg.equals("--steim2")) {
                doSteim1 = false;
                doSteim2 = true;
            } else if (nextArg.equals("--heartbeatverbose")) {
                heartbeatverbose = true;
            } else if (nextArg.equals("--tbzip")) {
                doTbZip = true;
            } else if (nextArg.equals("--list")) {
                doChannelList = true;
            } else if (it.hasNext()) {
                // arg with value
                if (nextArg.equals("-p")) {
                    winstonConfig.load(new BufferedReader(new FileReader(it.next())));
                } else if (nextArg.equals("-u")) {
                    winstonConfig.put("winston.url", it.next());
                } else if (nextArg.equals("--recLen")) {
                    recordSize = Integer.parseInt(it.next());
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
                } else if (nextArg.equals("--sleepmillis")) {
                    sleepMillis = Integer.parseInt(it.next());
                } else if (nextArg.equals("--j2KSecondsToDate")) {
                    System.out.println(WinstonUtil.j2KSecondsToDate(Double.parseDouble(it.next())));
                    System.exit(0);
                } else {
                    throw new IllegalArgumentException("Unknown argument: "+nextArg);
                }
            } else {
                throw new IllegalArgumentException("Unknown argument: "+nextArg);
            }
        }
        if (!doSync && params.getOutFile() == null) {
            params.setOutFile("output.mseed");
        }
    }

    QueryParams params;

    Properties winstonConfig = new Properties();

    boolean doSync = false;

    boolean doExport = false;

    boolean doTbZip = false;
    
    boolean doChannelList = false;

    boolean heartbeatverbose = false;

    int exportPort = -1;

    int recordSize = 12;

    boolean doSteim1 = false;
    
    boolean doSteim2 = false;

    String heartbeatText = "alive";
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
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
        try {
        List<WinstonSCNL> allChannels = winston.listChannelDatabases();
        Pattern staPattern = Pattern.compile("*".equals(params.getStation()) ? ".*" : params.getStation());
        Pattern chanPattern = Pattern.compile("*".equals(params.getChannel()) ? ".*" : params.getChannel());
        Pattern netPattern = Pattern.compile("*".equals(params.getNetwork()) ? ".*" : params.getNetwork());
        Pattern locPattern = Pattern.compile("*".equals(params.getLocation()) ? ".*" : params.getLocation());
        if (doChannelList) {
            for (WinstonSCNL scnl : allChannels) {
                if (staPattern.matcher(scnl.getStation()).matches() && chanPattern.matcher(scnl.getChannel()).matches()
                        && netPattern.matcher(scnl.getNetwork()).matches()
                        && locPattern.matcher(scnl.getLocId() == null ? "" : scnl.getLocId()).matches()) {
                    List<WinstonTable> dayTables = winston.listDayTables(scnl);
                    Collections.sort(dayTables, new Comparator<WinstonTable>() {
                        public int compare(WinstonTable c1, WinstonTable c2) {
                            return c1.getYear()*10000+c1.getMonth()*100+c1.getDay() - c2.getYear()*10000+c2.getMonth()*100+c2.getDay();
                        }
                    });
                    String s = scnl.getDatabaseName()+" ";
                    if (dayTables.size() != 0) {
                        WinstonTable first = dayTables.get(0);
                        WinstonTable last = dayTables.get(dayTables.size()-1);
                        s+= " "+first.getDateString()+" "+last.getDateString();
                    }
                    System.out.println(s);
                } else {
                    logger.debug("Skipping, does not match patterns: "+scnl.getDatabaseName());
                }
            }
        } else if (doTbZip) {
            File f = new File(params.getOutFile());
            String dirName = f.getName();
            if (dirName.endsWith(".zip")) {
                dirName = dirName.substring(0, dirName.length()-4);
            } else {
                dirName = dirName+"_TraceBufs";
            }
            ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
            ZipEntry tbzip = new ZipEntry(dirName+"/");
            zip.putNextEntry(tbzip);
            zip.closeEntry();
            for (WinstonSCNL scnl : allChannels) {
                if (staPattern.matcher(scnl.getStation()).matches() && chanPattern.matcher(scnl.getChannel()).matches()
                        && netPattern.matcher(scnl.getNetwork()).matches()
                        && locPattern.matcher(scnl.getLocId() == null ? "" : scnl.getLocId()).matches()) {
                    outputRawTraceBuf2s(winston, scnl, zip, dirName);
                }
            }
            zip.close();
        } else if (doExport) {
            EarthwormExport exporter = new EarthwormExport(exportPort, module, institution, heartbeatText, heartbeat);
            if (heartbeatverbose) {
                exporter.getHeartbeater().setVerbose(heartbeatverbose);
            }
            if (params.isVerbose()) {
                exporter.setVerbose(true);
                System.out.println("Waiting for client connect, port: " + exportPort);
            }
            exporter.waitForClient();
            Instant startTime = params.getBegin();
            Instant chunkBegin, chunkEnd;
            HashMap<WinstonSCNL, Instant> lastSent = new HashMap<WinstonSCNL, Instant>();
            for (WinstonSCNL scnl : allChannels) {
                if (staPattern.matcher(scnl.getStation()).matches() && chanPattern.matcher(scnl.getChannel()).matches()
                        && netPattern.matcher(scnl.getNetwork()).matches()
                        && locPattern.matcher(scnl.getLocId() == null ? "" : scnl.getLocId()).matches()) {
                    lastSent.put(scnl, startTime);
                } else {
                    logger.debug("Skipping, does not match patterns: "+scnl.getDatabaseName());
                }
            }
            while (startTime.isBefore(params.getEnd())) {
                chunkEnd = startTime.plus(Duration.ofSeconds( chunkSeconds));
                for (WinstonSCNL scnl : lastSent.keySet()) {
                    chunkBegin = lastSent.get(scnl);
                    if (chunkBegin.isBefore(chunkEnd)) {
                        Instant sentEnd = exportChannel(winston, scnl, chunkBegin, chunkEnd, exporter);
                        // sendEnd is expected time of next sample, ie 1 samp period after end time of last tb
                        lastSent.put(scnl, sentEnd.plus(TimeUtils.ONE_MILLISECOND));
                    }
                }
                startTime = chunkEnd.plus(Duration.ofMillis(1));
            }
            exporter.closeSocket();
            if (params.isVerbose()) {
                System.out.println("Done sending, " + exporter.getNumTraceBufSent() + " ("
                        + exporter.getNumSplitTraceBufSent() + " too big so split)");
            }
        } else {
            for (WinstonSCNL scnl : allChannels) {
                if (staPattern.matcher(scnl.getStation()).matches() && chanPattern.matcher(scnl.getChannel()).matches()
                        && netPattern.matcher(scnl.getNetwork()).matches()
                        && locPattern.matcher(scnl.getLocId() == null ? "" : scnl.getLocId()).matches()) {
                    processChannel(winston, scnl);
                }
            }
            params.getDataOutputStream().close();
        }
        } finally {
            if (winston != null) {
                winston.close();
            }
        }
        
    }

    Instant exportChannel(WinstonUtil winston, WinstonSCNL channel, Instant begin, Instant end, EarthwormExport exporter)
            throws SeisFileException, SQLException, DataFormatException, FileNotFoundException, IOException,
            URISyntaxException {
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        List<TraceBuf2> tbList = winston.extractData(channel, begin, end);
        Instant lastSentEnd = end;
        double sampRate = 1;
        TraceBuf2 prev = null;
        for (TraceBuf2 traceBuf2 : tbList) {
            if (params.isVerbose()) {
                System.out.println("Tracebuf: " + traceBuf2.getNetwork() + "." + traceBuf2.getStation() + "."
                        + traceBuf2.getLocId() + "." + traceBuf2.getChannel() + " "
                        + sdf.format(traceBuf2.getStartDate()) + " " + traceBuf2.getNumSamples() + " "
                        + sdf.format(traceBuf2.getEndDate()));
            }
            if (prev != null && prev.getEndDate().isAfter(traceBuf2.getStartDate())) {
                System.out.println("WARNING: current tracebuf overlaps previous: ");
                System.out.println("  prev: " + prev);
                System.out.println("  curr: " + traceBuf2);
            }
            exporter.exportWithRetry(traceBuf2);
            if (lastSentEnd.isBefore(traceBuf2.getPredictedNextStartDate())) {
                lastSentEnd = traceBuf2.getPredictedNextStartDate();
                sampRate = traceBuf2.getSampleRate();
            }
            if (params.isVerbose()) {
                System.out.print("sleep: " + sleepMillis + " milliseconds " + sdf.format(Instant.now()) + " ...");
            }
            try {
                Thread.sleep(sleepMillis);
            } catch(InterruptedException e) {}
            if (params.isVerbose()) {
                System.out.println("...back to work at " + sdf.format(Instant.now()) + ".");
            }
        }
        return lastSentEnd;
    }

    void processChannel(WinstonUtil winston, WinstonSCNL channel) throws SeisFileException, SQLException,
            DataFormatException, FileNotFoundException, IOException, URISyntaxException {
        List<TraceBuf2> tbList = winston.extractData(channel, params.getBegin(), params.getEnd());
        for (TraceBuf2 traceBuf2 : tbList) {
            List<DataRecord> mseedList;
            if (doSteim1) {
                mseedList = traceBuf2.toMiniSeed(recordSize, B1000Types.STEIM1);
            } else if (doSteim2) {
                mseedList = traceBuf2.toMiniSeed(recordSize, B1000Types.STEIM2);
            } else {
                // no compression
                mseedList = traceBuf2.toMiniSeedNoCompression(recordSize);
            }
            for (DataRecord dr : mseedList) {
                dr.write(params.getDataOutputStream());
            }
        }
    }

    void outputRawTraceBuf2s(WinstonUtil winston, WinstonSCNL channel, ZipOutputStream zip, String dir) throws SeisFileException, SQLException,
            DataFormatException, FileNotFoundException, IOException, URISyntaxException {
        List<TraceBuf2> tbList = winston.extractData(channel, params.getBegin(), params.getEnd());
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH_mm_ss.SSS");
        for (TraceBuf2 tb : tbList) {
            ZipEntry tbzip = new ZipEntry(dir+"/"+tb.getNetwork().trim()+"."
                                          +tb.getStation().trim()+"."
                                          +tb.getLocId().trim()+"."
                                          +tb.getChannel().trim()+"."
                                          +sdf.format(tb.getStartDate())+".tb");
            zip.putNextEntry(tbzip);
            byte[] outBytes = tb.toByteArray();
            zip.write(outBytes, 0, outBytes.length);
            zip.closeEntry();
        }
    }

    String getDbURL() {
        return winstonConfig.getProperty("winston.url");
    }

    String getUser() throws URISyntaxException, SeisFileException {
        return WinstonUtil.getUrlQueryParam("user", getDbURL());
    }

    String getPassword() throws URISyntaxException, SeisFileException {
        return WinstonUtil.getUrlQueryParam("password", getDbURL());
    }

    public String getHelp() {
        return "java "
                + WinstonClient.class.getName()
                + " "
                + QueryParams.getStandardHelpOptions()
                + "[-p <winston.config file>][-u databaseURL][--steim1][--recLen len(8-12)][[--export port][--chunk sec][--module modNum][--inst institutionNum][--heartbeat sec][--heartbeatverbose]]";
    }

    int heartbeat = DEFAULT_HEARTBEAT;

    int module = DEFAULT_MODULE;

    int institution = DEFAULT_INSTITUTION;

    int chunkSeconds = DEFAULT_CHUNK_SECONDS;

    int sleepMillis = 0;

    public static final int DEFAULT_CHUNK_SECONDS = 60;

    public static final int DEFAULT_HEARTBEAT = 5;

    public static final int DEFAULT_MODULE = 255;

    public static final int DEFAULT_INSTITUTION = 255;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(WinstonClient.class);
}
