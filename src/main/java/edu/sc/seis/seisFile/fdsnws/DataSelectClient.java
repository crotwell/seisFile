package edu.sc.seis.seisFile.fdsnws;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

import edu.sc.seis.seisFile.BuildVersion;
import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.client.ISOTimeParser;
import edu.sc.seis.seisFile.mseed.DataHeader;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.DataRecordIterator;
import edu.sc.seis.seisFile.mseed.SeedFormatException;

public class DataSelectClient extends AbstractFDSNClient {

    public DataSelectClient(String[] args) throws JSAPException {
        super(args);
    }

    @Override
    protected void addParams() throws JSAPException {
        super.addParams();
        add(ISOTimeParser.createRequiredParam(BEGIN, "The start time", false));
        add(ISOTimeParser.createRequiredParam(END, "The end time", true));
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
        add(new FlaggedOption(OUTPUT,
                              JSAP.STRING_PARSER,
                              null,
                              false,
                              'o',
                              OUTPUT,
                              "Filename for outputing DataRecords"));
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
        FDSNDataSelectQueryParams queryParams;
        try {
            queryParams = configureQuery(result);
            if (getResult().getBoolean(PRINTURL)) {
                System.out.println(queryParams.formURI());
                return;
            } else {
                FDSNDataSelectQuerier querier = new FDSNDataSelectQuerier(queryParams);
                DataRecordIterator it = querier.getDataRecordIterator();
                try {
                    handleResults(it);
                } finally {
                    it.close();
                }
            }
        } catch(Exception e) {
            System.err.println("Error: "+e.getMessage());
            e.printStackTrace();
        }
    }

    public FDSNDataSelectQueryParams configureQuery(JSAPResult result) throws SeisFileException {
        FDSNDataSelectQueryParams queryParams = new FDSNDataSelectQueryParams();
        if (result.contains(BEGIN)) {
            queryParams.setStartTime((Date)result.getObject(BEGIN));
        }
        if (result.contains(END)) {
            queryParams.setEndTime((Date)result.getObject(END));
        }
        if (result.contains(FDSNStationQueryParams.NETWORK)) {
            String[] vals = result.getStringArray(FDSNStationQueryParams.NETWORK);
            for (int i = 0; i < vals.length; i++) {
                queryParams.appendToNetwork(vals[i]);
            }
        }
        if (result.contains(FDSNStationQueryParams.STATION)) {
            String[] vals = result.getStringArray(FDSNStationQueryParams.STATION);
            for (int i = 0; i < vals.length; i++) {
                queryParams.appendToStation(vals[i]);
            }
        }
        if (result.contains(FDSNStationQueryParams.LOCATION)) {
            String[] vals = result.getStringArray(FDSNStationQueryParams.LOCATION);
            for (int i = 0; i < vals.length; i++) {
                queryParams.appendToLocation(vals[i]);
            }
        }
        if (result.contains(FDSNStationQueryParams.CHANNEL)) {
            String[] vals = result.getStringArray(FDSNStationQueryParams.CHANNEL);
            for (int i = 0; i < vals.length; i++) {
                queryParams.appendToChannel(vals[i]);
            }
        }
        if (result.contains(BASEURL)) {
            try {
                queryParams.setBaseURI(new URI(result.getString(BASEURL)));
            } catch(URISyntaxException e) {
                throw new SeisFileException("unable to parse base URI: " + result.getString(BASEURL), e);
            }
        }
        return queryParams;
    }

    public void handleResults(DataRecordIterator drIter) throws IOException, SeedFormatException {
        if (!drIter.hasNext()) {
            System.out.println("No Data (empty iterator)");
        }
        DataOutputStream out = null;
        try {
            if (result.contains(OUTPUT)) {
                out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File(result.getString(OUTPUT)))));
            }
            while (drIter.hasNext()) {
                DataRecord dr = drIter.next();
                if (out != null) {
                    dr.write(out);
                } else {
                    DataHeader dh = dr.getHeader();
                    System.out.println(dh);
                }
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private static final String OUTPUT = "output";

    /**
     * @param args
     * @throws JSAPException
     */
    public static void main(String[] args) throws JSAPException {
        new DataSelectClient(args).run();
    }
}
