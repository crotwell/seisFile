package edu.sc.seis.seisFile.client;

import edu.sc.seis.seisFile.fdsnws.quakeml.Event;
import edu.sc.seis.seisFile.fdsnws.quakeml.Origin;
import edu.sc.seis.seisFile.fdsnws.quakeml.Quakeml;
import edu.sc.seis.seisFile.fdsnws.stationxml.Channel;
import edu.sc.seis.seisFile.fdsnws.stationxml.FDSNStationXML;
import edu.sc.seis.seisFile.fdsnws.stationxml.Network;
import edu.sc.seis.seisFile.fdsnws.stationxml.Station;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.SeedRecord;
import edu.sc.seis.seisFile.mseed3.FDSNSourceIdException;
import edu.sc.seis.seisFile.mseed3.MSeed3Convert;
import edu.sc.seis.seisFile.mseed3.MSeed3EH;
import edu.sc.seis.seisFile.mseed3.MSeed3Record;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import edu.sc.seis.seisFile.sac.SacTimeSeries;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParseResult;

@Command(name = "mseed3",
        description = "list miniseed3 records, or convert from ver 2 records, or set extra headers from StationXML, QuakeML",
        versionProvider = edu.sc.seis.seisFile.client.VersionProvider.class)
public class MSeed3Client extends AbstractClient {

    @Option(names = {"-r", "--regex"}, description = "regular expression of sourceids to search")
    public Pattern sourceIdPattern;

    @Option(names = {"--max"}, description = "number of data records to process before ending", defaultValue = "-1")
    public int maxRecords = -1;

    @Option(names = {"--2to3"}, description = "convert miniseed2 to miniseed3")
    public boolean convert2to3 = false;

    @Option(names = {"--sacto3"}, description = "convert SAC to miniseed3")
    public boolean convertSacto3 = false;

    @Option(names = {"--data"}, description = "dump timeseries samples, default is to just print headers", defaultValue = "false")
    boolean dumpData = false;

    @Option(names = {"-o", "--out"}, description = "Output file (default: print to console)")
    private File outputFile = null;

    @Option(names = {"--staxml"}, description = "Set standard extra headers for station from stationxml file")
    File staxmlFile;

    @Option(names = {"--quakeml"}, description = "Set standard extra headers for event from quakeml file")
    File quakemlFile;

    Duration eventSearchTol = Duration.ofHours(1);
    @Parameters
    List<File> inputFileList;


    @Override
    public Integer call() throws Exception {
        ParseResult parsedArgs = spec.commandLine().getParseResult();
        if (requiresAtLeastOneArg() && parsedArgs.expandedArgs().isEmpty()) {
            throw new ParameterException(spec.commandLine(), "Must use at least one option");
        }
        if (convert2to3) {
            return doConvertTo3();
        } else if (convertSacto3) {
                return doConvertSacTo3();
        } else if (staxmlFile != null || quakemlFile != null) {
            int retVal = 0;
            Map<Network, List<Station>> netList = new HashMap<>();
            if (staxmlFile != null) {
                netList= FDSNStationXML.loadStationXML(staxmlFile).extractAllNetworks();
            }
            List<Event> events = new ArrayList<>();
            if (quakemlFile != null) {
                BufferedReader buf = new BufferedReader(new FileReader(quakemlFile));
                Quakeml qml = Quakeml.loadQuakeML(buf);
                events = qml.extractAllEvents();
            }
            for (File infile : inputFileList) {
                if (verbose) {
                    System.out.println("Read from " + infile.toString());
                }
                DataInputStream dis = null;
                File tempFile = File.createTempFile("seisFile", "ms3");
                DataOutputStream dos = null;
                try {
                    int fileBytes = (int) infile.length();
                    int bytesRead = 0;
                    MSeed3Record dr3;
                    dis = new DataInputStream(new BufferedInputStream(new FileInputStream(infile)));
                    dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(tempFile)));
                    while (bytesRead < fileBytes && (dr3 = MSeed3Record.read(dis)) != null) {
                        bytesRead += dr3.getSize();
                        if (sourceIdPattern == null || sourceIdPattern.matcher(dr3.getSourceIdStr()).matches()) {
                            Channel chan = FDSNStationXML.findChannelBySID(netList, dr3.getSourceId(), dr3.getStartInstant());
                            MSeed3EH ms3eh = new MSeed3EH(dr3.getExtraHeaders());
                            ms3eh.addToBag(chan);
                            Event ev = findQuakeInTime(events, dr3.getStartInstant(), eventSearchTol);
                            if (ev != null) {
                                ms3eh.addToBag(ev);
                            }
                        }
                        dr3.write(dos);
                    }
                    if (dos != null) {
                        dos.close();
                        tempFile.renameTo(infile);
                        dos = null;
                    }
                } catch (EOFException e) {
                    System.err.println(e);
                    e.printStackTrace();
                    // done...
                } finally {
                    if (dis != null) {
                        dis.close();
                    }
                    if (dos != null) {
                        dos.close();
                    }
                }
            }
            return retVal;
        } else {
            return printMSeed3();
        }
    }

    public Integer printMSeed3() throws IOException, SeedFormatException, FDSNSourceIdException {
        PrintWriter pw;
        if (outputFile != null) {
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile))));
        } else {
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
        }
        for (File infile : inputFileList) {
            if (verbose) {
                System.out.println("Read from " + infile.toString());
            }
            int bytesRead = 0;
            int fileBytes = 0;
            MSeed3Record dr3;
            int drNum = 0;
            int skippedRecords = 0;
            try {
                fileBytes = (int) infile.length();
                DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(infile)));
                while (bytesRead < fileBytes && (dr3 = MSeed3Record.read(dis)) != null) {
                    if (sourceIdPattern == null || sourceIdPattern.matcher(dr3.getSourceIdStr()).matches()) {
                        pw.println("--------- read record " + drNum++);
                        dr3.printASCII(pw, "  ", dumpData);

                    } else {
                        skippedRecords++;
                    }
                    bytesRead += dr3.getSize();
                }

                if (verbose) {
                    System.out.println("Read " + bytesRead + " file size=" + fileBytes+", skipped "+skippedRecords+" records.");
                }
            } catch (EOFException e) {
                System.err.println(e);
                e.printStackTrace();
                // done...
            } finally {
                if (pw != null) {
                    pw.flush();
                }
            }
        }
        if (pw != null) {
            pw.close();
        }
        return 0;
    }

    public Integer doConvertTo3() throws Exception {
        DataOutputStream dos = null;
        if (outputFile != null) {
            dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
        }
        for (File infile : inputFileList) {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(infile)));

            DataRecord dr2;
            if (dos == null) {
                File outFile = new File(infile.getName() + ".ms3");
                dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
            }

            try {
                while ((dr2 = (DataRecord) SeedRecord.read(dis, 0)) != null) {
                    MSeed3Record ms3 = MSeed3Convert.convert2to3(dr2);
                    ms3.write(dos);
                }
            } catch (EOFException e) {
                // done...
            } finally {
                if (dos != null && outputFile == null) {
                    // file per file, close, otherwise leave open for next input file
                    dos.close();
                    dos = null;
                }
            }

        }
        if (dos != null) {
            dos.close();
        }
        return 0;
    }


    public Integer doConvertSacTo3() throws Exception {
        DataOutputStream dos = null;
        if (outputFile != null) {
            dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
        }
        for (File infile : inputFileList) {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(infile)));

            SacTimeSeries sac;
            if (dos == null) {
                File outFile = new File(infile.getName() + ".ms3");
                dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)));
            }

            try {
                sac = SacTimeSeries.read(dis);
                MSeed3Record ms3 = MSeed3Convert.convertSacTo3(sac);
                ms3.write(dos);
            } finally {
                if (dos != null && outputFile == null) {
                    // file per file, close, otherwise leave open for next input file
                    dos.close();
                    dos = null;
                }
            }

        }
        if (dos != null) {
            dos.close();
        }
        return 0;
    }

    /**
     * Finds the first Event in a list that is within +- the tolerance of the given time.
     * @param quakes
     * @param time
     * @param tol
     * @return
     */
    public static Event findQuakeInTime(List<Event> quakes, Instant time, Duration tol) {
        Instant early = time.minus(tol);
        Instant late = time.plus(tol);
        for (Event e : quakes) {
            Origin o = e.getPreferredOrigin();
            if (o != null) {
                Instant otime = o.getTime().asInstant();
                if (otime.isAfter(early) && otime.isBefore(late)) {
                    return e;
                }
            }
        }
        return null;
    }

    public static void main(String... args) { // bootstrap the application
        System.exit(new CommandLine(new MSeed3Client()).execute(args));
    }
}
