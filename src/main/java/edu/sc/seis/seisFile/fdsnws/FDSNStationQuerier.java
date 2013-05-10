package edu.sc.seis.seisFile.fdsnws;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.quakeml.QuakeMLTagNames;
import edu.sc.seis.seisFile.fdsnws.quakeml.Quakeml;
import edu.sc.seis.seisFile.fdsnws.stationxml.FDSNStationXML;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;

public class FDSNStationQuerier extends AbstractFDSNQuerier {

    public FDSNStationQuerier(FDSNStationQueryParams queryParams) {
        this.queryParams = queryParams;
    }
    
    public void validateFDSNStationXML() throws SeisFileException, URISyntaxException {
        try {
            connect(queryParams.formURI());
            if (!isError()) {
                if (!isEmpty()) {
                    XMLInputFactory factory = XMLInputFactory.newInstance();
                    XMLStreamReader reader = factory.createXMLStreamReader(getConnectionUri().toString(),
                                                                           getInputStream());
                    validate(reader, FDSNStationXML.loadSchema());
                }
            }
        } catch(SAXException e) {
            throw new SeisFileException("Unable to validate xml", e);
        } catch(XMLStreamException e) {
            throw new SeisFileException("Unable to read xml", e);
        } catch(IOException e) {
            throw new SeisFileException("IOException trying to validate", e);
        }
    }

    public FDSNStationXML getFDSNStationXML() throws SeisFileException {
        try {
            connect(queryParams.formURI());
            if (!isError()) {
                if (!isEmpty()) {
                    try {
                        FDSNStationXML stationxml = new FDSNStationXML(getReader());
                        if (!stationxml.checkSchemaVersion()) {
                            logger.warn("XmlSchema of this document does not match this code, results may be incorrect. "
                                    + " XmlSchema (code): "+ StationXMLTagNames.CURRENT_SCHEMA_VERSION
                                    + " XmlSchema (doc): " + stationxml.getSchemaVersion());
                        }
                        return stationxml;
                    } catch(XMLStreamException e) {
                        throw new SeisFileException("Unable to load xml", e);
                    }
                } else {
                    // return iterator with nothing in it
                    return FDSNStationXML.createEmpty();
                }
            } else {
                throw new SeisFileException("Error: " + getErrorMessage());
            }
        } catch(URISyntaxException e) {
            throw new SeisFileException("Error with URL syntax", e);
        } catch(MalformedURLException e) {
            throw new SeisFileException("Error forming URL", e);
        } catch(IOException e) {
            throw new SeisFileException("Error with Connection", e);
        }
    }

    FDSNStationQueryParams queryParams;

    private static Logger logger = LoggerFactory.getLogger(FDSNStationQuerier.class);
}
