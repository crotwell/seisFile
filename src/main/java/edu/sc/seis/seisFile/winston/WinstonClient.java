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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
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
        QueryParams defaults = new QueryParams(new String[] {"-n", "*", "-s", "*", "-l", "*", "-c", "*"});
        params = new QueryParams(args, defaults);
        winstonConfig.put("winston.driver", WinstonUtil.MYSQL_DRIVER);
        winstonConfig.put("winston.prefix", "W");
        winstonConfig.put("winston.url", "jdbc:mysql://localhost/?user=wwsuser&password=");
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
                } else if (args[i].equals("--export")) {
                    doExport = true;
                    exportPort = Integer.parseInt(args[i+1]);
                } else if (args[i].equals("--chunk")) {
                    chunkSeconds = Integer.parseInt(args[i+1]);
                } else if (args[i].equals("--module")) {
                    module = Integer.parseInt(args[i+1]);
                } else if (args[i].equals("--inst")) {
                    institution = Integer.parseInt(args[i+1]);
                } else if (args[i].equals("--heartbeat")) {
                    heartbeat = Integer.parseInt(args[i+1]);
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
    
    boolean doExport = false;
    
    int exportPort = -1;

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
        Pattern staPattern = Pattern.compile("*".equals(params.getStation())  ? ".*" : params.getStation());
        Pattern chanPattern = Pattern.compile("*".equals(params.getChannel()) ? ".*" : params.getChannel());
        Pattern netPattern = Pattern.compile("*".equals(params.getNetwork())  ? ".*" : params.getNetwork());
        Pattern locPattern = Pattern.compile("*".equals(params.getLocation()) ? ".*" : params.getLocation());
        if (doSync) {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(params.getDataOutputStream())));
            SyncFileWriter syncOut = new SyncFileWriter("winston", out);
            for (WinstonSCNL scnl : allChannels) {
                if (staPattern.matcher(scnl.getStation()).matches() && chanPattern.matcher(scnl.getChannel()).matches()
                        && netPattern.matcher(scnl.getNetwork()).matches()
                        && locPattern.matcher(scnl.getLocId()==null?"":scnl.getLocId()).matches()) {
                    syncChannel(winston, scnl, syncOut);
                }
            }
            syncOut.close();
        } else if (doExport) {
            EarthwormExport exporter = new EarthwormExport(exportPort, module, institution, "heartbeat", heartbeat);
            if (params.isVerbose()) {
                exporter.setVerbose(true);
                System.out.println("Waiting for client connect, port: "+exportPort);
            }
            exporter.waitForClient();
            Date startTime = params.getBegin();
            Date chunkBegin, chunkEnd;
            HashMap<WinstonSCNL, Date> lastSent = new HashMap<WinstonSCNL, Date>();
            for (WinstonSCNL scnl : allChannels) {
                
                if (staPattern.matcher(scnl.getStation()).matches() && chanPattern.matcher(scnl.getChannel()).matches()
                        && netPattern.matcher(scnl.getNetwork()).matches()
                        && locPattern.matcher(scnl.getLocId()==null?"":scnl.getLocId()).matches()) {
                    lastSent.put(scnl, startTime);
                }
            }
            while(startTime.before(params.getEnd())) {
                chunkEnd = new Date(startTime.getTime()+chunkSeconds*1000);
                for (WinstonSCNL scnl : lastSent.keySet()) {
                    chunkBegin = lastSent.get(scnl);
                    if (chunkBegin.before(chunkEnd)) {
                        Date sentEnd = exportChannel(winston, scnl, chunkBegin, chunkEnd, exporter);
                        lastSent.put(scnl, new Date(sentEnd.getTime()+100)); // just past last packet
                    }
                }
                startTime = new Date(chunkEnd.getTime()+1);
            }
            exporter.closeSocket();
            if (params.isVerbose()) {
                System.out.println("Done sending, "+exporter.getNumTraceBufSent()+" ("+exporter.getNumSplitTraceBufSent()+" after splitting)");
            }
        } else {
                for (WinstonSCNL scnl : allChannels) {
                    if (staPattern.matcher(scnl.getStation()).matches() && chanPattern.matcher(scnl.getChannel()).matches()
                            && netPattern.matcher(scnl.getNetwork()).matches()
                            && locPattern.matcher(scnl.getLocId()==null?"":scnl.getLocId()).matches()) {
                        processChannel(winston, scnl);
                    }
                }
                params.getDataOutputStream().close();
            }
        winston.close();
    }

    void syncChannel(WinstonUtil winston, WinstonSCNL channel, SyncFileWriter syncOut) throws SeisFileException,
            SQLException, DataFormatException, FileNotFoundException, IOException, URISyntaxException {
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
        winston.writeSyncBetweenDates(channel, startYear, startMonth, startDay, endYear, endMonth, endDay, syncOut);
    }

    Date exportChannel(WinstonUtil winston, WinstonSCNL channel, Date begin, Date end, EarthwormExport exporter) throws SeisFileException, SQLException,
            DataFormatException, FileNotFoundException, IOException, URISyntaxException {
        List<TraceBuf2> tbList = winston.extractData(channel, begin, end);
        Date lastSentEnd = end;
        double sampRate = 1;
        TraceBuf2 prev = null;
        for (TraceBuf2 traceBuf2 : tbList) {
            if (params.isVerbose()) {
                System.out.println("Tracebuf: "+traceBuf2.getNetwork()+"."+traceBuf2.getStation()+"."+traceBuf2.getLocId()+"."+traceBuf2.getChannel()+" "+traceBuf2.getStartDate()+" "+traceBuf2.getNumSamples()+" "+traceBuf2.getEndDate());
            }
            if (prev != null && prev.getEndDate().after(traceBuf2.getStartDate())) {
                System.out.println("WARNING: current tracebuf overlaps previous: ");
                System.out.println("  prev: "+prev);
                System.out.println("  curr: "+traceBuf2);
            }
            boolean notSent = true;
            while(notSent) {
                try {
                    exporter.export(traceBuf2);
                    notSent = false;
                } catch(IOException e) {
                    if (params.isVerbose()) {
                        System.out.println("Caught exception, waiting for reconnect, will resend tracebuf"+ e);
                    }
                    logger.warn("Caught exception, waiting for reconnect, will resend tracebuf", e);
                    exporter.closeClient();
                    exporter.waitForClient();
                }
            }
            if (lastSentEnd.before(traceBuf2.getEndDate())) {
                lastSentEnd = traceBuf2.getEndDate();
                sampRate = traceBuf2.getSampleRate();
            }
        }

        lastSentEnd = new Date(lastSentEnd.getTime()+(long)(1000/sampRate)+1);
        return lastSentEnd;
    }

    void processChannel(WinstonUtil winston, WinstonSCNL channel) throws SeisFileException, SQLException,
            DataFormatException, FileNotFoundException, IOException, URISyntaxException {
        List<TraceBuf2> tbList = winston.extractData(channel, params.getBegin(), params.getEnd());
        for (TraceBuf2 traceBuf2 : tbList) {
            List<DataRecord> mseedList = traceBuf2.toMiniSeedWithSplit(recordSize, doSteim1);
            for (DataRecord dr : mseedList) {
                dr.write(params.getDataOutputStream());
            }
        }
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
                + " "+QueryParams.getStandardHelpOptions()+"[-p <winston.config file>][-u databaseURL][--sync][--steim1][--recLen len(8-12)][[--export port][--chunk sec][--module modNum][--inst institutionNum][--heartbeat sec]]";
    }
    
    int heartbeat = DEFAULT_HEARTBEAT;
    int module = DEFAULT_MODULE;
    int institution = DEFAULT_INSTITUTION;
    int chunkSeconds = DEFAULT_CHUNK_SECONDS;

    public static final int DEFAULT_CHUNK_SECONDS = 60;
    public static final int DEFAULT_HEARTBEAT = 5;
    public static final int DEFAULT_MODULE = 255;
    public static final int DEFAULT_INSTITUTION = 255;
    
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(WinstonClient.class);
}
