package edu.sc.seis.seisFile.fdsnws;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import edu.sc.seis.seisFile.ChannelTimeWindow;
import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.mseed.DataRecordIterator;

public class FDSNDataSelectQuerier extends AbstractFDSNQuerier {

    public FDSNDataSelectQuerier(FDSNDataSelectQueryParams queryParams) {
        this(queryParams, null);
    }

    /**
     * This uses POST instead of GET, allowing many channel time windows.
     * 
     * @throws SeisFileException
     */
    public FDSNDataSelectQuerier(FDSNDataSelectQueryParams queryParams, List<ChannelTimeWindow> request) {
        this.queryParams = queryParams;
        this.request = request;
    }
    
    public void enableRestrictedData(String username, String password) {
        this.username = username;
        this.password = password;
        Authenticator.setDefault(new MyAuthenticator(username, password));
        queryParams.setFdsnQueryStyle("queryauth");
    }

    public DataRecordIterator getDataRecordIterator() throws SeisFileException {
        URI uri = null;
        try {
            if (request == null) {
                // normal GET request, so use super
                uri = queryParams.formURI();
                connect(uri);
            } else {
                // POST request, so we have to do connection special
                connectForPost();
            }
            if (!isError()) {
                if (!isEmpty()) {
                    BufferedInputStream bif = new BufferedInputStream(getInputStream());
                    final DataInputStream in = new DataInputStream(bif);
                    return new DataRecordIterator(in);
                } else {
                    // return iterator with nothing in it
                    return new DataRecordIterator(new DataInputStream(new ByteArrayInputStream(new byte[0])));
                }
            } else {
                throw new FDSNWSException("Error: " + getErrorMessage(), uri);
            }
        } catch(URISyntaxException e) {
            throw new FDSNWSException("Error with URL syntax", e);
        } catch(MalformedURLException e) {
            throw new FDSNWSException("Error forming URL", e, uri);
        } catch(IOException e) {
            throw new FDSNWSException("Error with Connection", e, uri);
        }
    }

    /**
     * This uses POST instead of GET, allowing many channel time windows.
     * 
     * @throws SeisFileException
     * @throws URISyntaxException
     * @throws IOException
     * @throws MalformedURLException
     */
    void connectForPost() throws URISyntaxException, MalformedURLException, IOException {
        String postQuery = queryParams.formPostString(request);
        connectionUri = new URI(queryParams.getScheme(), // don't form as all parameters in POST
                                queryParams.getUserInfo(),
                                queryParams.getHost(),
                                queryParams.getPort(),
                                queryParams.getPath(),
                                "",
                                queryParams.getFragment());
        HttpURLConnection conn = (HttpURLConnection)connectionUri.toURL().openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(getConnectTimeout());
        conn.setReadTimeout(getReadTimeout());
        conn.setRequestProperty("User-Agent", getUserAgent());
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        byte[] queryBytes = postQuery.getBytes();
        conn.setRequestProperty("Content-Length", "" + Integer.toString(queryBytes.length));
        conn.setUseCaches(false);
        conn.setAllowUserInteraction(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.connect();
        OutputStream outputStream = conn.getOutputStream();
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(outputStream));
        out.write(postQuery);
        out.close();
        processConnection(conn);
    }

    String username;
    
    String password;
    
    List<ChannelTimeWindow> request;

    FDSNDataSelectQueryParams queryParams;

    public void outputRaw(OutputStream out) throws MalformedURLException, IOException, FDSNWSException, URISyntaxException {
        if (request == null) {
            // normal GET request, so use super
            connect(queryParams.formURI());
        } else {
            // POST request, so we have to do connection special
            connectForPost();
        }
        outputRaw(getInputStream(), out);
    }
}


class MyAuthenticator extends Authenticator {

    String user;

    String password;

    public MyAuthenticator(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password.toCharArray());
    }
}
