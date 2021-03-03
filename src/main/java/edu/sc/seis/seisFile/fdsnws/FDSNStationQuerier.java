package edu.sc.seis.seisFile.fdsnws;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.stationxml.FDSNStationXML;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;

public class FDSNStationQuerier extends AbstractFDSNQuerier {

    public FDSNStationQuerier(FDSNStationQueryParams queryParams) {
        this.queryParams = queryParams;
    }

    public URL getSchemaURL() {
        return FDSNStationXML.findInternalSchema();
    }

    public void validateFDSNStationXML() throws SeisFileException, URISyntaxException {
        validateFDSNStationXML(FDSNStationXML.findInternalSchema());
    }

    public void validateFDSNStationXML(URL schemaURL) throws SeisFileException, URISyntaxException {
        try {
            connect();
            if (!isError()) {
                if (!isEmpty()) {
                    XMLInputFactory factory = XMLInputFactory.newInstance();
                    XMLStreamReader reader = factory.createXMLStreamReader(getConnectionUri().toString(),
                                                                           getInputStream());
                    validate(reader, schemaURL);
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

    public void outputRaw(OutputStream out) throws IOException, URISyntaxException, FDSNWSException {
        connect();
        outputRaw(getInputStream(), out);
    }

    public FDSNStationXML getFDSNStationXML() throws FDSNWSException {
        try {
            connect();
            if (!isError()) {
                if (!isEmpty()) {
                    try {
                        FDSNStationXML stationxml = new FDSNStationXML(getReader());
                        if (!stationxml.checkSchemaVersion()) {
                            logger.warn("XmlSchema of this document does not match this code, results may be incorrect. "
                                    + " XmlSchema (code): "+ StationXMLTagNames.CURRENT_SCHEMA_VERSION
                                    + " XmlSchema (doc): " + stationxml.getSchemaVersion());
                        }
                        stationxml.setQuerier(this); // GC closing stream if querier not held
                        stationxml.setResponse(response); // so can be closed when done
                        return stationxml;
                    } catch(XMLStreamException e) {
                        handleXmlStreamException(e);
                        // can't get here as handleXmlStreamException throw FDSNWSException
                        throw new RuntimeException("Should not happen");          }
                } else {
                    // return iterator with nothing in it
                    return FDSNStationXML.createEmpty();
                }
            } else {
                throw new FDSNWSException("Error: " + getErrorMessage(), getConnectionUri(), responseCode);
            }
        } catch(URISyntaxException e) {
            throw new FDSNWSException("Error with URL syntax", e);
        } catch(SeisFileException e) {
            if (e instanceof FDSNWSException) {
                ((FDSNWSException)e).setTargetURI(getConnectionUri());
                throw (FDSNWSException)e;
            } else {
                throw new FDSNWSException(e.getMessage(), e, getConnectionUri());
            }
        }
    }

    @Override
    public URI formURI() throws URISyntaxException {
        return queryParams.formURI();
    }

    FDSNStationQueryParams queryParams;

    private static Logger logger = LoggerFactory.getLogger(FDSNStationQuerier.class);

}
