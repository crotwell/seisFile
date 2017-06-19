package edu.sc.seis.seisFile.fdsnws;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.stream.XMLStreamException;

import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.fdsnws.virtualnet.VirtualNetworkList;


public class IRISWSVirtualNetworkQuerier extends AbstractFDSNQuerier {

    public IRISWSVirtualNetworkQuerier(IRISWSVirtualNetworkQueryParams queryParams) {
        this.queryParams = queryParams;
    }

    @Override
    public URI formURI() throws URISyntaxException {
        return queryParams.formURI();
    }

    @Override
    public URL getSchemaURL() {
        // TODO Auto-generated method stub
        return null;
    }
    

    public VirtualNetworkList getVirtual() throws FDSNWSException {
        try {
            connect();
            if (!isError()) {
                if (!isEmpty()) {
                    try {
                        VirtualNetworkList vnets = new VirtualNetworkList(getReader());
                        // should validate, but virtualnetworks doesn't have schema
                        response.close();
                        return vnets;
                    } catch(XMLStreamException e) {
                        handleXmlStreamException(e);
                        // can't get here as handleXmlStreamException throw FDSNWSException
                        throw new RuntimeException("Should not happen");
                    } catch(IOException e) {
                        throw new FDSNWSException("trouble closing", e);
                    }
                } else {
                    // return iterator with nothing in it
                    return VirtualNetworkList.createEmptyVirtualNets();
                }
            } else {
                throw new FDSNWSException("Error: " + getErrorMessage(), getConnectionUri(), responseCode);
            }
        } catch(URISyntaxException e) {
            throw new FDSNWSException("Error with URL syntax", e);
        } catch(SeisFileException e) {
            if (e instanceof FDSNWSException) {
                ((FDSNWSException)e).setTargetURI(getConnectionUri());
                throw (FDSNWSException)e;
            } else {
                throw new FDSNWSException(e.getMessage(), e, getConnectionUri());
            }
        }
    }
    
    IRISWSVirtualNetworkQueryParams queryParams;
}
