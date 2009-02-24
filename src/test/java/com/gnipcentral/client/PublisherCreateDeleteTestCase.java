package com.gnipcentral.client;

import com.gnipcentral.client.resource.*;

public class PublisherCreateDeleteTestCase extends GnipTestCase
{
    protected void setUp() throws Exception {
        // suppress setup of publisher, activities, and filters
        setupLocalPublisher = false;
        setupActivities = false;
        setupFilters = false;
        // setup test case
        super.setUp();
        
        // ensure transient publishers are deleted
        try {
            gnipConnection.delete(PublisherScope.MY, "test-create-delete-publisher");
            waitForServerWorkToComplete();
        } catch (GnipException e) {
        }
    }
    
    protected void tearDown() throws Exception {
        // ensure transient publishers are deleted
        try {
            gnipConnection.delete(PublisherScope.MY, "test-create-delete-publisher");
        } catch (GnipException e) {
        }

        // tear down test case
        super.tearDown();
    }

    public void testCreateDeletePublisher() throws Exception {
        // test non-existing publisher exception
        try {
            gnipConnection.getPublisher(PublisherScope.MY, "test-create-delete-publisher");
            fail("Unexpected successful access to publisher that should not exist");
        } catch (GnipException e) {
        }
        
        // dynamically create new publisher
        Publisher newPublisher = new Publisher(PublisherScope.MY, "test-create-delete-publisher");
        newPublisher.addSupportedRuleType(RuleType.ACTOR);
        newPublisher.addSupportedRuleType(RuleType.REGARDING);
        newPublisher.addSupportedRuleType(RuleType.SOURCE);
        newPublisher.addSupportedRuleType(RuleType.TAG);
        newPublisher.addSupportedRuleType(RuleType.TO);
        try {
            gnipConnection.create(newPublisher);
            waitForServerWorkToComplete();
        } catch (GnipException e) {
            LOG.log("Unexpected create() test exception: %s\n", e.toString());
            fail(e.toString());
        }

        // test existing publisher
        try {
            newPublisher = gnipConnection.getPublisher(PublisherScope.MY, "test-create-delete-publisher");
            assertNotNull(newPublisher);
        } catch (GnipException e) {
            LOG.log("Unexpected getPublisher() test exception: %s\n", e.toString());
            fail(e.toString());
        }

        // delete new publisher
        try {
            gnipConnection.delete(newPublisher);
            waitForServerWorkToComplete();
        } catch (GnipException e) {
            LOG.log("Unexpected delete() test exception: %s\n", e.toString());
            fail(e.toString());
        }

        // test non-existing publisher exception
        try {
            gnipConnection.getPublisher(PublisherScope.MY, "test-create-delete-publisher");
            fail("Unexpected successful access to publisher that should have been deleted");
        } catch (GnipException e) {
        }
    }
}
