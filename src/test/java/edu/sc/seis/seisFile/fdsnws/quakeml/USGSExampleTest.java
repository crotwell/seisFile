package edu.sc.seis.seisFile.fdsnws.quakeml;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.FDSNEventQuerier;
import edu.sc.seis.seisFile.fdsnws.stationxml.StationXMLException;

public class USGSExampleTest {

    @Test
    public void test() throws IOException, SeisFileException, XMLStreamException, StationXMLException, SAXException {
        String[] usgsFilename = new String[] {};
        String[] filenames = new String[] {"usgs/B000I68313Short.quakeml",
                                           "usgs/201330/201330_1374538994_C000IRGM_24_Long.quakeml_Verified",
                                           "usgs/201330/201330_1374600435_B000IKUF_48_Long.quakeml_Verified"
                                        };
        /*
            "usgs/201330/201330_1374541979_B000IKFK_21_Long.quakeml_Verified",
                                           "usgs/201330/201330_1374543994_B000IKG9_30_Long.quakeml_Verified",
                                           "usgs/201330/201330_1374544987_B000IKGM_8_Long.quakeml_Verified",
                                           "usgs/201330/201330_1374545027_B000IKGR_39_Long.quakeml_Verified",
                                           "usgs/201330/201330_1374545986_B000IKGX_31_Long.quakeml_Verified",
                                           "usgs/201330/201330_1374548533_B000IKH7_31_Long.quakeml_Verified",
                                           "usgs/201330/201330_1374549661_B000IKHG_7_Long.quakeml_Verified",
                                           "usgs/201330/201330_1374551665_C000IRGP_4_Long.quakeml_Verified",
                                           "usgs/201330/201330_1374553118_B000IKI5_12_Long.quakeml_Verified",
                                           "usgs/201330/201330_1374556280_B000IKIR_36_Long.quakeml_Verified",
                                           "usgs/201330/201330_1374557156_C000IRGT_4_Long.quakeml_Verified",
                                           "usgs/201330/201330_1374563445_B000IKJJ_7_Long.quakeml_Verified"
         */
        for (String filename : filenames) {
            try {
                System.out.println("checking "+filename);
                URL url = QuakeMLTest.loadResourceURL(filename);
                XMLInputFactory factory = XMLInputFactory.newInstance();
                XMLEventReader r = factory.createXMLEventReader(url.toString(), QuakeMLTest.loadResource(filename));
                FDSNEventQuerier.validateQuakeML(factory.createXMLStreamReader(url.toString(),
                                                                               QuakeMLTest.loadResource(filename)));
                Quakeml qml = new Quakeml(r);
                assertTrue("sceham version", qml.checkSchemaVersion());
                EventParameters ep = qml.getEventParameters();
                EventIterator it = ep.getEvents();
                while (it.hasNext()) {
                    System.out.println(it.next());
                }
            } catch(Exception ex) {
                ex.printStackTrace(System.err);
                Throwable subEx = ex;
                while (subEx.getCause() != null) {
                    subEx = subEx.getCause();
                }
                if (subEx instanceof SAXParseException) {
                    SAXParseException saxEx = (SAXParseException)subEx;
                    fail("SAX Trouble with " + filename + " at " + saxEx.getLineNumber() + ":"
                            + saxEx.getColumnNumber()+" "+ex);
                } else {
                    fail("Trouble with " + filename+" "+ex);
                }
            }
        }
    }
}
