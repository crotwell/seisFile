package edu.sc.seis.seisFile.fdsnws.stationxml;

import java.io.*;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import edu.sc.seis.seisFile.mseed3.FDSNSourceId;
import org.apache.http.client.methods.CloseableHttpResponse;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.FDSNStationQuerier;
import edu.sc.seis.seisFile.fdsnws.StaxUtil;

public class FDSNStationXML {

    public FDSNStationXML(final XMLEventReader reader) throws XMLStreamException, StationXMLException {
        this.reader = reader;
        StaxUtil.skipToStartElement(reader);
        StartElement startE = StaxUtil.expectStartElement(StationXMLTagNames.FDSNSTATIONXML, reader);
        Attribute schemaLocAttr = startE.getAttributeByName(new QName("http://www.w3.org/2001/XMLSchema-instance", "schemaLocation"));
        if (schemaLocAttr != null) {
            xmlSchemaLocation = schemaLocAttr.getValue();
        } else {
            xmlSchemaLocation = StationXMLTagNames.CURRENT_SCHEMALOCATION_VERSION + " " + StationXMLTagNames.CURRENT_SCHEMALOCATION_LOCATION;
        }
        schemaVersion = StaxUtil.pullAttribute(startE, StationXMLTagNames.SCHEMAVERSION);
        OUTER:
        while (reader.hasNext()) {
            XMLEvent e = reader.peek();
            if (e.isStartElement()) {
                String elName = e.asStartElement().getName().getLocalPart();
                switch (elName) {
                    case StationXMLTagNames.SOURCE:
                        source = StaxUtil.pullText(reader, StationXMLTagNames.SOURCE);
                        break;
                    case StationXMLTagNames.SENDER:
                        sender = StaxUtil.pullText(reader, StationXMLTagNames.SENDER);
                        break;
                    case StationXMLTagNames.MODULE:
                        module = StaxUtil.pullText(reader, StationXMLTagNames.MODULE);
                        break;
                    case StationXMLTagNames.MODULEURI:
                        moduleUri = StaxUtil.pullText(reader, StationXMLTagNames.MODULEURI);
                        break;
                    case StationXMLTagNames.CREATED:
                        created = StaxUtil.pullText(reader, StationXMLTagNames.CREATED);
                        break;
                    case StationXMLTagNames.NETWORK:
                        networks = new NetworkIterator(reader);
                        break OUTER;
                    default:
                        StaxUtil.skipToMatchingEnd(reader);
                        break;
                }
            } else if (e.isEndElement()) {
                return;
            } else {
                reader.nextEvent();
            }
        }
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public NetworkIterator getNetworks() {
        if (networks == null) {
            networks = new NetworkIterator(null) {

                @Override
                public boolean hasNext() throws XMLStreamException {
                    return false;
                }

                @Override
                public Network next() throws XMLStreamException, StationXMLException {
                    throw new StationXMLException("No mo networks");
                }

            };
        }
        return networks;
    }

    public Map<Network, List<Station>> extractAllNetworks() throws XMLStreamException, StationXMLException {
        NetworkIterator nIt = getNetworks();
        List<Network> networkList = new ArrayList<>();
        Map<Network, List<Station>> netToSta = new HashMap<>();
        while (nIt.hasNext()) {
            Network n = nIt.next();
            networkList.add(n);
            List<Station> stationList = new ArrayList<>();
            netToSta.put(n, stationList);
            StationIterator sIt = n.getStations();
            while (sIt.hasNext()) {
                Station s = sIt.next();
                stationList.add(s);
            }
        }
        return netToSta;
    }

    /**
     * Finds the channel with the given FDSN SourceId that was active at the given time. Usually the networks
     * Map comes from extractAllNetworks().
     *
     * @param networks map of network to list of stations to search.
     * @param sid FDSN SourceId to match
     * @param time time to overlap
     * @return Channel if found or null if not
     */
    public static Channel findChannelBySID(Map<Network, List<Station>> networks, FDSNSourceId sid, Instant time) {
        if (networks == null || sid == null) { return null;}
        for(Network n : networks.keySet()) {
            if (n.getNetworkCode().equals(sid.getNetworkCode())) {
                for (Station s : networks.get(n)) {
                    if (s.getStationCode().equals(sid.getStationCode())) {
                        for (Channel c : s.getChannelList()) {
                            if (c.getLocCode().equals(sid.getLocationCode()) && c.getChannelCode().equals(sid.getChannelCode())) {
                                if (c.getStartDateTime().isBefore(time) && (c.getEndDateTime() == null || c.getEndDateTime().isAfter(time))) {
                                    return c;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public void closeReader() {
        if (fdsnStationQuerier != null) {
            fdsnStationQuerier.close();
            fdsnStationQuerier = null;
        }
        if (reader != null) {
            try {
                reader.close();
            } catch (XMLStreamException e) {
                logger.warn("problem closing underlying XMLEventReader.", e);
            }
        }
        reader = null;
        if (response != null) {
            try {
                response.close();
            } catch (IOException e) {
                getLogger().warn("trouble closing HttpResponse", e);
            }
        }
        response = null;
    }

    public String getXmlSchemaLocation() {
        return xmlSchemaLocation;
    }

    public void setXmlSchemaLocation(String xmlns) {
        this.xmlSchemaLocation = xmlns;
    }

    public XMLEventReader getReader() {
        return reader;
    }

    public String getModuleUri() {
        return moduleUri;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public static org.slf4j.Logger getLogger() {
        return logger;
    }

    public boolean checkSchemaVersion() {
        if (!(xmlSchemaLocation.split(" ")[0].equals(StationXMLTagNames.CURRENT_SCHEMALOCATION_VERSION) ||
              xmlSchemaLocation.split(" ")[0].equals(StationXMLTagNames.PREV_SCHEMALOCATION_VERSION))) {
            return false;
        }
        if (!(StationXMLTagNames.CURRENT_SCHEMA_VERSION.equals(getSchemaVersion())
                || StationXMLTagNames.CURRENT_SCHEMA_VERSION_ALT.equals(getSchemaVersion())
                || StationXMLTagNames.PREV_SCHEMA_VERSION.equals(getSchemaVersion())
                || "1".equals(getSchemaVersion()))) { // dumb NZ server
            return false;
        }
        return true;
    }

    public void setResponse(CloseableHttpResponse response) {
        this.response = response;
    }

    /**
     * Where input stream came from, so can be closed at end
     */
    CloseableHttpResponse response;

    XMLEventReader reader;

    String source;

    String sender;

    String module;

    String moduleUri;

    String created;

    String xmlSchemaLocation;

    String schemaVersion;

    NetworkIterator networks;

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FDSNStationXML.class);

    public static FDSNStationXML createEmpty() {
        try {
            URL url = FDSNStationXML.class.getClassLoader().getResource("edu/sc/seis/seisFile/stationxml/empty.stationxml");
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader r;
            r = factory.createXMLEventReader(url.toString(), url.openStream());
            return new FDSNStationXML(r);
        } catch (Exception e) {
            throw new RuntimeException("Should not happen", e);
        }
    }

    public static URL findInternalSchema() {
        return FDSNStationXML.class.getClassLoader().getResource("edu/sc/seis/seisFile/stationxml/fdsn-station-1.1.xsd");
    }
    
    public static void printSchema(OutputStream out) throws IOException {
        BufferedInputStream bufIn = new BufferedInputStream(findInternalSchema().openStream());
        BufferedOutputStream bufOut = new BufferedOutputStream(out);
        byte[] buf = new byte[1024];
        int numRead = bufIn.read(buf);
        while (numRead != -1) {
            bufOut.write(buf, 0, numRead);
            numRead = bufIn.read(buf);
        }
        bufIn.close(); // close as we hit EOF
        bufOut.flush();// only flush in case outside wants to write more
    }


    public static FDSNStationXML loadStationXML(Reader streamReader) throws XMLStreamException, IOException, SeisFileException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader r = factory.createXMLEventReader(streamReader);
        XMLEvent e = r.peek();
        while (!e.isStartElement()) {
            r.nextEvent(); // eat this one
            e = r.peek(); // peek at the next
        }
        FDSNStationXML fdsnStationXML = new FDSNStationXML(r);
        return fdsnStationXML;
    }

    public static FDSNStationXML loadStationXML(InputStream stream) throws XMLStreamException, IOException, SeisFileException {
        return loadStationXML(new InputStreamReader(stream));
    }

    public static FDSNStationXML loadStationXML(String filename) throws XMLStreamException, IOException, SeisFileException {
        return loadStationXML(new FileInputStream(filename));
    }

    public static FDSNStationXML loadStationXML(File filename) throws XMLStreamException, IOException, SeisFileException {
        return loadStationXML(new FileInputStream(filename));
    }

    /**
     * this is mainly so that the resources associated with the xml reader are
     * no garbage collected. Was having trouble with connections being closed in
     * the middle of reading xml, I believe due to finalize() and adding this
     * seemed to fixe it.
     */
    FDSNStationQuerier fdsnStationQuerier;

    public void setQuerier(FDSNStationQuerier fdsnStationQuerier) {
        this.fdsnStationQuerier = fdsnStationQuerier;
    }
}
