package com.gnipcentral.client;

import java.net.URL;

/**
 * Configuration information for a {@link GnipConnection}.
 * It supports basic settings
 * for username, password, and URL of the Gnip service to connect to as
 * well as advanced settings including an option to tunnel PUT and DELETE
 * requests over POST and configuring network timeouts.
 *
 * The default connection URL is:
 * <pre>
 *   {@link #DEFAULT_SERVER_URL}
 * </pre>
 */
public class Config {

    public static final String DEFAULT_SERVER_URL = "https://prod.gnipcentral.com";
    public static final int DEFAULT_READ_TIMEOUT_SECONDS = 2;

    private boolean useGzip = false;
    private boolean tunnelOverPost = false;
    private String username;
    private String password;
    private String gnipServer;
    private int readTimeoutMillis = DEFAULT_READ_TIMEOUT_SECONDS * 1000;

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

    /**
     * Retrieves the username used to connect to Gnip.
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Retrieves the password used to connect to Gnip.
     * @return 
     */
    public String getPassword() {
        return password;
    }

    /**
     * Retrieves the URL of the Gnip service.
     * @return
     */
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
     * The default value of this setting is {@value #DEFAULT_READ_TIMEOUT_SECONDS} seconds<br/>
     * <br/> 
     * @param readTimeoutMillis the read timeout value in milliseconds
     */
    public void setReadTimeout(int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }

    /**
     * Retrieves the read timeout.<br/>
     * <br/>
     * The default value of this setting is {@value #DEFAULT_READ_TIMEOUT_SECONDS} seconds<br/>
     * 
     * @return the read timeout in milliseconds
     */
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

    /**
     * Retrieves the tunnel over post flag.  The default value is <code>false</code>.
     *
     * @return 
     */
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

    /**
     * Retrieves the compression flag.  The default value is <code>false</code>.
     * @return
     */
    public boolean isUseGzip() {
        return useGzip;
    }
}
