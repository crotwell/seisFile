package edu.sc.seis.seisFile.client;

import java.util.concurrent.Callable;

import edu.sc.seis.seisFile.BuildVersion;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import picocli.CommandLine.Model.CommandSpec;

public abstract class AbstractClient implements Callable<Integer> {

    @Option(names = { "--help" }, usageHelp = true, description = "display a help message")
    boolean helpRequested = false;

    @Option(names = { "-V", "--version" }, versionHelp = true, description = "Print version and exit")
    boolean versionRequested = false;
    

    @Spec
    protected CommandSpec spec;
    
    protected boolean requiresAtLeastOneArg() {
        return true;
    }
    
    public AbstractClient() {
        
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
    

    protected String[] args;

    protected String commandName;
    
    protected String userAgent = DEFAULT_USER_AGENT;
    
    public static final String DEFAULT_USER_AGENT = "SeisFile-"+BuildVersion.getVersion();

}
