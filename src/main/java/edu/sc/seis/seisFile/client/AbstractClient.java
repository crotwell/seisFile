package edu.sc.seis.seisFile.client;

import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.StringParser;
import com.martiansoftware.jsap.Switch;

import edu.sc.seis.seedCodec.BuildVersion;

public class AbstractClient {

    protected boolean requiresAtLeastOneArg() {
        return true;
    }

    protected FlaggedOption createListOption(String id, char shortFlag, String longFlag, String help) {
        return createListOption(id, shortFlag, longFlag, help, null, JSAP.STRING_PARSER);
    }

    protected FlaggedOption createListOption(String id, char shortFlag, String longFlag, String help, String defaultArg) {
        return createListOption(id, shortFlag, longFlag, help, defaultArg, JSAP.STRING_PARSER);
    }

    protected FlaggedOption createListOption(String id,
                                             char shortFlag,
                                             String longFlag,
                                             String help,
                                             String defaultArg,
                                             StringParser parser) {
        FlaggedOption listOption = new FlaggedOption(id, parser, defaultArg, false, shortFlag, longFlag, help);
        listOption.setList(true);
        listOption.setListSeparator(',');
        return listOption;
    }

    protected void addParams() throws JSAPException {
        add(new Switch("version", 'v', "version", "Print version and exit"));
        add(new Switch("help", 'h', "help", "Print this message."));
        add(new FlaggedOption("props", JSAP.STRING_PARSER, null, false, 'p', "props", "Use an additional props file"));
    }

    protected void add(Parameter param) throws JSAPException {
        jsap.registerParameter(param);
        params.add(param);
    }

    protected boolean isSpecified(Parameter p) {
        return result.contains(p.getID());
    }

    public String[] getArgs() {
        return args;
    }

    public boolean shouldPrintHelp() {
        return result.getBoolean(HELP);
    }

    public boolean shouldPrintVersion() {
        return result.getBoolean(VERSION);
    }
    
    public String getHelp() {
        String out = "";
        for (Parameter param : getParams()) {
            out += param.getHelp()+"\n";
        }
        return out;
    }

    public boolean isSuccess() {
        return result.success();
    }

    public List<Parameter> getParams() {
        return params;
    }

    public JSAPResult getResult() {
        return result;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public static final String HELP = "help";
    
    public static final String VERSION  = "version";
    
    protected List<Parameter> params = new ArrayList<Parameter>();

    protected JSAPResult result;

    protected JSAP jsap = new JSAP();

    protected String[] args;

    protected String commandName;
    
    protected String userAgent = DEFAULT_USER_AGENT;
    
    public static final String DEFAULT_USER_AGENT = "SeisFile-"+BuildVersion.getVersion();

    public AbstractClient(String[] args) throws JSAPException {
        this.args = args;
        addParams();
        String[] segs = getClass().getName().split("\\.");
        commandName = segs[segs.length - 1];
        result = jsap.parse(args);
        if (requiresAtLeastOneArg() && args.length == 0) {
            result.addException("Must use at least one option", new RuntimeException("Must use at least one option"));
        }
    }
}
