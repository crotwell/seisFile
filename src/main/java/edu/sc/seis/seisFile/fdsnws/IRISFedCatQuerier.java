package edu.sc.seis.seisFile.fdsnws;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.stationxml.FDSNStationXML;
import edu.sc.seis.seisFile.fdsnws.stationxml.Network;
import edu.sc.seis.seisFile.fdsnws.stationxml.Station;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static edu.sc.seis.seisFile.fdsnws.AbstractQueryParams.FORMAT;
import static edu.sc.seis.seisFile.fdsnws.IRISFedCatQueryParams.FORMAT_TEXT;

public class IRISFedCatQuerier extends AbstractFDSNQuerier {

    public IRISFedCatQuerier(IRISFedCatQueryParams queryParams) {
        this.queryParams = queryParams;
    }

    @Override
    public URI formURI() throws URISyntaxException {
        return queryParams.formURI();
    }

    @Override
    public URL getSchemaURL() {
        if (queryParams.isStation()) {
            FDSNStationXML.findInternalSchema();
        }
        return null;
    }

    public List<Station> getStationsFromText() throws FDSNWSException {
        queryParams.setFormat(FORMAT_TEXT);
        if (Objects.equals(queryParams.params.get(FORMAT), FORMAT_TEXT)) {
            setAcceptHeader("text/plain");
        }
        try {
            connect();
            if (!isError()) {
                if (!isEmpty()) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(getInputStream()));
                    String line = in.readLine().trim();
                    while (line.length()==0 || line.startsWith("#")) {
                        line = in.readLine().trim();
                    }
                    HashMap<String, Network> networks = new HashMap<>();
                    List<Station> staList = new ArrayList<>();
                    while (line != null && line.length()!=0) {
                        String[] split = line.split("\\|");
                        Network net;
                        if (networks.containsKey(split[0])) {
                            net = networks.get(split[0]);
                        } else {
                            net = new Network(split[0]);
                            networks.put(net.getNetworkId(), net);
                        }
                        Station sta = new Station(net, split[1]);
                        sta.setLatitude(Float.parseFloat(split[2]));
                        sta.setLongitude(Float.parseFloat(split[3]));
                        sta.setName(split[4]);
                        sta.setStartDate(split[5]);
                        if (split[6].startsWith("19") || split[6].startsWith("20")) {
                            sta.setEndDate(split[6]);
                        }
                        staList.add(sta);
                        line = in.readLine().trim();
                    }
                    return staList;
                } else {
                    // return iterator with nothing in it
                    return List.of();
                }
            } else {
                throw new FDSNWSException("Error: " + getErrorMessage(), getConnectionUri(), responseCode);
            }
        } catch(URISyntaxException e) {
            throw new FDSNWSException("Error with URL syntax", e);
        } catch(IOException e) {
            throw new FDSNWSException("Error parsing text format from server", e);
        } catch(SeisFileException e) {
            if (e instanceof FDSNWSException) {
                ((FDSNWSException)e).setTargetURI(getConnectionUri());
                throw (FDSNWSException)e;
            } else {
                throw new FDSNWSException(e.getMessage(), e, getConnectionUri());
            }
        }
    }

    public static final String SERVICE_NAME = "fedcatalog";

    IRISFedCatQueryParams queryParams;

    private static Logger logger = LoggerFactory.getLogger(IRISFedCatQuerier.class);

}
