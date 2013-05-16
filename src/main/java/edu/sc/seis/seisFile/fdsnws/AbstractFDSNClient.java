package edu.sc.seis.seisFile.fdsnws;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.Switch;

import edu.sc.seis.seisFile.client.AbstractClient;

public class AbstractFDSNClient extends AbstractClient {

    public AbstractFDSNClient(String[] args) throws JSAPException {
        super(args);
    }

    protected void addParams() throws JSAPException {
        super.addParams();
        add(new Switch(PRINTURL, JSAP.NO_SHORTFLAG, PRINTURL, "Construct and print URL and exit"));
        add(new Switch(RAW, JSAP.NO_SHORTFLAG, RAW, "Output the raw data to stdout"));
        add(new FlaggedOption(BASEURL, JSAP.STRING_PARSER, null, false, JSAP.NO_SHORTFLAG, BASEURL, "Base URL for queries, ie everything before the '?'"));
        add(new FlaggedOption(HOST, JSAP.STRING_PARSER, null, false, JSAP.NO_SHORTFLAG, HOST, "Host for queries, such as service.iris.edu or www.ncedc.org."));
    }

    public static final String HOST = "host";

    public static final String BASEURL = "baseurl";

    public static final String PRINTURL = "printurl";
    
    public static final String RAW = "raw";
    
    public static final String VALIDATE = "validate";

    public static final String BEGIN = "begin";

    public static final String END = "end";

}
