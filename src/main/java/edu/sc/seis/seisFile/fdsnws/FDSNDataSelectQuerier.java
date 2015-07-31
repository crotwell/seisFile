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
import java.net.URL;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import edu.sc.seis.seisFile.ChannelTimeWindow;
import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.mseed.DataRecordIterator;

public class FDSNDataSelectQuerier extends AbstractFDSNQuerier {

    public FDSNDataSelectQuerier(FDSNDataSelectQueryParams queryParams) {
        this(queryParams, null);
    }

    /** There is no schema for dataselect. Returns null */
    public URL getSchemaURL() {
        return null;
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
        try {
            if (request == null) {
                // normal GET request, so use super
                connect();
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
                logger.info("Error: " + getErrorMessage());
                if (responseCode == 401 || responseCode == 403) {
                    throw new FDSNWSAuthorizationException("Not Authorized for Restricted Data: " + getErrorMessage(),
                                                           getConnectionUri(),
                                                           responseCode);
                }
                throw new FDSNWSException("Error: " + getErrorMessage(), getConnectionUri(), responseCode);
            }
        } catch(URISyntaxException e) {
            throw new FDSNWSException("Error with URL syntax", e);
        } catch(MalformedURLException e) {
            throw new FDSNWSException("Error forming URL", e, getConnectionUri());
        } catch(IOException e) {
            throw new FDSNWSException("Error with Connection", e, getConnectionUri());
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
        connectionUri = new URI(queryParams.getScheme(), // don't form as all
                                                         // parameters in POST
                                queryParams.getUserInfo(),
                                queryParams.getHost(),
                                queryParams.getPort(),
                                queryParams.getPath(),
                                "",
                                queryParams.getFragment());
        logger.info("Post Query: " + connectionUri);
        logger.info(postQuery);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(getConnectTimeout())
                .setConnectionRequestTimeout(getConnectTimeout())
                .setSocketTimeout(getReadTimeout())
                .build();
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
        HttpPost request = new HttpPost(connectionUri);
        request.setHeader("User-Agent", getUserAgent());
        request.setHeader("Accept", "application/vnd.fdsn.mseed");
        request.setHeader("Accept-Encoding", "gzip, deflate");
        HttpEntity entity = new StringEntity(postQuery);
        request.setEntity(entity);
        response = httpClient.execute(request);
        processConnection(response);
    }

    String username;

    String password;

    List<ChannelTimeWindow> request;

    FDSNDataSelectQueryParams queryParams;

    public void outputRaw(OutputStream out) throws MalformedURLException, IOException, FDSNWSException,
            URISyntaxException {
        if (request == null) {
            // normal GET request, so use super
            connect();
        } else {
            // POST request, so we have to do connection special
            connectForPost();
        }
        outputRaw(getInputStream(), out);
    }

    @Override
    public URI formURI() throws URISyntaxException {
        return queryParams.formURI();
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FDSNDataSelectQuerier.class);
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
