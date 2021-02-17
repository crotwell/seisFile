package edu.sc.seis.seisFile.fdsnws;

import javax.xml.stream.XMLStreamException;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.quakeml.Event;
import edu.sc.seis.seisFile.fdsnws.quakeml.EventIterator;
import edu.sc.seis.seisFile.fdsnws.quakeml.Magnitude;
import edu.sc.seis.seisFile.fdsnws.quakeml.Origin;
import edu.sc.seis.seisFile.fdsnws.quakeml.QuakeMLTagNames;
import edu.sc.seis.seisFile.fdsnws.quakeml.Quakeml;
import picocli.CommandLine;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;

public class EventClient extends AbstractFDSNClient {

    private static final String DEPTH = "depth";

    private static final String CONTRIBUTORS = "contributors";

    private static final String CATALOGS = "catalogs";

    private static final String MAGNITUDE = "magnitude";

    private static final String TYPES = "types";

    private static final String INCLUDEALLORIGINS = "includeallorigins";

    private static final String INCLUDEALLMAGNITUDES = "includeallmagnitudes";

    private static final String INCLUDEARRIVALS = "includearrivals";


    @Override
    public Integer call() {
        ParseResult parsedArgs = spec.commandLine().getParseResult();
        if (requiresAtLeastOneArg() && parsedArgs.expandedArgs().size() == 0) {
            throw new ParameterException(spec.commandLine(), "Must use at least one option");
        }
        Quakeml quakeml =  null;
        try {
            if (isPrintUrl) {
                System.out.println(queryParams.formURI());
                return 0;
            } else {
                FDSNEventQuerier querier = new FDSNEventQuerier(queryParams);
                if (isValidate) {
                    querier.validateQuakeML();
                    System.out.println("Valid");
                } else if (isRaw) {
                    querier.outputRaw(System.out);
                } else {
                    quakeml = querier.getQuakeML();
                    if (!quakeml.checkSchemaVersion()) {
                        System.out.println("");
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
            if (e.getOriginList().size() > 0) {
                Origin o = e.getOriginList().get(0);
                oString = o.getLatitude() + "/" + o.getLongitude() + " ";
                timeString = o.getTime().getValue();
            }
            String magString = NO_MAGNITUDE;
            if (e.getMagnitudeList().size() > 0) {
                Magnitude m = e.getMagnitudeList().get(0);
                magString = m.getMag().getValue() + " " + m.getType();
            }
            System.out.println(oString + " " + magString + " " + timeString);
        }
    }

    public static final String NO_ORIGIN = "no origin";
    public static final String NO_MAGNITUDE = "no magnitude";

    @Mixin
    FDSNEventQueryParams queryParams = new FDSNEventQueryParams();

    public static void main(String... args) { // bootstrap the application
        System.exit(new CommandLine(new EventClient()).execute(args));
    }
}
