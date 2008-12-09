package com.gnipcentral.client;

import com.gnipcentral.client.resource.Publisher;
import com.gnipcentral.client.resource.RuleType;
import com.gnipcentral.client.resource.Publishers;

import java.util.HashSet;

/**
 * 
 */
public class PublisherTest extends GnipTestCase {

    public void testPublisherConstructors() throws Exception {
        {
            HashSet<RuleType> oneRuleType = new HashSet<RuleType>();
            oneRuleType.add(RuleType.ACTOR);
            
            Publisher publisher = new Publisher("foobar", oneRuleType);
            assertEquals("foobar", publisher.getName());
            assertEquals(oneRuleType.size(), publisher.getSupportedRuleTypes().size());
            assertTrue(publisher.hasSupportedRuleType(RuleType.ACTOR));
        }

        {
            Publisher publisher = new Publisher("foobar", RuleType.ACTOR, RuleType.TAG);
            assertEquals("foobar", publisher.getName());
            assertEquals(2, publisher.getSupportedRuleTypes().size());
            assertTrue(publisher.hasSupportedRuleType(RuleType.ACTOR));
            assertTrue(publisher.hasSupportedRuleType(RuleType.TAG));
        }
    }

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
