package edu.sc.seis.seisFile.fdsnws.quakeml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;
import edu.sc.seis.seisFile.fdsnws.quakeml.Comment;
import edu.sc.seis.seisFile.fdsnws.quakeml.CreationInfo;
import edu.sc.seis.seisFile.fdsnws.quakeml.QuakeMLTagNames;


public class Arrival {

    public Arrival(XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(QuakeMLTagNames.magnitude, reader);
        publicId = StaxUtil.pullAttribute(startE, QuakeMLTagNames.publicId);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.comment)) {
                    commentList.add(new Comment(reader));
                } else if (elName.equals(QuakeMLTagNames.phase)) {
                    phase = StaxUtil.pullText(reader, QuakeMLTagNames.phase);
                } else if (elName.equals(QuakeMLTagNames.azimuth)) {
                    azimuth = StaxUtil.pullFloat(reader, QuakeMLTagNames.azimuth);
                } else if (elName.equals(QuakeMLTagNames.distance)) {
                    distance = StaxUtil.pullFloat(reader, QuakeMLTagNames.distance);
                } else if (elName.equals(QuakeMLTagNames.timeResidual)) {
                    timeResidual = StaxUtil.pullFloat(reader, QuakeMLTagNames.timeResidual);
                } else if (elName.equals(QuakeMLTagNames.pickID)) {
                    pickID = StaxUtil.pullText(reader, QuakeMLTagNames.pickID);
                } else if (elName.equals(QuakeMLTagNames.creationInfo)) {
                    creationInfo = new CreationInfo(reader);
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
    
    String publicId;
    
    String phase;
    
    float azimuth;
    
    float distance;

    float timeResidual;
    
    String pickID;
    
    CreationInfo creationInfo;

    List<Comment> commentList = new ArrayList<Comment>();
}
