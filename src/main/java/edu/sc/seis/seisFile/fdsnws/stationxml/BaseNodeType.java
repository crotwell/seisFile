package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLTagNames;

public class BaseNodeType {

    void parseAttributes(StartElement startE) throws StationXMLException {
        code = StaxUtil.pullAttribute(startE, StationXMLTagNames.CODE);
        startDate = StaxUtil.pullAttributeIfExists(startE, StationXMLTagNames.STARTDATE);
        endDate = StaxUtil.pullAttributeIfExists(startE, StationXMLTagNames.ENDDATE);
        historicalCode = StaxUtil.pullAttributeIfExists(startE, StationXMLTagNames.HISTORICALCODE);
        alternateCode = StaxUtil.pullAttributeIfExists(startE, StationXMLTagNames.ALTERNATECODE);
        restrictedStatus = StaxUtil.pullAttributeIfExists(startE, StationXMLTagNames.RESTRICTEDSTATUS);
    }

    boolean parseSubElement(String elName, final XMLEventReader reader) throws StationXMLException, XMLStreamException {
        if (elName.equals(StationXMLTagNames.DESCRIPTION)) {
            description = StaxUtil.pullText(reader, StationXMLTagNames.DESCRIPTION);
            return true;
        } else if (elName.equals(StationXMLTagNames.COMMENT)) {
            commentList.add(new Comment(reader, StationXMLTagNames.COMMENT));
            return true;
        } else if (elName.equals(StationXMLTagNames.DATAAVAILABILITY)) {
            dataAvailability = new DataAvailability(reader);
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

    public List<Comment> getCommentList() {
        return commentList;
    }

    public DataAvailability getDataAvailability() {
        return dataAvailability;
    }

    String code;

    String startDate;

    String endDate;

    String historicalCode;

    String alternateCode;

    String restrictedStatus;

    String description;

    List<Comment> commentList = new ArrayList<Comment>();

    DataAvailability dataAvailability;
}
