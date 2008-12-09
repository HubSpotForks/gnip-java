package com.gnipcentral.client;

import java.net.URL;

/**
 * A Config object provides configuration information for a
 * {@link com.gnipcentral.client.GnipConnection}.  It supports basic settings
 * for username, password, and URL of the Gnip service to connect to as
 * well as advanced settings including an option to tunnel PUT and DELETE
 * requests over POST and configuring network timeouts.
 *
 * The default connection URL is:
 * <pre>
 *   https://prod.gnipcentral.com
 * </pre>
 */
public class Config {

    private static final String DEFAULT_SERVER_URL = "https://prod.gnipcentral.com";

    private boolean useGzip = false;
    private boolean tunnelOverPost = false;
    private String username;
    private String password;
    private String gnipServer;
    private int readTimeoutMillis = 2 * 1000;

    /**
     * Create a {@link Config} object with the specified username and password
     * to the default Gnip server.
     * @param username
     * @param password
     */
    public Config(String username, String password) {
        this.username = username;
        this.password = password;
        this.gnipServer = DEFAULT_SERVER_URL;
    }

    /**
     * Create a {@link Config} object to the Gnip server at the provided URL
     * with the username and password credentials.
     * 
     * @param username
     * @param password
     * @param gnipServer
     */
    public Config(String username, String password, URL gnipServer) {
        this.username = username;
        this.password = password;
        this.gnipServer = gnipServer.toString();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getGnipServer() {
        return gnipServer;
    }

    /**
     * Configure the setting that controls how long the local HTTP connection will
     * wait for a Gnip server's response to a request.  When making requests that
     * transmit large amounts of data, including creating Filters with large
     * rule sets, or that are sent over slow networks, the connection will be more
     * reliable when this timeout is increased.<br/>
     * <br/>
     * This value is configured in milliseconds.<br/>
     * <br/>
     * The default value of this setting is 2000 milliseconds or two seconds.<br/>
     * <br/> 
     * @param readTimeoutMillis
     */
    public void setReadTimeout(int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }

    public int getReadTimeout() {
        return readTimeoutMillis;
    }

    /**
     * Configure the setting that controls whether the HTTP PUT and DELETE are
     * sent to the server by tunneling them through an HTTP POST.  This setting
     * can be useful when a network configuration disallows directly sending PUT
     * and DELETE requests.  The default for this setting is <code>false</code>.
     * @param tunnelOverPost
     */
    public void setTunnelOverPost(boolean tunnelOverPost) {
        this.tunnelOverPost = tunnelOverPost;
    }

    public boolean isTunnelOverPost() {
        return tunnelOverPost;
    }

    /**
     * Configure the setting that controls whether HTTP requests and responses use
     * the the <code>gzip</code> encoding which is set using the <code>Content-Encoding</code>
     * and the <code>Accept-Encoding</code> HTTP headers.
     * @param useGzip
     */
    public void setUseGzip(boolean useGzip) {
        this.useGzip = useGzip;
    }

    public boolean isUseGzip() {
        return useGzip;
    }
}
