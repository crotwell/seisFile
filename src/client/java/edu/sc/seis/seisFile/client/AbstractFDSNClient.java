package edu.sc.seis.seisFile.client;


import java.io.File;
import java.net.URI;

import picocli.CommandLine.Option;

public abstract class AbstractFDSNClient extends AbstractClient {

    public static final String HOST = "host";

    public static final String BASEURL = "baseurl";

    public static final String PRINTURL = "printurl";
    
    public static final String RAW = "raw";
    
    public static final String VALIDATE = "validate";

    public static final String BEGIN = "begin";

    public static final String END = "end";


    @Option(names="--"+AbstractFDSNClient.BASEURL, description="Base URL for queries, ie everything before the '/<service>/<version>/<query>?'")
    public void setBaseURL(URI uri) {
        internalSetBaseURI(uri);
    }
    
    protected abstract void internalSetBaseURI(URI uri);

    @Option(names = "--nodata", description = "nodata http return code", defaultValue="204")
    public int nodata = 204;


    @Option(names="--"+PRINTURL, description="Construct and print URL and exit")
    public boolean isPrintUrl = false;
    
    @Option(names="--"+VALIDATE, description="Validate XML against schema")
    public boolean isValidate = false;
    
    @Option(names="--"+RAW, description="Output the raw data to stdout")
    public boolean isRaw = false;

    
    @Option(names={"-o","--output"}, description="File for outputing result")
    public File outputFile;
}
