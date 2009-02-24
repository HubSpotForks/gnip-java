package com.gnipcentral.client;

import com.gnipcentral.client.resource.*;
import com.gnipcentral.client.util.Logger;
import com.gnipcentral.client.util.LoggerFactory;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.URL;
import java.util.zip.GZIPOutputStream;

public class GnipTestCase extends BaseTestCase {

    protected final static TestConfig CONFIG;
    protected final static Logger LOG;

    static {
        CONFIG = TestConfig.getInstance(); 
        LOG = LoggerFactory.getInstance();
    }
    
    protected boolean setupLocalPublisher = true;
    protected boolean setupActivities = true;
    protected boolean setupFilters = true;

    protected Config config;
    protected long serverTimeCorrection;
    protected GnipConnection gnipConnection;
    protected Filter filterToCreate;
    protected Filter notificationFilterToCreate;
    protected Activities activities;
    protected Publisher localPublisher;
    protected Activity activity1;
    protected Activity activity2;
    protected Activity activity3;

    protected void setUp() throws Exception {
        super.setUp();
        
        LOG.log("========== Test setUp() start\n");
        LOG.log("Attempting to connect to Gnip at %s using username %s\n", CONFIG.getHost(), CONFIG.getUsername());
        config = new Config(CONFIG.getUsername(), CONFIG.getPassword(), new URL(CONFIG.getHost()));
        config.setReadTimeout(10000);
        gnipConnection = new GnipConnection(config);
        
        serverTimeCorrection = gnipConnection.getServerTimeDelta();
        if (serverTimeCorrection == 0L) {
            LOG.log("Note: connection server time delta may not be available\n");            
        }
        gnipConnection.setTimeCorrection(serverTimeCorrection);

        if (setupLocalPublisher) {
            String localPublisherId = CONFIG.getPublisher();
            localPublisher = gnipConnection.getPublisher(PublisherScope.MY, localPublisherId);
            if(localPublisher == null) {
                throw new AssertionError("No Publisher found with name " + localPublisherId + ".  Be sure " +
                    "to provide the name of a publisher you own in the test.properties file.");
            }
        }

        if (setupActivities) {
            activities = new Activities();
            activity1 = new Activity(new Actor("joe"), "update1");
            activities.add(activity1);
            activity2 = new Activity(new Actor("tom"), "update2");
            activities.add(activity2);
            activity3 = new Activity(new Actor("jane"), "update3");
            activities.add(activity3);
        }

        if (setupFilters) {
            filterToCreate = new Filter("tomFilter");
            filterToCreate.addRule(new Rule(RuleType.ACTOR, "tom"));

            notificationFilterToCreate = new Filter("janeFilter");
            notificationFilterToCreate.setFullData(false);
            notificationFilterToCreate.addRule(new Rule(RuleType.ACTOR, "jane"));

            // sleep to ensure that the filter is created
            // before starting to run the tests
            Thread.sleep(CONFIG.getIdleMillis());
        }

        LOG.log("Test setUp() end\n");
    }

    protected void tearDown() throws Exception {
        LOG.log("Test tearDown() start\n");
        // sleep between tests
        Thread.sleep(CONFIG.getIdleMillis());
        LOG.log("Test tearDown() end\n");        
        super.tearDown();
    }

    public void testEmpty() throws Exception {
        // this space intentionally left blank and ensures that the
        // setUp() and tearDown() method pair work when run by themselves
    }
    
    protected void waitForServerWorkToComplete() throws Exception {
        LOG.log("Waiting for server work to complete: %dms...\n", CONFIG.getIdleMillis());
        Thread.sleep(CONFIG.getIdleMillis());
        LOG.log("Continuing...\n");
    }

    protected void waitForPublishTimeBucketStart() throws Exception {
        // wait for publish time bucket to start to ensure
        // getting activities and notifications in the current
        // time bucket will succeed assuming interval between
        // publish and access is approximately the idle millis
        // or less.
        long currentServerTime = System.currentTimeMillis() + serverTimeCorrection;
        long timeRemainingInCurrentBucket = GnipConnection.BUCKET_SIZE_MILLIS - (currentServerTime % GnipConnection.BUCKET_SIZE_MILLIS);
        if (timeRemainingInCurrentBucket < CONFIG.getIdleMillis()*2) {
            long timeToWaitForNextBucketStart = timeRemainingInCurrentBucket+CONFIG.getIdleMillis();
            LOG.log("Waiting for server time bucket start: %dms...\n", timeToWaitForNextBucketStart);
            Thread.sleep(timeToWaitForNextBucketStart);
            LOG.log("Continuing...\n");
        }
    }

    protected String encodePayload(String string) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(baos);
        byte[] bytes = string.getBytes();
        gos.write(bytes, 0, bytes.length);
        gos.finish();
        bytes = Base64.encodeBase64(baos.toByteArray());
        String raw = new String(bytes);
        return raw;        
    }
}