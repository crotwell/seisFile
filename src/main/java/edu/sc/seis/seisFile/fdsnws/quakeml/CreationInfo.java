package edu.sc.seis.seisFile.fdsnws.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.quakeml.QuakeMLTagNames;
import edu.sc.seis.seisFile.fdsnws.stationxml.StaxUtil;


public class CreationInfo {
    
    public CreationInfo(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(QuakeMLTagNames.creationInfo, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.agencyID)) {
                    agencyID = StaxUtil.pullText(reader, QuakeMLTagNames.agencyID);
                } else if (elName.equals(QuakeMLTagNames.agencyURI)) {
                    agencyURI = StaxUtil.pullText(reader, QuakeMLTagNames.agencyURI);
                } else if (elName.equals(QuakeMLTagNames.author)) {
                    author = StaxUtil.pullText(reader, QuakeMLTagNames.author);
                } else if (elName.equals(QuakeMLTagNames.authorURI)) {
                    authorURI = StaxUtil.pullText(reader, QuakeMLTagNames.authorURI);
                } else if (elName.equals(QuakeMLTagNames.creationTime)) {
                    creationTime = StaxUtil.pullText(reader, QuakeMLTagNames.creationTime);
                } else if (elName.equals(QuakeMLTagNames.version)) {
                    version = StaxUtil.pullText(reader, QuakeMLTagNames.version);
                } else {
                    StaxUtil.skipToMatchingEnd(reader);
                }
            } else if (e.isEndElement()) {
                reader.nextEvent();
                return;
            } else {
                e = reader.nextEvent();
            }
        }
    }
    
    
    public String getAgencyID() {
        return agencyID;
    }

    
    public String getAgencyURI() {
        return agencyURI;
    }

    
    public String getAuthor() {
        return author;
    }

    
    public String getAuthorURI() {
        return authorURI;
    }

    
    public String getCreationTime() {
        return creationTime;
    }

    
    public String getVersion() {
        return version;
    }

    String agencyID, agencyURI, author, authorURI, creationTime, version;
}
