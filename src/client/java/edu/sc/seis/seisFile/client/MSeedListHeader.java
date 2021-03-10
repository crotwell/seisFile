package edu.sc.seis.seisFile.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.iris.dmc.seedcodec.CodecException;
import edu.iris.dmc.seedcodec.DecompressedData;
import edu.iris.dmc.seedcodec.UnsupportedCompressionType;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import edu.sc.seis.seisFile.mseed.SeedRecord;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParseResult;

@Command(name="mseedlh", 
         description="list miniseed record headers",
         versionProvider=edu.sc.seis.seisFile.client.VersionProvider.class)
public class MSeedListHeader extends AbstractClient {


    @Option(names= {"-n", "--network"}, description="list of networks to search")
    List<String> network = new ArrayList<String>();
    @Option(names= {"-s", "--station"}, description="list of stations to search")
    List<String> station = new ArrayList<String>();;
    @Option(names= {"-l", "--location"}, description="list of locations to search")
    List<String> location = new ArrayList<String>();;
    @Option(names= {"-c", "--channel"}, description="list of channels to search")
    List<String> channel = new ArrayList<String>();;

    @Option(names= { "--max"}, description="number of data records to process before ending", defaultValue="-1")
    public int maxRecords = -1;

    @Option(names= { "--rec"}, description="default record size if record is missing a B1000", defaultValue="512")
    public int defaultRecordSize = 512;
    
    @Option(names= { "--data"}, description="dump timeseries samples, default is to just print headers", defaultValue="false")
    boolean dumpData = false;
    
    @Option(names = { "-o", "--out" }, description = "Output file (default: print to console)")
    private File outputFile;
    
    @Parameters() 
    List<File> files;

    
    
    @Override
    public Integer call() throws Exception {
        ParseResult parsedArgs = spec.commandLine().getParseResult();
        if (requiresAtLeastOneArg() && parsedArgs.expandedArgs().size() == 0) {
            throw new ParameterException(spec.commandLine(), "Must use at least one option");
        }
        DataOutputStream dos = null;
        if (outputFile != null) {
            dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
        }
        PrintWriter out = new PrintWriter(System.out, true);
        for (File filename : files) {
            processFile(filename,
                        network,
                        station,
                        location,
                        channel,
                        maxRecords,
                        defaultRecordSize,
                        verbose,
                        dumpData,
                        dos,
                        out);
        }
        if (dos != null) {
            dos.close();
        }
        
        return 0;
    }


    public static void processFile(File infile,
                                   List<String> network,
                                   List<String> station,
                                   List<String> location,
                                   List<String> channel,
                                   int maxRecords,
                                   int defaultRecordSize,
                                   boolean verbose,
                                   boolean dumpData,
                                   DataOutputStream dos,
                                   PrintWriter out) throws IOException, SeedFormatException {
        InputStream inStream;
        if (infile == null) {
            inStream = System.in;
        } else if (infile.exists() && infile.isFile()) {
            inStream = new FileInputStream(infile);
        } else {
            throw new IOException("Unable to read file: "+infile.getAbsolutePath());
        }
        // if you wish to customize the blockette creation, for example to add
        // new types of Blockettes,
        // create an object that implements BlocketteFactory and then
        // SeedRecord.setBlocketteFactory(myBlocketteFactory);
        // see DefaultBlocketteFactory for an example
        DataInputStream dataInStream = new DataInputStream(new BufferedInputStream(inStream, 1024));
        int i = 0;
        try {
            while (maxRecords == -1 || i < maxRecords) {
                SeedRecord sr = SeedRecord.read(dataInStream, defaultRecordSize);
                if (sr instanceof DataRecord) {
                    DataRecord dr = (DataRecord)sr;
                    if ((network == null || network.size() == 0 || network.contains(dr.getHeader().getNetworkCode()))
                            && (station == null || station.size() == 0 || station.contains(dr.getHeader().getStationIdentifier()))
                            && (location == null || location.size() == 0 || location.contains(dr.getHeader().getLocationIdentifier()))
                            && (channel == null || channel.size() == 0 || channel.contains(dr.getHeader().getChannelIdentifier()))) {

                        i++;
                        if (dos != null) {
                            dr.write(dos);
                        }
                        if (dos == null || verbose) {
                            // print something to the screen if we are not
                            // saving to disk
                            dr.writeASCII(out, "    ");
                            out.flush();
                        }
                        if (dumpData) {
                            out.println("# compressed");
                            dr.printData(out);
                            out.println("# decompressed");
                            try {
                                DecompressedData dd = dr.decompress();
                                int[] intData = dd.getAsInt();
                                for (int j = 0; j < intData.length; j++) {
                                    out.print(intData[j] + " ");
                                    if (j % 10 == 9 || j == intData.length-1) {
                                        out.println();
                                    }
                                }
                            } catch(UnsupportedCompressionType e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch(CodecException e) {
                                throw new SeedFormatException("Unable to read record "+i+" from "+infile.getPath(), e);
                            }
                            out.flush();
                        }
                    }
                } else {
                    // print non-data records just because...
                    sr.writeASCII(out, "    ");
                    out.flush();
                }
            }
        } catch(EOFException e) {
            // done I guess
        } finally {
            if (dataInStream != null) {
                dataInStream.close();
            }
        }
    }

    
    public static void main(String... args) { // bootstrap the application
        System.exit(new CommandLine(new MSeedListHeader()).execute(args));
    }
}
