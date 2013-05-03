package edu.sc.seis.seisFile.fdsnws;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import javax.xml.stream.XMLStreamException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.quakeml.EventIterator;
import edu.sc.seis.seisFile.fdsnws.quakeml.QuakeMLTagNames;
import edu.sc.seis.seisFile.fdsnws.quakeml.Quakeml;
import edu.sc.seis.seisFile.mseed.DataRecordIterator;

public class FDSNEventQuerier extends AbstractFDSNQuerier {

    public FDSNEventQuerier(FDSNEventQueryParams queryParams) {
        this.queryParams = queryParams;
    }

    public Quakeml getQuakeML() throws SeisFileException {
        try {
            connect(queryParams.formURI());
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
            throw new SeisFileException("Error with URL syntax", e);
        } catch(MalformedURLException e) {
            throw new SeisFileException("Error forming URL", e);
        } catch(IOException e) {
            throw new SeisFileException("Error with Connection", e);
        }
    }

    FDSNEventQueryParams queryParams;

    private static Logger logger = LoggerFactory.getLogger(FDSNEventQuerier.class);
}
