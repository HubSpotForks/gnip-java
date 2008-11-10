package com.gnipcentral.client;

import java.net.URL;

public class Config {

    private static final String DEFAULT_URL = "https://prod.gnipcentral.com";

    private boolean useGzip = false;
    private String username;
    private String password;
    private String gnipServer;
    private int readTimeoutMillis = 2 * 1000;

    public Config(String username, String password) {
        this.username = username;
        this.password = password;
        gnipServer = DEFAULT_URL;
    }

    public Config(String username, String password, URL gnipServer) {
        this.username = username;
        this.password = password;
        this.gnipServer = gnipServer.toString();
    }

    public boolean isUseGzip() {
        return useGzip;
    }

    public void setUseGzip(boolean useGzip) {
        this.useGzip = useGzip;
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

    public int getReadTimeout() {
        return readTimeoutMillis;
    }

    public void setReadTimeout(int readTimeoutMillis) {
        this.readTimeoutMillis = readTimeoutMillis;
    }
}
