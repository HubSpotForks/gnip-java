package com.gnipcentral.client;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;

final class TestConfig {

    private static TestConfig testConfig = null;

    static TestConfig getInstance() {
        if(testConfig != null)
            return testConfig;

        Properties p = new Properties();
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.properties");
            if(is == null) {                
                throw new RuntimeException("Unable to load properties file configuring username/password");
            }
            
            p.load(is);
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to load properties file for test.", e);
        }
        finally {
            try{if(is != null) is.close();}catch(IOException ignore) {}
        }

        testConfig = new TestConfig(
                p.getProperty("gnip.username"),
                p.getProperty("gnip.password"),
                p.getProperty("gnip.host"),
                p.getProperty("gnip.publisher"),
                new Integer(p.getProperty("gnip.idlesecs"))*1000);

        return testConfig;
    }

    private final String username;
    private final String password;
    private final String host;
    private final String publisher;
    private final Integer idleMillis;

    private TestConfig(String username, String password, String host, String publisher, Integer idleMillis) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.publisher = publisher;
        this.idleMillis = idleMillis;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public String getPublisher() {
        return publisher;
    }

    public Integer getIdleMillis() {
        return idleMillis;
    }
}
