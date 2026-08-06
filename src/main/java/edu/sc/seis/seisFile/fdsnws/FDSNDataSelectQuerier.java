package edu.sc.seis.seisFile.fdsnws;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import edu.sc.seis.seisFile.ChannelTimeWindow;
import edu.sc.seis.seisFile.SeisFileException;
import edu.sc.seis.seisFile.mseed.DataRecordIterator;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.http.io.entity.StringEntity;

import javax.xml.stream.XMLStreamException;

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
     */
    public FDSNDataSelectQuerier(FDSNDataSelectQueryParams queryParams, List<ChannelTimeWindow> request) {
        this.queryParams = queryParams;
        this.request = request;
        setAcceptHeader("application/vnd.fdsn.mseed");
    }

    public void enableRestrictedData(String username, String password) {
        enableRestrictedData( username,  password, null); // empty realm
    }
    
    public void enableRestrictedData(String username, String password, String realm) {
        this.username = username;
        this.password = password;
        this.realm = realm;
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
                    DataRecordIterator drIt = new DataRecordIterator(in);
                    drIt.setQuerier(this);
                    return drIt;
                } else {
                    // return iterator with nothing in it
                    return new DataRecordIterator(new DataInputStream(new ByteArrayInputStream(new byte[0])));
                }
            } else {
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
     * @throws FDSNWSException 
     */
    void connectForPost() throws URISyntaxException, MalformedURLException, IOException, FDSNWSException {
        String postQuery = queryParams.formPostString(request);
        connectionUri = formURIForPost();
        logger.info("Post Query: " + connectionUri);
        logger.info(postQuery);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(getConnectTimeout())
                .setRedirectsEnabled(true)
                .setCircularRedirectsAllowed(false)
                .build();
        PoolingHttpClientConnectionManager manager = PoolingHttpClientConnectionManagerBuilder
                .create()
                .setDefaultSocketConfig(SocketConfig.custom()
                        .setSoTimeout(getReadTimeout())
                        .build()
                )
                .setDefaultConnectionConfig(ConnectionConfig.custom()
                        .setConnectTimeout(getConnectTimeout())
                        .build()
                )
                .build();
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
                .setConnectionManager(manager)
                .setDefaultRequestConfig(requestConfig);
        if (username != null && username.length()!= 0 && password != null && password.length() != 0) {
            logger.info("Adding user/pass cred to query");
            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(username, password.toCharArray());
            BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope(queryParams.getHTTPHost(), realm, queryParams.getScheme()), creds);
            httpClientBuilder.setDefaultCredentialsProvider(credsProvider);
        }

        try (CloseableHttpClient httpClient = httpClientBuilder.build()) {
            TimeQueryLog.add(connectionUri);
            HttpPost request = new HttpPost(connectionUri);
            HttpClientContext context = HttpClientContext.create();
            request.setHeader("User-Agent", getUserAgent());
            request.setHeader("Accept", getAcceptHeader());
            request.setHeader("Accept-Encoding", "gzip, deflate");
            HttpEntity entity = new StringEntity(postQuery);
            request.setEntity(entity);
            // as we have setRedirectsEnabled set for the request config and
            // setCircularRedirectsAllowed unset, we should need no special handling
            // of request redirects -- the library should do that automatically.
            this.response = httpClient.execute(request, context, response -> {
                try {
                    processConnection(response);
                } catch (FDSNWSException | XMLStreamException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                return response;
            });
        } catch(IOException e) {
            throw new FDSNWSException("Problem with connection", e, connectionUri);
        } catch(RuntimeException e) {
            throw new FDSNWSException("At-runtime problem with connection", e.getCause(), connectionUri);
        }
    }

    String username;

    String password;
    
    String realm = null; // null means any realm

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

    public URI formURIForPost() throws URISyntaxException {
        // all parameters in POST, not in url
        URI uriForGet = formURI();
        return new URI(uriForGet.getScheme(),
                uriForGet.getUserInfo(),
                uriForGet.getHost(),
                uriForGet.getPort(),
                uriForGet.getPath(),
                "",
                uriForGet.getFragment());
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(FDSNDataSelectQuerier.class);
}

