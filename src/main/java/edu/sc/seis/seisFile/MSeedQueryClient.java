package edu.sc.seis.seisFile;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;

public abstract class MSeedQueryClient {

    public MSeedQueryClient(String[] args) throws SeisFileException {
        BasicConfigurator.configure();
        params = new QueryParams(args);
        Logger.getRootLogger().setLevel(Level.WARN);
        if (params.isVerbose()) {
            Logger.getLogger("root").setLevel(Level.DEBUG);
        }
        if (params.isPrintHelp()) {
            System.out.println(getHelp());
            System.exit(0);
        } else if (params.isPrintVersion()) {
            System.out.println("Version: " + BuildVersion.getDetailedVersion());
            System.exit(0);
        }
    }

    public void readData() throws SeedFormatException, IOException, SeisFileException {
        if (params.isVerbose()) {
            reader.setVerbose(params.isVerbose());
        }
        PrintWriter out = new PrintWriter(System.out, true);
        List<DataRecord> data = reader.read(params.getNetwork(),
                                            params.getStation(),
                                            params.getLocation(),
                                            params.getChannel(),
                                            params.getBegin(),
                                            params.getEnd());
        for (DataRecord dr : data) {
            if (params.getDataOutputStream() != null) {
                dr.write(params.getDataOutputStream());
            }
            if (params.getDataOutputStream() == null || params.isVerbose()) {
                // print something to the screen if we are not saving to
                // disk
                dr.writeASCII(out, "    ");
                out.flush();
            }
        }
        if (params.isVerbose() && data.size() == 0) {
            out.println("No Data.");
            out.flush();
        }
        if (params.getDataOutputStream() != null) {
            params.getDataOutputStream().flush();
        }
        if (params.isVerbose()) {
            out.println("Finished: " + new Date());
        }
    }

    public abstract String getHelp();

    protected QueryParams params;

    protected MSeedQueryReader reader;
}
