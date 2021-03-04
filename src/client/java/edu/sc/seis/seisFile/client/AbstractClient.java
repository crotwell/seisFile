package edu.sc.seis.seisFile.client;

import java.util.concurrent.Callable;

import org.apache.log4j.BasicConfigurator;

import edu.sc.seis.seisFile.fdsnws.AbstractFDSNQuerier;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;

public abstract class AbstractClient implements Callable<Integer> {

    @Option(names = { "--help" }, usageHelp = true, description = "display a help message")
    protected boolean helpRequested = false;

    @Option(names = { "-V", "--version" }, versionHelp = true, description = "Print version and exit")
    protected boolean versionRequested = false;

    @Option(names = { "-v", "--verbose" }, description = "Verbose")
    protected boolean verbose = false;

    @Spec
    protected CommandSpec spec;
    
    protected boolean requiresAtLeastOneArg() {
        return true;
    }
    
    public AbstractClient() {
        BasicConfigurator.configure();
        org.apache.log4j.Logger.getRootLogger().setLevel(org.apache.log4j.Level.INFO);
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
    
    protected String userAgent = AbstractFDSNQuerier.DEFAULT_USER_AGENT;
    

}
