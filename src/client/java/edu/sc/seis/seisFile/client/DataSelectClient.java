package edu.sc.seis.seisFile.client;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import edu.sc.seis.seisFile.fdsnws.FDSNDataSelectQuerier;
import edu.sc.seis.seisFile.fdsnws.FDSNDataSelectQueryParams;
import edu.sc.seis.seisFile.fdsnws.FDSNEventQueryParams;
import edu.sc.seis.seisFile.mseed.DataHeader;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.DataRecordIterator;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;

@Command(name="fdsndataselect",
         description="Example client to query a remote FDSN DataSelect web service via GET or POST",
         footer= {
                 "",
                 "Example:",
                 "",
                 "fdsndataselect -n CO -s JSC -c HHZ -o jsc.mseed -b 2021-02-28T12:45:00 -e 2021-02-28T12:47:00 -v"
         },
         versionProvider=edu.sc.seis.seisFile.client.VersionProvider.class)
public class DataSelectClient extends AbstractFDSNClient {

    FDSNDataSelectQueryParams queryParams;

    @Mixin
    FDSNDataSelectCmdLineQueryParams cmdLine;

    public DataSelectClient() {
        this.cmdLine = new FDSNDataSelectCmdLineQueryParams();
        this.queryParams = this.cmdLine.queryParams;
    }

    @Override
    public Integer call() {
        ParseResult parsedArgs = spec.commandLine().getParseResult();
        if (requiresAtLeastOneArg() && parsedArgs.expandedArgs().size() == 0) {
            throw new ParameterException(spec.commandLine(), "Must use at least one option");
        }
        try {
            
            FDSNDataSelectQuerier querier;
            if (cmdLine.doPost) {
                querier = new FDSNDataSelectQuerier(queryParams, queryParams.createChannelTimeWindow());

            } else {
                querier = new FDSNDataSelectQuerier(queryParams);
            }
            if (isPrintUrl) {
                if (cmdLine.doPost) {
                    System.out.println(querier.formURIForPost());
                    System.out.println("POST:");
                    System.out.println(queryParams.formPostString());
                } else {
                    System.out.println(querier.formURI());
                }
                return 0;
            } else {
                if (userPassword != null) {
                    querier.enableRestrictedData(userPassword.user, userPassword.password);
                }
                if (isValidate) {
                    // dumb way to validate, but...
                    DataRecordIterator drIter = querier.getDataRecordIterator();
                    while (drIter.hasNext()) {
                        DataRecord dr = drIter.next();
                    }
                    System.out.println("Valid");
                } else if (isRaw) {
                    querier.outputRaw(System.out);
                } else {
                    DataRecordIterator it = querier.getDataRecordIterator();
                    try {
                        handleResults(it);
                    } finally {
                        it.close();
                    }
                }
            }
        } catch(Exception e) {
            System.err.println("Error: "+e.getMessage());
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    public void handleResults(DataRecordIterator drIter) throws IOException, SeedFormatException {
        if (!drIter.hasNext()) {
            System.out.println("No Data");
        }
        DataOutputStream out = null;
        try {
            if (outputFile != null) {
                out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)));
            }
            while (drIter.hasNext()) {
                DataRecord dr = drIter.next();
                if (verbose || out == null ) {
                    DataHeader dh = dr.getHeader();
                    System.out.println(dh);
                } 
                if (out != null ) {
                    dr.write(out);
                }
                
            }
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    
    @ArgGroup(exclusive = false)
    UserPassword userPassword;

    static class UserPassword {
        @Option(names = {"--user"}, required = true, description="username for restricted data access")
        String user;
        @Option(names = {"--password"}, required = true, description="password for restricted data access")
        String password;
    }
    
    public FDSNDataSelectQueryParams getQueryParams() {
        return queryParams;
    }

    protected void internalSetBaseURI(URI uri) {
        queryParams.setBaseURL(uri);
    }

    private static final String OUTPUT = "output";

    private static final String USER = "user";

    private static final String PASSWORD = "password";
    
    public static void main(String... args) { // bootstrap the application
        System.exit(new CommandLine(new DataSelectClient()).execute(args));
    }
}
