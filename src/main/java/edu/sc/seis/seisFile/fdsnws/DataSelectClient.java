package edu.sc.seis.seisFile.fdsnws;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;


import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.client.ISOTimeParser;
import edu.sc.seis.seisFile.mseed.DataHeader;
import edu.sc.seis.seisFile.mseed.DataRecord;
import edu.sc.seis.seisFile.mseed.DataRecordIterator;
import edu.sc.seis.seisFile.mseed.SeedFormatException;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.ParseResult;

public class DataSelectClient extends AbstractFDSNClient {


    @Override
    public Integer call() {
        ParseResult parsedArgs = spec.commandLine().getParseResult();
        if (requiresAtLeastOneArg() && parsedArgs.expandedArgs().size() == 0) {
            throw new ParameterException(spec.commandLine(), "Must use at least one option");
        }
        try {
            if (isPrintUrl) {
                System.out.println(queryParams.formURI());
                return 0;
            } else {
                FDSNDataSelectQuerier querier = new FDSNDataSelectQuerier(queryParams);
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
    
    @Option(names={"-o","--output"}, description="Filename for outputing DataRecords")
    public File outputFile;
    
    @ArgGroup(exclusive = false)
    UserPassword userPassword;

    static class UserPassword {
        @Option(names = {"--user"}, required = true, description="username for restricted data access")
        String user;
        @Option(names = {"--password"}, required = true, description="password for restricted data access")
        String password;
    }
    

    private static final String OUTPUT = "output";

    private static final String USER = "user";

    private static final String PASSWORD = "password";

    @Mixin
    FDSNDataSelectQueryParams queryParams = new FDSNDataSelectQueryParams();
    
    public static void main(String... args) { // bootstrap the application
        System.exit(new CommandLine(new DataSelectClient()).execute(args));
    }
}
