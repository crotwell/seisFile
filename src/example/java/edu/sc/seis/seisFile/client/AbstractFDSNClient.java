package edu.sc.seis.seisFile.fdsnws;


import edu.sc.seis.seisFile.client.AbstractClient;
import picocli.CommandLine.Option;

public abstract class AbstractFDSNClient extends AbstractClient {

    public static final String HOST = "host";

    public static final String BASEURL = "baseurl";

    public static final String PRINTURL = "printurl";
    
    public static final String RAW = "raw";
    
    public static final String VALIDATE = "validate";

    public static final String BEGIN = "begin";

    public static final String END = "end";


    @Option(names="--"+PRINTURL, description="Construct and print URL and exit")
    public boolean isPrintUrl = false;
    
    @Option(names="--"+VALIDATE, description="Validate XML against schema")
    public boolean isValidate = false;
    
    @Option(names="--"+RAW, description="Output the raw data to stdout")
    public boolean isRaw = false;
    
}
