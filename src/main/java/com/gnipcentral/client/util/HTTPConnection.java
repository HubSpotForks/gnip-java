package com.gnipcentral.client.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.gnipcentral.client.Config;

/**
 * Basic abstraction atop an HTTP connection that is used to handle low-level Gnip <> HTTP protocol interaction.
 * This class is used by the {@link com.gnipcentral.client.GnipConnection} to communicate with a Gnip server
 * and is not intended to be used by clients.
 * <br/>
 * <br/>
 * This class sets several headers on the request, in part based on the how the connection is configured
 * by the {@link com.gnipcentral.client.Config} instance.
 * <ul>
 * <li>
 * <code>Content-Encoding</code> and <code>Accept-Encoding</code> are set if {@link Config#setUseGzip(boolean)} is
 * <code>true</code>
 * </li>
 * <li><code>User-Agent</code> is set to a Java-client value that includes the version of the client library</li>
 * <li><code>Authorization</code> is set using basic authentication credentials</li>
 * </ul> 
 */
public class HTTPConnection {

    private static final Logger LOG = LoggerFactory.getInstance();
    private static final String USER_AGENT_STRING;

    static {
        String properties = "META-INF/maven/com.gnipcentral/gnip-client/pom.properties";
        InputStream is = null;
        String v = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream(properties);
            if(is != null) {
                Properties p = new Properties();
                p.load(is);
                v = p.getProperty("version");
            }
        }
        catch(Exception e) {
            System.err.println("Unable to load version number for the Gnip client library.  Exception: " + e);
        }
        finally {
            try{if(is != null) is.close();}catch(IOException ignore) {/*Fail quietly*/} 
            USER_AGENT_STRING = "Gnip-Client-Java/" + (v != null ? v : "build");
        }
    }

    private final Config config;

    /**
     * Create a new {@link HTTPConnection} with the provided configuration.
     * @param config the configuration for the connection
     */
    public HTTPConnection(Config config) {
        this.config = config;
    }

    /**
     * Send an HTTP request of type GET to the given URL.
     * @param urlString the URL to receive the GET
     * @return the {@link InputStream} from the response
     * @throws IOException if an exception occurs communicating with the server
     */
    public InputStream doGet(String urlString) throws IOException {
        HttpURLConnection urlConnection = getConnection(urlString, HTTPMethod.GET);
        LOG.log("HTTP GET to %s\n", urlString);
        return getData(urlConnection);
    }

    private InputStream getData(HttpURLConnection urlConnection) throws IOException {
        urlConnection.connect();
        int responseCode = urlConnection.getResponseCode();
        String responseMessage = urlConnection.getResponseMessage();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Error with request code:" + responseCode + " message: " + responseMessage);
        }
        InputStream resultStream;
        InputStream stream;
        String contentEncoding = urlConnection.getHeaderField("Content-Encoding");
        if ("gzip".equalsIgnoreCase(contentEncoding)) {
            stream = new GZIPInputStream(urlConnection.getInputStream());
        } else {
            stream = urlConnection.getInputStream();
        }
        ByteArrayOutputStream resultData = new ByteArrayOutputStream();
        IOUtils.copy(stream, resultData);
        resultStream = new ByteArrayInputStream(resultData.toByteArray());

        urlConnection.disconnect();
        return resultStream;
    }

    private HttpURLConnection getConnection(String urlString, HTTPMethod method) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod(method.name());
        urlConnection.addRequestProperty("Content-Type", "application/xml");
        urlConnection.addRequestProperty("Authorization", "Basic " + new String(Base64.encodeBase64(getGnipCredentials()), Charset.forName("UTF-8")));
        urlConnection.addRequestProperty("User-Agent", USER_AGENT_STRING);
        urlConnection.setConnectTimeout(config.getReadTimeout());
        urlConnection.setReadTimeout(config.getReadTimeout());
        if (config.isUseGzip()) {
            urlConnection.addRequestProperty("Accept-Encoding", "gzip");
            urlConnection.addRequestProperty("Content-Encoding", "gzip");
        }
        return urlConnection;
    }

    private byte[] getGnipCredentials() {
        String credentials = config.getUsername() + ":" + config.getPassword();
        return credentials.getBytes(Charset.forName("UTF-8"));
    }
}
