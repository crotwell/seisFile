package edu.sc.seis.seisFile.winston;

import java.util.Iterator;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

import edu.sc.seis.seisFile.BuildVersion;
import edu.sc.seis.seisFile.client.AbstractClient;
import edu.sc.seis.seisFile.client.ISOTimeParser;
import edu.sc.seis.seisFile.fdsnws.FDSNStationQueryParams;


public class WinstonRTPlayback extends AbstractClient {

    public WinstonRTPlayback(String[] args) throws JSAPException {
        super(args);
    }

    public void run() {
        JSAPResult result = getResult();
        if (shouldPrintHelp()) {
            System.out.println(jsap.getHelp());
            return;
        }
        if (shouldPrintVersion()) {
            System.out.println(BuildVersion.getVersion());
            return;
        }
        if (!isSuccess()) {
            for (Iterator errs = result.getErrorMessageIterator(); errs.hasNext();) {
                System.err.println("Error: " + errs.next());
            }
            System.err.println();
            System.err.println("Usage: java " + this.getClass().getName());
            System.err.println("                " + jsap.getUsage());
            System.err.println();
            System.err.println(jsap.getHelp());
            return;
        }
        
    }
    
    @Override
    protected void addParams() throws JSAPException {
        super.addParams();

        add(ISOTimeParser.createParam(BEGIN, "The earliest time for acceptance", false));
        add(ISOTimeParser.createParam(END, "The latest time for acceptance", true));
        add(createListOption(FDSNStationQueryParams.NETWORK,
                             'n',
                             FDSNStationQueryParams.NETWORK,
                             "A comma separated list of networks to search"));
        add(createListOption(FDSNStationQueryParams.STATION,
                             's',
                             FDSNStationQueryParams.STATION,
                             "A comma separated list of stations to search"));
        add(createListOption(FDSNStationQueryParams.LOCATION,
                             'l',
                             FDSNStationQueryParams.LOCATION,
                             "A comma separated list of locations to search"));
        add(createListOption(FDSNStationQueryParams.CHANNEL,
                             'c',
                             FDSNStationQueryParams.CHANNEL,
                             "A comma separated list of channels to search"));
    }


    /**
     * @param args
     * @throws JSAPException 
     */
    public static void main(String[] args) throws JSAPException {
        WinstonRTPlayback rtp = new WinstonRTPlayback(args);
        
    }
    

    public static final String BEGIN = "begin";

    public static final String END = "end";
}
