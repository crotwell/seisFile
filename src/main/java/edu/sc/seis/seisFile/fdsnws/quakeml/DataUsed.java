package edu.sc.seis.seisFile.fdsnws.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class DataUsed {

    public static final String ELEMENT_NAME = QuakeMLTagNames.dataUsed;

    public DataUsed(XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(ELEMENT_NAME, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.waveType)) {
                    waveType = StaxUtil.pullText(reader, QuakeMLTagNames.waveType);
                } else if (elName.equals(QuakeMLTagNames.stationCount)) {
                    stationCount = StaxUtil.pullInt(reader, QuakeMLTagNames.stationCount);
                } else if (elName.equals(QuakeMLTagNames.componentCount)) {
                    componentCount = StaxUtil.pullInt(reader, QuakeMLTagNames.componentCount);
                } else if (elName.equals(QuakeMLTagNames.shortestPeriod)) {
                    shortestPeriod = StaxUtil.pullFloat(reader, QuakeMLTagNames.shortestPeriod);
                } else if (elName.equals(QuakeMLTagNames.longestPeriod)) {
                    longestPeriod = StaxUtil.pullFloat(reader, QuakeMLTagNames.longestPeriod);
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

    public String getWaveType() {
        return waveType;
    }

    public int getStationCount() {
        return stationCount;
    }

    public int getComponentCount() {
        return componentCount;
    }

    public float getShortestPeriod() {
        return shortestPeriod;
    }

    public float getLongestPeriod() {
        return longestPeriod;
    }

    private String waveType;

    private int stationCount;

    private int componentCount;

    private float shortestPeriod;

    private float longestPeriod;
}
