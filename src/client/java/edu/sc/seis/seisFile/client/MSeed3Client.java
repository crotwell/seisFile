package edu.sc.seis.seisFile.client;

import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.SeedRecord;
import edu.sc.seis.seisFile.mseed3.FDSNSourceIdException;
import edu.sc.seis.seisFile.mseed3.MSeed3Convert;
import edu.sc.seis.seisFile.mseed3.MSeed3Record;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParseResult;

@Command(name = "mseed3",
        description = "list miniseed3 records and convert from ver 2 records",
        versionProvider = edu.sc.seis.seisFile.client.VersionProvider.class)
public class MSeed3Client extends AbstractClient {

    @Option(names = {"-r", "--regex"}, description = "regular expression of sourceids to search")
    public Pattern sourceIdPattern;

    @Option(names = {"--max"}, description = "number of data records to process before ending", defaultValue = "-1")
    public int maxRecords = -1;

    @Option(names = {"--2to3"}, description = "convert miniseed2 to miniseed3")
    public boolean convert2to3 = false;

    @Option(names = {"--data"}, description = "dump timeseries samples, default is to just print headers", defaultValue = "false")
    boolean dumpData = false;

    @Option(names = {"-o", "--out"}, description = "Output file (default: print to console)")
    private File outputFile;

    @Parameters
    List<File> inputFileList;


    @Override
    public Integer call() throws Exception {
        ParseResult parsedArgs = spec.commandLine().getParseResult();
        if (requiresAtLeastOneArg() && parsedArgs.expandedArgs().size() == 0) {
            throw new ParameterException(spec.commandLine(), "Must use at least one option");
        }
        if (convert2to3) {
            return doConvertTo3();
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

    public static void main(String... args) { // bootstrap the application
        System.exit(new CommandLine(new MSeed3Client()).execute(args));
    }
}
