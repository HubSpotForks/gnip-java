package com.gnipcentral.client;

import com.gnipcentral.client.resource.Publisher;
import com.gnipcentral.client.resource.RuleType;
import com.gnipcentral.client.resource.Publishers;

/**
 * 
 */
public class PublisherTest extends GnipTestCase {

    public void testGetPublisher() throws Exception {
        Publisher publisher = gnipConnection.getPublisher(localPublisher.getName());
        assertNotNull(publisher);
        assertEquals(localPublisher.getName(), publisher.getName());
    }

    public void testGetPublisherIncludesCapabilities() throws Exception {
        Publisher publisher = gnipConnection.getPublisher(localPublisher.getName());
        assertNotNull(publisher);
        assertTrue(localPublisher.hasSupportedRuleType(RuleType.ACTOR));
    }

    public void testGetPublishers() throws Exception {
        Publishers publishers = gnipConnection.getPublishers();
        assertNotNull(publishers);
        assertContains(localPublisher, publishers.getPublishers());
    }
}
