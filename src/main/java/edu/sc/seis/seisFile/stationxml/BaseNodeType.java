package edu.sc.seis.seisFile.stationxml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

public class BaseNodeType {

    void parseAttributes(StartElement startE) throws StationXMLException {
        code = StaxUtil.pullAttribute(startE, StationXMLTagNames.CODE);
        startDate = StaxUtil.pullAttributeIfExists(startE, StationXMLTagNames.CODE);
        endDate = StaxUtil.pullAttributeIfExists(startE, StationXMLTagNames.CODE);
        historicalCode = StaxUtil.pullAttributeIfExists(startE, StationXMLTagNames.CODE);
        alternateCode = StaxUtil.pullAttributeIfExists(startE, StationXMLTagNames.CODE);
        restrictedStatus = StaxUtil.pullAttributeIfExists(startE, StationXMLTagNames.CODE);
    }

    boolean parseSubElement(String elName, final XMLEventReader reader) throws StationXMLException, XMLStreamException {
        if (elName.equals(StationXMLTagNames.DESCRIPTION)) {
            description = StaxUtil.pullText(reader, StationXMLTagNames.DESCRIPTION);
            return true;
        } else if (elName.equals(StationXMLTagNames.COMMENT)) {
            commentList.add(StaxUtil.pullText(reader, StationXMLTagNames.COMMENT));
            return true;
        } else {
            return false;
        }
    }

    public String getCode() {
        return code;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getHistoricalCode() {
        return historicalCode;
    }

    public String getAlternateCode() {
        return alternateCode;
    }

    public String getRestrictedStatus() {
        return restrictedStatus;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getCommentList() {
        return commentList;
    }

    String code;

    String startDate;

    String endDate;

    String historicalCode;

    String alternateCode;

    String restrictedStatus;

    String description;

    List<String> commentList = new ArrayList<String>();
}
