package edu.sc.seis.seisFile.fdsnws;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;

public class FDSNEventCatalogQuerier extends AbstractFDSNQuerier {

    public FDSNEventCatalogQuerier(FDSNEventQueryParams queryParams) {
        this.queryParams = queryParams;
    }

    public List<String> getCatalogs() throws FDSNWSException {
        return getList("Catalog");
    }
    public List<String> getContributors() throws FDSNWSException {
        return getList("Contributor");
        
    }
   
    List<String> getList(String style) throws FDSNWSException {
        try {
            fdsnQueryStyle = style.toLowerCase()+"s";
            connect();
            if (!isError()) {
                if (!isEmpty()) {
                    try {
                        XMLEventReader in = getReader();
                        return parse(in, style);
                    } catch(XMLStreamException e) {
                        handleXmlStreamException(e);
                        // can't get here as handleXmlStreamException throw
                        // FDSNWSException
                        throw new RuntimeException("Should not happen");
                    }
                } else {
                    // return iterator with nothing in it
                    return new ArrayList<String>();
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

    static List<String> parse(XMLEventReader reader, String style) throws XMLStreamException, StationXMLException {
        List<String> out = new ArrayList<String>();
        StaxUtil.skipToStartElement(reader);
        StartElement startE = StaxUtil.expectStartElement(style+"s", reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(style)) {
                    out.add(StaxUtil.pullText(reader, style));
                } else {
                    System.err.println(style + " skipping " + elName);
                    StaxUtil.skipToMatchingEnd(reader);
                }
            } else if (e.isEndElement()) {
                reader.nextEvent();
                break;
            } else {
                e = reader.nextEvent();
            }
        }
        return out;
    }

    FDSNEventQueryParams queryParams;
    
    String fdsnQueryStyle = "catalogs";
    
    String getFdsnQueryStyle() {
        return fdsnQueryStyle;
    }

    @Override
    public URI formURI() throws URISyntaxException {
        FDSNEventQueryParams catalogQP = queryParams.clone();
        catalogQP.fdsnQueryStyle = getFdsnQueryStyle();
        catalogQP.clear();
        return catalogQP.formURI();
    }

    private static Logger logger = LoggerFactory.getLogger(FDSNEventCatalogQuerier.class);
}
