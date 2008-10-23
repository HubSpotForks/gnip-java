package com.gnipcentral.client;

import com.gnipcentral.client.resource.Publisher;
import java.net.URL;

public class StandaloneGnipTest {
    public static void main(String[] args) throws Exception {
        TestConfig testConfig = TestConfig.getInstance();
        Config config = new Config(testConfig.getUsername(), testConfig.getPassword(), new URL(testConfig.getHost()));
        GnipConnection gnip = new GnipConnection(config);
        Publisher p = gnip.getPublisher(testConfig.getPublisher());
    }
}