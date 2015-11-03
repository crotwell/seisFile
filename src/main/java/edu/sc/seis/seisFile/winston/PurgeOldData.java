package edu.sc.seis.seisFile.winston;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.log4j.BasicConfigurator;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.stringparsers.FileStringParser;

import edu.sc.seis.seisFile.QueryParams;
import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.client.AbstractClient;
import edu.sc.seis.seisFile.syncFile.SyncFile;
import edu.sc.seis.seisFile.syncFile.SyncFileCompare;
import edu.sc.seis.seisFile.syncFile.SyncLine;

public class PurgeOldData extends AbstractClient {

    public PurgeOldData(String[] args) throws JSAPException {
        super(args);
        winstonConfig.put("winston.driver", WinstonUtil.MYSQL_DRIVER);
        winstonConfig.put("winston.prefix", "W");
        winstonConfig.put("winston.url", "jdbc:mysql://localhost/?user=wwsuser&password=");
    }

    @Override
    protected void addParams() throws JSAPException {
        super.addParams();
        add(new FlaggedOption(YEAR_DAY,
                              JSAP.STRING_PARSER,
                              null,
                              false,
                              JSAP.NO_SHORTFLAG,
                              YEAR_DAY,
                              "Date, as yyyy-mm-dd."));
        add(new FlaggedOption(SYNCFILE,
                              FileStringParser.getParser(),
                              null,
                              false,
                              JSAP.NO_SHORTFLAG,
                              SYNCFILE,
                              "Syncfile from the upstream datacenter."));
        add(new FlaggedOption(WINSTON_CONFIG,
                              FileStringParser.getParser(),
                              "Winston.config",
                              false,
                              JSAP.NO_SHORTFLAG,
                              WINSTON_CONFIG,
                              "Winston configuration prop file."));
        add(new Switch(DRY_RUN, JSAP.NO_SHORTFLAG, DRY_RUN, "Dry run, find data to delete and then exit"));
    }

    public void purge() throws IOException, SeisFileException, URISyntaxException, SQLException {
        if (shouldPrintHelp()) {
            System.out.println(getHelp());
            System.exit(0);
        }
        upstreamSyncfile = SyncFile.load(getResult().getFile(SYNCFILE));
        winstonConfig.load(new BufferedReader(new FileReader(getResult().getFile(WINSTON_CONFIG))));
        WinstonUtil winston = new WinstonUtil(winstonConfig);
        HashMap<String, SyncFile> upstreamMap = upstreamSyncfile.splitByChannel();
        for (Entry<String, SyncFile> entry : upstreamMap.entrySet()) {
            System.out.println("Work on " + entry.getKey());
            SyncLine first = entry.getValue().getSyncLines().get(0);
            WinstonSCNL scnl = winston.createWinstonSCNL(first.getSta(),
                                                         first.getChan(),
                                                         first.getNet(),
                                                         first.getLoc());
            Calendar startCal = Calendar.getInstance(QueryParams.UTC);
            startCal.setTime(entry.getValue().getEarliest());
            Calendar endCal = Calendar.getInstance(QueryParams.UTC);
            endCal.setTime(entry.getValue().getLatest());
            System.out.println(scnl+" Calc dates "+startCal.getTime()+"  "+endCal.getTime());
            SyncFile winstonSync = winston.calculateSyncBetweenDates(scnl,
                                                                     startCal.get(Calendar.YEAR),
                                                                     startCal.get(Calendar.MONTH)+1, // why are months zero based?
                                                                     startCal.get(Calendar.DAY_OF_MONTH),
                                                                     endCal.get(Calendar.YEAR),
                                                                     endCal.get(Calendar.MONTH)+1, // why are months zero based?
                                                                     endCal.get(Calendar.DAY_OF_MONTH),
                                                                     "Winston");
            SyncFileCompare compare = new SyncFileCompare(entry.getValue(), winstonSync);
            SyncFile notInUpstream = compare.getNotAinB();
            String syncFileBase = entry.getKey()+".sync";
            if (notInUpstream.size() != 0) {
                notInUpstream.cleanSmallSegments(1).saveToFile("NotUpstream_" + syncFileBase);
                System.out.println("Save to " + "NotUpstream_" + syncFileBase);
            } else {
                System.out.println("No data not already in upstream sync for " + entry.getKey());
            }
            SyncFile inboth = compare.getInAinB();
            inboth.saveToFile("Both_"+syncFileBase);
            System.out.println("in both to "+"Both_"+syncFileBase+"  line="+inboth.size());
            winstonSync.saveToFile("winston_"+syncFileBase);
            System.out.println("winston_"+syncFileBase+"   lines="+winstonSync.size());
            entry.getValue().saveToFile("upstream_"+syncFileBase);
        }
    }

    public static void main(String[] args) throws Exception {
        BasicConfigurator.configure();
        PurgeOldData purger = new PurgeOldData(args);
        purger.purge();
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

    Properties winstonConfig = new Properties();

    SyncFile upstreamSyncfile;

    public static final String YEAR_DAY = "date";

    public static final String DRY_RUN = "dryrun";

    public static final String WINSTON_CONFIG = "winston";

    public static final String SYNCFILE = "syncfile";
}
