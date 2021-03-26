package edu.sc.seis.seisFile.client;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name="seisfile", 
         description="seisfile client example codes",
         versionProvider=edu.sc.seis.seisFile.client.VersionProvider.class,
         subcommands = {
                 FDSNDataSelectClient.class,
                 FDSNEventClient.class,
                 FDSNStationClient.class,
                 IRISVirtualNetClient.class,
                 MSeedListHeader.class,
                 SacListHeader.class,
                 EarthwormExportServer.class,
                 EarthwormImportClient.class,
                 DataLinkClient.class,
                 SeedLinkClient.class
})
public class SeisFile {

    
    public static void main(String... args) { // bootstrap the application
        System.exit(new CommandLine(new SeisFile()).execute(args));
    }

}
