package edu.sc.seis.seisFile.client;

import edu.sc.seis.seisFile.BuildVersion;
import picocli.CommandLine.IVersionProvider;

public class VersionProvider implements IVersionProvider {

    public VersionProvider() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public String[] getVersion() throws Exception {
        return new String[] { BuildVersion.getDetailedVersion() };
    }

}
