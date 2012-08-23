package edu.sc.seis.seisFile.stationxml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class IrisComment {

    public IrisComment(XMLEventReader reader) throws XMLStreamException, StationXMLException {
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.IRISCOMMENT, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(StationXMLTagNames.STARTDATE)) {
                    startDate = StaxUtil.pullText(reader, StationXMLTagNames.STARTDATE);
                } else if (elName.equals(StationXMLTagNames.ENDDATE)) {
                    endDate = StaxUtil.pullText(reader, StationXMLTagNames.ENDDATE);
                } else if (elName.equals(StationXMLTagNames.IRISTEXT)) {
                    text = StaxUtil.pullText(reader, StationXMLTagNames.IRISTEXT);
                } else if (elName.equals(StationXMLTagNames.IRISCLASS)) {
                    commentClass = StaxUtil.pullText(reader, StationXMLTagNames.IRISCLASS);
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCommentClass() {
        return commentClass;
    }

    public void setCommentClass(String commentClass) {
        this.commentClass = commentClass;
    }

    private String startDate;

    private String endDate;

    private String text;

    private String commentClass; // class is reserved key word
}
