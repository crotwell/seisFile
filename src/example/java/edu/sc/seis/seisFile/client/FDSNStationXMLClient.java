package edu.sc.seis.seisFile.client;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.stationxml.FDSNStationXML;

public class FDSNStationXMLClient {

    public FDSNStationXMLClient() {
        // TODO Auto-generated constructor stub
    }


    public static void main(String[] args) throws XMLStreamException, IOException, SeisFileException {
        final FDSNStationXML stationXml = FDSNStationXML.loadStationXML(args[0]);
        StationClient sc = new StationClient() {
            public Integer call() {
                try {
                    handleResults(stationXml);
                    return 0;
                } catch (XMLStreamException | SeisFileException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return 1;
                }
            }
        };
        sc.call();
    }

}
