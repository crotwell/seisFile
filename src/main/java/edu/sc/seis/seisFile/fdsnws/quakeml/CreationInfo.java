package edu.sc.seis.seisFile.fdsnws.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;


public class CreationInfo {
    
    public static final String ELEMENT_NAME = QuakeMLTagNames.creationInfo;
    
    public CreationInfo(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(ELEMENT_NAME, reader);
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
                    System.err.println("CreationInfo skipping "+elName);
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


    public void setAgencyID(String agencyID) {
        this.agencyID = agencyID;
    }

    
    public void setAgencyURI(String agencyURI) {
        this.agencyURI = agencyURI;
    }

    
    public void setAuthor(String author) {
        this.author = author;
    }

    
    public void setAuthorURI(String authorURI) {
        this.authorURI = authorURI;
    }

    
    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    
    public void setVersion(String version) {
        this.version = version;
    }

    String agencyID, agencyURI, author, authorURI, creationTime, version;
}
