package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;

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
            startDate = toISOString(getStartDateTime());
        }
        return startDate;
    }

    public String getEndDate() {
        if (endDate == null && endDateTime != null) {
            endDate = toISOString(getEndDateTime());
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

    
    public void setDataAvailability(DataAvailability dataAvailability) {
        this.dataAvailability = dataAvailability;
    }

    
    public ZonedDateTime getStartDateTime() {
        if (startDateTime == null && startDate != null) {
            startDateTime = parseISOString(getStartDate());
        }
        return startDateTime;
    }

    
    public void setStartDateTime(ZonedDateTime startDateTime) {
        this.startDateTime = startDateTime;
        this.startDate = null;
    }

    
    public ZonedDateTime getEndDateTime() {
        if (endDateTime == null && endDate != null) {
            endDateTime = parseISOString(getEndDate());
        }
        return endDateTime;
    }

    
    public void setEndDateTime(ZonedDateTime endDateTime) {
        this.endDateTime = endDateTime;
        this.endDate = null;
    }

    String code;

    String startDate;

    String endDate;
    
    ZonedDateTime startDateTime;

    ZonedDateTime endDateTime;

    String historicalCode;

    String alternateCode;

    String restrictedStatus;

    String description;

    List<Comment> commentList = new ArrayList<Comment>();

    DataAvailability dataAvailability;
    
    public Integer getDbid() {
        return dbid;
    }
    
    void setDbid(Integer i) {
        this.dbid = i;
    }
    
    public static DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSSSSSX").withZone(TZ_UTC);
    }
    
    public static DateTimeFormatter getFracSecondsDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX").withZone(TZ_UTC);
    }
    
    public static DateTimeFormatter getMillisDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSSX").withZone(TZ_UTC);
    }

    public static ZonedDateTime parseISOString(String time) {
        if (time.length() == 15 || (time.length() == 16 && time.endsWith(ZULU))) {
            // no factional seconds
            return ZonedDateTime.parse(time, getFracSecondsDateTimeFormatter());
        } else if (time.length() == 20) {
            // only milliseconds
            return ZonedDateTime.parse(time, getMillisDateTimeFormatter());
        }
        return ZonedDateTime.parse(time, getDateTimeFormatter());
    }
    
    public static String toISOString(ZonedDateTime time) {
        return getDateTimeFormatter().format(time);
    }
    
    public static final String ZULU = "Z";
    
    public static final ZoneId TZ_UTC = ZoneId.of("UTC");

    /** For Hibernate/JPA
     */
    private Integer dbid;
}
