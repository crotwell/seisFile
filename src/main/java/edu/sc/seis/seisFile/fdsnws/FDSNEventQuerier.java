package edu.sc.seis.seisFile.fdsnws;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
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

public class FDSNEventQuerier extends AbstractFDSNQuerier {

    public FDSNEventQuerier(FDSNEventQueryParams queryParams) {
        this.queryParams = queryParams;
    }

    public Quakeml getQuakeML() throws FDSNWSException {
        URI uri = null;
        try {
            uri = queryParams.formURI();
            connect(uri);
            if (!isError()) {
                if (!isEmpty()) {
                    try {
                        Quakeml quakeml = new Quakeml(getReader());
                        if (!quakeml.checkSchemaVersion()) {
                            logger.warn("XmlSchema of this document does not match this code, results may be incorrect. "
                                    + "XmlSchema (code): " + QuakeMLTagNames.CODE_MAIN_SCHEMA_VERSION
                                    + "XmlSchema (doc): " + quakeml.getSchemaVersion());
                        }
                        return quakeml;
                    } catch(XMLStreamException e) {
                        throw new SeisFileException("Unable to load xml", e);
                    }
                } else {
                    // return iterator with nothing in it
                    return Quakeml.createEmptyQuakeML();
                }
            } else {
                throw new SeisFileException("Error: " + getErrorMessage());
            }
        } catch(URISyntaxException e) {
            throw new FDSNWSException("Error with URL syntax", e);
        } catch(SeisFileException e) {
            if (e instanceof FDSNWSException) {
                ((FDSNWSException)e).setTargetURI(uri);
                throw (FDSNWSException)e;
            } else {
                throw new FDSNWSException(e.getMessage(), e, uri);
            }
        }
    }

    FDSNEventQueryParams queryParams;

    private static Logger logger = LoggerFactory.getLogger(FDSNEventQuerier.class);

    public void validateQuakeML() throws SeisFileException, URISyntaxException {
        URI uri = queryParams.formURI();
        try {
            connect(uri);
            if (!isError()) {
                if (!isEmpty()) {
                    XMLInputFactory factory = XMLInputFactory.newInstance();
                    XMLStreamReader reader = factory.createXMLStreamReader(getConnectionUri().toString(),
                                                                           getInputStream());
                    validateQuakeML(reader);
                }
            }
        } catch(SAXException e) {
            throw new FDSNWSException("Unable to validate xml", e, uri);
        } catch(XMLStreamException e) {
            throw new FDSNWSException("Unable to read xml", e, uri);
        } catch(IOException e) {
            throw new FDSNWSException("IOException trying to validate", e, uri);
        }
    }
    
    public static void validateQuakeML(XMLStreamReader reader) throws SAXException, IOException {
        validate(reader, Quakeml.loadSchema());
    }

    public void outputRaw(OutputStream out) throws MalformedURLException, IOException, URISyntaxException, FDSNWSException {
        connect(queryParams.formURI());
        outputRaw(getInputStream(), out);
    }
    
}
