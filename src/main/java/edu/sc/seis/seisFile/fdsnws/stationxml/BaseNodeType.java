package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

import edu.sc.seis.seisFile.TimeUtils;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public abstract class BaseNodeType {

    public BaseNodeType() {}
    
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
        if (startDate == null && startDateTime != null) {
            startDate = TimeUtils.toISOString(getStartDateTime());
        }
        return startDate;
    }

    public String getEndDate() {
        if (endDate == null && endDateTime != null) {
            endDate = TimeUtils.toISOString(getEndDateTime());
        }
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

    
    public void setCode(String code) {
        this.code = code;
    }

    
    public void setStartDate(String startDate) {
        this.startDate = startDate;
        this.startDateTime = null;
    }

    
    public void setEndDate(String endDate) {
        this.endDate = endDate;
        this.endDateTime = null;
    }

    
    public void setHistoricalCode(String historicalCode) {
        this.historicalCode = historicalCode;
    }

    
    public void setAlternateCode(String alternateCode) {
        this.alternateCode = alternateCode;
    }

    
    public void setRestrictedStatus(String restrictedStatus) {
        this.restrictedStatus = restrictedStatus;
    }

    
    public void setDescription(String description) {
        this.description = description;
    }

    
    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public void addComment(Comment comment) {
        commentList.add(comment);
    }

    
    public void setDataAvailability(DataAvailability dataAvailability) {
        this.dataAvailability = dataAvailability;
    }

    
    public Instant getStartDateTime() {
        if (startDateTime == null && startDate != null) {
            startDateTime = TimeUtils.parseISOString(getStartDate());
        }
        return startDateTime;
    }

    
    public void setStartDateTime(Instant startDateTime) {
        this.startDateTime = startDateTime;
        this.startDate = null;
    }

    
    public Instant getEndDateTime() {
        if (endDateTime == null && endDate != null) {
            endDateTime = TimeUtils.parseISOString(getEndDate());
        }
        return endDateTime;
    }

    
    public void setEndDateTime(Instant endDateTime) {
        this.endDateTime = endDateTime;
        this.endDate = null;
    }

    String code;

    String startDate;

    String endDate;
    
    Instant startDateTime;

    Instant endDateTime;

    String historicalCode;

    String alternateCode;

    String restrictedStatus;

    String description;

    List<Comment> commentList = new ArrayList<Comment>();

    DataAvailability dataAvailability;
    
    public int getDbid() {
        return dbid;
    }
    
    void setDbid(int i) {
        this.dbid = i;
    }
    
    /** For Hibernate/JPA
     */
    private int dbid;
}
