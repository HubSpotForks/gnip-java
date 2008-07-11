package com.gnipcentral.client;

import java.net.URL;

public class Config {
    private boolean useGzip = true;
    private String username;
    private String password;
    private String gnipServer;

    public Config(String username, String password) {
        this.username = username;
        this.password = password;
        gnipServer = "https://s.gnipcentral.com";
    }

    public Config(String username, String password, URL gnipServer) {
        this.username = username;
        this.password = password;
        this.gnipServer = gnipServer.toString();
    }

    public void setUseGzip(boolean useGzip) {
        this.useGzip = useGzip;
    }

    public boolean useGzip() {
        return useGzip;
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
}
