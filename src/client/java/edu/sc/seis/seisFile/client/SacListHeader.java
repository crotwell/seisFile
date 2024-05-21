package edu.sc.seis.seisFile.client;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.sc.seis.seisFile.sac.SacHeader;
import edu.sc.seis.seisFile.sac.SacIncrementalloader;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ParseResult;

@Command(name="saclh", 
         description="print some or all header values from a SAC file",
         versionProvider=edu.sc.seis.seisFile.client.VersionProvider.class)
public class SacListHeader extends AbstractClient {
    
    @Option(names={"-h","--headers"}, description="Headers to print", split = ",")
    public List<String> headerList = new ArrayList<>();

    @Parameters( description="SAC files")
    public List<File> sacfileList = new ArrayList<>();
    

    @Override
    public Integer call() throws IOException, SecurityException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        ParseResult parsedArgs = spec.commandLine().getParseResult();
        if (requiresAtLeastOneArg() && parsedArgs.expandedArgs().isEmpty()) {
            throw new ParameterException(spec.commandLine(), "Must use at least one option");
        }
        // check to see if headers are correct

        Class<SacHeader> headerClass = SacHeader.class;
        Map<String, Method> fieldMap = new HashMap<>();
        for (String h : headerList) {
            try {
                String getterName = "get"+h.substring(0,1).toUpperCase()+h.substring(1);
                Method fieldGetter = headerClass.getMethod(getterName);
                fieldMap.put(h, fieldGetter);
            } catch(NoSuchMethodException e) {
                System.err.println("No header named '"+h+"', cowardly quitting...");
                return 1;
            }
        }
        PrintWriter out = new PrintWriter(System.out, true);
        for (File sacFile : sacfileList) {
            if (sacFile.exists() && sacFile.isFile()) {
                SacIncrementalloader loader = new SacIncrementalloader(sacFile);
                SacHeader header = loader.getHeader();
                String filename = sacFile.getName();
                boolean byteOrder = header.getByteOrder();
                String byteOrderStr = byteOrder ? "big endian" : "little endian";
                if (headerList.isEmpty()) {
                    out.println();
                    out.println(filename+" ("+byteOrderStr+")");
                    String dashLine = "";
                    for (int j = 0; j < filename.length(); j++) {
                        dashLine += "-";
                    }
                    out.println(dashLine);
                    out.println();
                    header.printHeader(out);
                } else {
                    String headerString = filename+":";
                    for (String h : headerList) {
                        Method fieldGetter = fieldMap.get(h);
                        if (fieldGetter != null) {
                            if (fieldGetter.getReturnType() == Float.TYPE) {
                                headerString+= SacHeader.format(h, (float)fieldGetter.invoke(header));
                            } else if (fieldGetter.getReturnType() == Integer.TYPE) {
                                headerString+= SacHeader.format(h, (int)fieldGetter.invoke(header));
                            } else {
                                headerString+= SacHeader.format(h, (String)fieldGetter.invoke(header), SacHeader.DEFAULT_LABEL_WIDTH, SacHeader.DEFAULT_VALUE_WIDTH);
                            }
                        }
                    }
                    out.println(headerString);
                }
            } else {
                out.println("Cannot load, "+sacFile.getPath()+" exists="+sacFile.exists()+" isFile="+sacFile.isFile());
            }
        }
        return 0;
    }

    public static void main(String... args) { // bootstrap the application
        System.exit(new CommandLine(new SacListHeader()).execute(args));
    }
}
