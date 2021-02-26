package edu.sc.seis.seisFile.client;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import edu.sc.seis.seisFile.sac.SacHeader;
import edu.sc.seis.seisFile.sac.SacIncrementalloader;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParseResult;

@Command(name="saclh", versionProvider=edu.sc.seis.seisFile.client.VersionProvider.class)
public class SacListHeader extends AbstractClient {
    
    @Option(names={"-h","--headers"}, description="Headers to print")
    public List<String> headerList = new ArrayList<String>();

    @Parameters( description="SAC files")
    public List<File> sacfileList = new ArrayList<File>();
    

    @Override
    public Integer call() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        ParseResult parsedArgs = spec.commandLine().getParseResult();
        if (requiresAtLeastOneArg() && parsedArgs.expandedArgs().size() == 0) {
            throw new ParameterException(spec.commandLine(), "Must use at least one option");
        }
        PrintWriter out = new PrintWriter(System.out, true);
        for (File sacFile : sacfileList) {
            if (sacFile.exists() && sacFile.isFile()) {
                SacIncrementalloader loader = new SacIncrementalloader(sacFile);
                SacHeader header = loader.getHeader();
                String filename = sacFile.getName();
                if (headerList.size() == 0) {
                    out.println();
                    out.println(filename);
                    String dashLine = "";
                    for (int j = 0; j < filename.length(); j++) {
                        dashLine += "-";
                    }
                    out.println(dashLine);
                    out.println();
                    header.printHeader(out);
                } else {
                    String headerString = filename+":";
                    Class<SacHeader> headerClass = SacHeader.class;
                    for (String h : headerList) {
                        Field field = headerClass.getField(h);
                        if (field != null) {
                            headerString+= " "+field.get(header);
                        }
                    }
                    out.println(headerString);
                }
            } else {
                out.println("Cannot load, exists="+sacFile.exists()+" isFile="+sacFile.isFile());
            }
        }
        return 0;
    }

    public static void main(String... args) { // bootstrap the application
        System.exit(new CommandLine(new SacListHeader()).execute(args));
    }
}
