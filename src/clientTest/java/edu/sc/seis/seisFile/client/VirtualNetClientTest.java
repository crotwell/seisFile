package edu.sc.seis.seisFile.client;

import edu.sc.seis.seisFile.fdsnws.FDSNWSException;
import edu.sc.seis.seisFile.fdsnws.IRISWSVirtualNetworkQuerier;
import edu.sc.seis.seisFile.fdsnws.IRISWSVirtualNetworkQueryParams;
import edu.sc.seis.seisFile.fdsnws.virtualnet.VirtualNetwork;
import edu.sc.seis.seisFile.fdsnws.virtualnet.VirtualNetworkList;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

public class VirtualNetClientTest {

    @Test
    public void formURITest() throws URISyntaxException {
        IRISWSVirtualNetworkQueryParams qp = new IRISWSVirtualNetworkQueryParams();
        IRISWSVirtualNetworkQuerier querier = new IRISWSVirtualNetworkQuerier(qp);
        System.err.println(querier.formURI());
        assertEquals("https://service.earthscope.org:443/irisws/virtualnetwork/1/query?", querier.formURI().toString());
    }

    @Test
    public void testForNet() throws FDSNWSException {
        String code = "_AFTAC";
        IRISWSVirtualNetworkQueryParams qp = new IRISWSVirtualNetworkQueryParams();
        qp.setCode(code);
        IRISWSVirtualNetworkQuerier querier = new IRISWSVirtualNetworkQuerier(qp);
        VirtualNetworkList vnet = querier.getVirtual();
        assertNotNull(vnet);
        System.err.println(vnet.getVirtualNetworks().size());
        for (VirtualNetwork vn : vnet.getVirtualNetworks()) {
            System.err.println(vn.getCode());
        }
    }
}
