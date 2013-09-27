package edu.sc.seis.seisFile.fdsnws.quakeml;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class Tensor {

    public static final String ELEMENT_NAME = QuakeMLTagNames.tensor;

    public Tensor(final XMLEventReader reader) throws XMLStreamException, SeisFileException {
        StartElement startE = StaxUtil.expectStartElement(ELEMENT_NAME, reader);
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                if (elName.equals(QuakeMLTagNames.Mrr)) {
                    Mrr = new RealQuantity(reader, QuakeMLTagNames.Mrr);
                } else if (elName.equals(QuakeMLTagNames.Mtt)) {
                    Mtt = new RealQuantity(reader, QuakeMLTagNames.Mtt);
                } else if (elName.equals(QuakeMLTagNames.Mpp)) {
                    Mpp = new RealQuantity(reader, QuakeMLTagNames.Mpp);
                } else if (elName.equals(QuakeMLTagNames.Mrt)) {
                    Mrt = new RealQuantity(reader, QuakeMLTagNames.Mrt);
                } else if (elName.equals(QuakeMLTagNames.Mrp)) {
                    Mrp = new RealQuantity(reader, QuakeMLTagNames.Mrp);
                } else if (elName.equals(QuakeMLTagNames.Mtp)) {
                    Mtp = new RealQuantity(reader, QuakeMLTagNames.Mtp);
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

    public RealQuantity getMrr() {
        return Mrr;
    }

    public RealQuantity getMtt() {
        return Mtt;
    }

    public RealQuantity getMpp() {
        return Mpp;
    }

    public RealQuantity getMrt() {
        return Mrt;
    }

    public RealQuantity getMrp() {
        return Mrp;
    }

    public RealQuantity getMtp() {
        return Mtp;
    }

    RealQuantity Mrr, Mtt, Mpp, Mrt, Mrp, Mtp;
}
