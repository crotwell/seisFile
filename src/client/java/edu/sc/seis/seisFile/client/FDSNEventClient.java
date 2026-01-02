package edu.sc.seis.seisFile.client;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.net.URI;

import javax.xml.stream.XMLStreamException;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.FDSNEventQuerier;
import edu.sc.seis.seisFile.fdsnws.FDSNEventQueryParams;
import edu.sc.seis.seisFile.fdsnws.quakeml.Event;
import edu.sc.seis.seisFile.fdsnws.quakeml.EventIterator;
import edu.sc.seis.seisFile.fdsnws.quakeml.Magnitude;
import edu.sc.seis.seisFile.fdsnws.quakeml.Origin;
import edu.sc.seis.seisFile.fdsnws.quakeml.QuakeMLTagNames;
import edu.sc.seis.seisFile.fdsnws.quakeml.Quakeml;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;

@Command(name="fdsnevent",
         description="example client to query a remote FDSN Event web service",
         versionProvider=edu.sc.seis.seisFile.client.VersionProvider.class)
public class FDSNEventClient extends AbstractFDSNClient {

    FDSNEventQueryParams queryParams;

    @Option(names= { "--schema"}, description="prints schema")
    public boolean isPrintSchema = false;
    
    @Mixin
    FDSNEventCmdLineQueryParams cmdLine;

    public FDSNEventClient() {
        this.cmdLine = new FDSNEventCmdLineQueryParams();
        this.queryParams = this.cmdLine.queryParams;
    }
    
    @Override
    public Integer call() {
        ParseResult parsedArgs = spec.commandLine().getParseResult();
        if (requiresAtLeastOneArg() && parsedArgs.expandedArgs().size() == 0) {
            throw new ParameterException(spec.commandLine(), "Must use at least one option");
        }
        try {
            cmdLine.validateArguments();
        } catch ( IllegalArgumentException e) {
            if (spec != null ) {
                throw new CommandLine.ParameterException(spec.commandLine(), e.getMessage(), e);
            } else {
                throw e;
            }
        }

        Quakeml quakeml =  null;
        try {
            if (isPrintUrl) {
                System.out.println(queryParams.formURI());
                return 0;
            } else if (isPrintSchema) {
                PrintStream out = System.out;
                if (outputFile != null) {
                    out = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
                }
                Quakeml.printSchema(out);
                out.println();
                out.flush();
                return 0;
            } else {
                FDSNEventQuerier querier = new FDSNEventQuerier(queryParams);
                if (isValidate) {
                    querier.validateQuakeML();
                    System.out.println("Valid");
                } else if (isRaw) {
                    PrintStream out = System.out;
                    if (outputFile != null) {
                        out = new PrintStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
                    }
                    querier.outputRaw(out);
                    out.println();
                    out.flush();
                } else {
                    quakeml = querier.getQuakeML();
                    if (!quakeml.checkSchemaVersion()) {
                        System.out.println();
                        System.out.println("WARNING: XmlSchema of this document does not match this code, results may be incorrect.");
                        System.out.println("XmlSchema (code): " + QuakeMLTagNames.CODE_MAIN_SCHEMA_VERSION);
                        System.out.println("XmlSchema (doc): " + quakeml.getSchemaVersion());
                    }
                    handleResults(quakeml);
                }
            }
        } catch(Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return 1;
        } finally {
            if (quakeml != null) {
                quakeml.close();
            }
        }
        return 0;
    }

    public void handleResults(Quakeml quakeml) throws XMLStreamException, SeisFileException {
        EventIterator eIt = quakeml.getEventParameters().getEvents();
        while (eIt.hasNext()) {
            Event e = eIt.next();
            String oString = NO_ORIGIN;
            String timeString = "";
            if (!e.getOriginList().isEmpty()) {
                Origin o = e.getOriginList().get(0);
                oString = o.getLatitude() + "/" + o.getLongitude() + " ";
                timeString = o.getTime().getValue();
            }
            String magString = NO_MAGNITUDE;
            if (!e.getMagnitudeList().isEmpty()) {
                Magnitude m = e.getMagnitudeList().get(0);
                magString = m.getMag().getValue() + " " + m.getType();
            }
            System.out.println(oString + " " + magString + " " + timeString);
        }
    }
    
    public static final String NO_ORIGIN = "no origin";
    public static final String NO_MAGNITUDE = "no magnitude";

    public FDSNEventQueryParams getQueryParams() {
        return queryParams;
    }

    protected void internalSetBaseURI(URI uri) {
        queryParams.setBaseURL(uri);
    }
    
    public static void main(String... args) { // bootstrap the application
        System.exit(new CommandLine(new FDSNEventClient()).execute(args));
    }
}
