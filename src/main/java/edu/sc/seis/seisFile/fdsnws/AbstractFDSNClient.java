package edu.sc.seis.seisFile.fdsnws;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLConnection;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;

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
        add(new FlaggedOption(BASEURL, JSAP.STRING_PARSER, null, false, JSAP.NO_SHORTFLAG, BASEURL, "Base URL for queries, ie everything before the '?'"));
    }

    public static final String BASEURL = "baseurl";
    
    public static final String PRINTURL = "printurl";

    public static final String BEGIN = "begin";

    public static final String END = "end";

}
