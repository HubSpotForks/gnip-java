package com.gnipcentral.client;

import java.net.URL;

/**
  A simple, standalone class with a <code>main</code> method that can be used to
  test whether {@link com.gnipcentral.client.util.Logger} implementations and the
  User-Agent haeder containing the library version number are picked up accurately.
  In order to test this in a "real" environment, the Maven project must be built and
  packaged.  Then, this test can be run with a command like:
  <pre>
  java -cp target/test-classes/:target/gnip-client-<version>.jar com.gnipcentral.client.StandaloneGnipTest
  </pre>
  Note, be sure to replace the &lt;version&gt; with the Maven version from pom.xml.
 */
public class StandaloneGnipTest {
    public static void main(String[] args) throws Exception {
        TestConfig testConfig = TestConfig.getInstance();
        Config config = new Config(testConfig.getUsername(), testConfig.getPassword(), new URL(testConfig.getHost()));
        GnipConnection gnip = new GnipConnection(config);
        gnip.getPublisher(testConfig.getPublisher());
    }
}