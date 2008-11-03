package com.gnipcentral.client;

import com.gnipcentral.client.resource.*;
import com.gnipcentral.client.util.Logger;
import com.gnipcentral.client.util.LoggerFactory;
import org.joda.time.DateTime;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class GnipConnectionTest extends BaseTestCase {

    private final static TestConfig CONFIG;
    private final static Logger LOG;

    static {
        CONFIG = TestConfig.getInstance(); 
        LOG = LoggerFactory.getInstance();
    }

    private GnipConnection gnipConnection;
    private Filter filterToCreate;
    private Filter notificationFilterToCreate;
    private Activities activities;
    private Publisher localPublisher;
    private Activity activity1;
    private Activity activity2;
    private Activity activity3;

    protected void setUp() throws Exception {
        super.setUp();
        
        LOG.log("========== Test setUp() start\n");
        LOG.log("Attempting to connect to Gnip at %s using username %s\n", CONFIG.getHost(), CONFIG.getUsername());
        Config config = new Config(CONFIG.getUsername(), CONFIG.getPassword(), new URL(CONFIG.getHost()));
        gnipConnection = new GnipConnection(config);

        String localPublisherId = CONFIG.getPublisher();
        localPublisher = gnipConnection.getPublisher(localPublisherId);
        if(localPublisher == null) {
            throw new AssertionError("No Publisher found with name " + localPublisherId + ".  Be sure " +
                "to provide the name of a publisher you own in the test.properties file.");
        }

        activities = new Activities();
        activity1 = new Activity("joe", "update1");
        activities.add(activity1);
        activity2 = new Activity("tom", "update2");
        activities.add(activity2);
        activity3 = new Activity("jane", "update3");
        activities.add(activity3);

        filterToCreate = new Filter("tomFilter");
        filterToCreate.addRule(new Rule(RuleType.ACTOR, "tom"));

        notificationFilterToCreate = new Filter("janeFilter");
        notificationFilterToCreate.setFullData(false);
        notificationFilterToCreate.addRule(new Rule(RuleType.ACTOR, "jane"));

        Thread.sleep(CONFIG.getIdleSeconds()); // sleep to ensure that the filter is createdn
                                               // before starting to run the tests

        LOG.log("Test setUp() end\n");
    }

    protected void tearDown() throws Exception {
        LOG.log("Test tearDown() start\n");
        Thread.sleep(CONFIG.getIdleSeconds()); // sleep to ensure that the filter is created before starting to run the tests
        LOG.log("Test tearDown() end\n");        
        super.tearDown();
    }

    public void testEmpty() throws Exception {
        // this space intentionally left blank and ensures that the
        // setUp() and tearDown() method pair work when run by themselves
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

    public void testPublishActivityToGnip() throws Exception {
        gnipConnection.publish(localPublisher, activities);
    }

    public void testPayloadEncodeDecode() throws Exception {
        String body = "foo";
        String decodedRaw = "bar";
        String raw = encodePayload(decodedRaw);

        Payload payload = new Payload(body, decodedRaw, false);
        assertEquals(raw, payload.getRaw());
        assertEquals(decodedRaw, payload.getDecodedRaw());
    }

    public void testPublishActivityWithPayloadToGnip() throws Exception {
        Activities activities = new Activities();

        String payload = "joe's update payload";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(baos);
        byte[] bytes = payload.getBytes();
        gos.write(bytes, 0, bytes.length);
        gos.flush();
        bytes = Base64.encodeBase64(baos.toByteArray());

        Activity activity = new Activity("joe", "update", new Payload(payload, new String(bytes)));
        activities.add(activity);

        gnipConnection.publish(localPublisher, activities);
    }

    public void testGetNotificationForPublisherFromGnip() throws Exception {
        gnipConnection.publish(localPublisher, activities);

        waitForServerWorkToComplete();

        Activities activities = gnipConnection.getNotifications(localPublisher);
        assertNotNull(activities);
        List<Activity> activitiesList = activities.getActivities();
        int idx = activitiesList.size()-1;
        assertTrue(activitiesList.size() >= 2);
        assertEquals(activity1.getAction(), activitiesList.get(idx-2).getAction());
        assertEquals(activity2.getAction(), activitiesList.get(idx-1).getAction());
    }

    public void testGetNotificationForPublisherFromGnipWithTime() throws Exception {
        gnipConnection.publish(localPublisher, activities);

        //waitForServerWorkToComplete();
        Thread.sleep(4000);

        DateTime bucketTime = new DateTime();
        Activities activities = gnipConnection.getNotifications(localPublisher, bucketTime);
        assertNotNull(activities.getActivities());
        List<Activity> activitiesList = activities.getActivities();
        int idx = activitiesList.size()-1;
        assertTrue(activitiesList.size() >= 2);
        assertEquals(activity1.getAction(), activitiesList.get(idx-2).getAction());
        assertEquals(activity2.getAction(), activitiesList.get(idx-1).getAction());
    }

    /*
    // this test can only be run if your user has permission
    // to access full data from Gnip
    public void testGetActivityWithPayloadForPublisherFromGnip() throws Exception {
        Activities activities = new Activities();

        String body = "joe's update payload body";
        String raw = "joe's update body raw";
        Activity activity = new Activity("joe", "update", new Payload(body, raw, false));
        activities.add(activity);
        String encodedRaw = activity.getPayload().getRaw();

        gnipConnection.publish(localPublisher, activities);

        waitForServerWorkToComplete();                                            

        activities = gnipConnection.getActivities(localPublisher);
        assertNotNull(activities);
        List<Activity> activitiesList = activities.getActivities();
        int idx = activitiesList.size()-1;
        assertEquals(activity.getAction(), activitiesList.get(idx).getAction());
        assertEquals(body, activitiesList.get(idx).getPayload().getBody());
        assertEquals(encodedRaw, activitiesList.get(idx).getPayload().getRaw());
        assertEquals(raw, activitiesList.get(idx).getPayload().getDecodedRaw());
    }
    */

    public void testGetFilter() throws Exception {
        boolean createdFilter = false;
        Filter existingFilter = new Filter("existingFilter");
        try {
            existingFilter.addRule(new Rule(RuleType.ACTOR, "joe"));
            existingFilter.addRule(new Rule(RuleType.ACTOR, "jane"));
            gnipConnection.create(localPublisher, existingFilter);

            waitForServerWorkToComplete();

            Filter existing = gnipConnection.getFilter(localPublisher.getName(), existingFilter.getName());

            waitForServerWorkToComplete();

            assertNotNull(existing);
            assertEquals(2, existing.getRules().size());
        }
        catch(Exception e) {
            throw e;
        }
        finally {
            if(createdFilter) {
                gnipConnection.delete(localPublisher, existingFilter);
            }
        }
    }

    public void testCreateFilter() throws Exception {
        boolean filterCreated = false;
        try {
            gnipConnection.create(localPublisher, filterToCreate);
            filterCreated = true;

            waitForServerWorkToComplete();

            Filter filter = gnipConnection.getFilter(localPublisher.getName(), filterToCreate.getName());
            assertNotNull(filter);
            assertEquals(filterToCreate.getName(), filter.getName());
            List<Rule> list = filter.getRules();
            assertEquals(1, list.size());
            Rule rule = list.get(0);
            assertEquals(RuleType.ACTOR, rule.getType());
            assertEquals("tom", rule.getValue());
        }
        finally {
            if(filterCreated) {
                gnipConnection.delete(localPublisher, filterToCreate);
            }
        }
    }

    public void testGetNotificationForFilterFromGnip() throws Exception {
        assertFalse(notificationFilterToCreate.isFullData());
        gnipConnection.create(localPublisher, notificationFilterToCreate);

        waitForServerWorkToComplete();                        

        Filter filter = gnipConnection.getFilter(localPublisher.getName(), notificationFilterToCreate.getName());
        assertNotNull(filter);

        gnipConnection.publish(localPublisher, activities);

        waitForServerWorkToComplete();
        
        Activities activities = gnipConnection.getActivities(localPublisher, notificationFilterToCreate);
        assertNotNull(activities);
        List<Activity> activityList = activities.getActivities();
        assertTrue(activityList.size() > 0);
        assertEquals(activity3.getAction(), activityList.get(0).getAction());

        gnipConnection.delete(localPublisher, notificationFilterToCreate);
    }

    public void testGetNotificationForFilterFromGnipWithTime() throws Exception {
        assertFalse(notificationFilterToCreate.isFullData());
        gnipConnection.create(localPublisher, notificationFilterToCreate);

        waitForServerWorkToComplete();                        

        Filter filter = gnipConnection.getFilter(localPublisher.getName(), notificationFilterToCreate.getName());
        assertNotNull(filter);

        gnipConnection.publish(localPublisher, activities);

        waitForServerWorkToComplete();
        
        Activities activities = gnipConnection.getActivities(localPublisher, notificationFilterToCreate, new DateTime());
        assertNotNull(activities);
        List<Activity> activityList = activities.getActivities();
        assertTrue(activityList.size() > 0);
        int idx = activityList.size();
        assertEquals(activity3.getAction(), activityList.get(idx-1).getAction());

        gnipConnection.delete(localPublisher, notificationFilterToCreate);
    }

    public void testGetActivityForFilterFromGnip() throws Exception {
        boolean createdFilter = false;
        Filter existingFilter = new Filter("existingFilter");
        try {
            existingFilter.addRule(new Rule(RuleType.ACTOR, "joe"));
            existingFilter.addRule(new Rule(RuleType.ACTOR, "jane"));
            gnipConnection.create(localPublisher, existingFilter);
            createdFilter = true;

            waitForServerWorkToComplete();

            gnipConnection.publish(localPublisher, activities);

            waitForServerWorkToComplete();

            Activities activities = gnipConnection.getActivities(localPublisher, existingFilter);
            assertNotNull(activities);
            List<Activity> activityList = activities.getActivities();
            assertEquals(activity1.getAction(), activityList.get(0).getAction());
        }
        catch(Exception e) {
            throw e;
        }
        finally {
            if(createdFilter) {
                gnipConnection.delete(localPublisher, existingFilter);
            }
        }
    }

    public void testGetActivityForFilterFromGnipWithTime() throws Exception {
        boolean createdFilter = false;
        Filter existingFilter = new Filter("existingFilter");
        try {
            existingFilter.addRule(new Rule(RuleType.ACTOR, "joe"));
            existingFilter.addRule(new Rule(RuleType.ACTOR, "jane"));
            gnipConnection.create(localPublisher, existingFilter);
            createdFilter = true;

            waitForServerWorkToComplete();            

            gnipConnection.publish(localPublisher, activities);

            waitForServerWorkToComplete();                                    

            Activities activities = gnipConnection.getActivities(localPublisher, existingFilter, new DateTime());
            assertNotNull(activities);
            List<Activity> activityList = activities.getActivities();
            assertEquals(activity1.getAction(), activityList.get(0).getAction());
        }
        catch(Exception e) {
            throw e;
        }
        finally {
            if(createdFilter) {
                gnipConnection.delete(localPublisher, existingFilter);
            }
        }        
    }
    
    public void testUpdateFilter() throws Exception {
        gnipConnection.create(localPublisher, filterToCreate);

        waitForServerWorkToComplete();

        Filter filter = gnipConnection.getFilter(localPublisher.getName(), filterToCreate.getName());
        filter.addRule(new Rule(RuleType.ACTOR, "jojo"));
        gnipConnection.update(localPublisher, filter);

        waitForServerWorkToComplete();
        
        Filter updated = gnipConnection.getFilter(localPublisher.getName(), filterToCreate.getName());
        List<Rule> rules = updated.getRules();
        assertEquals(2, rules.size());

        int idx = ("jojo".equals(rules.get(0).getValue()) ? 0 : 1);

        assertEquals(RuleType.ACTOR, rules.get(idx).getType());
        assertEquals("jojo", rules.get(idx).getValue());

        gnipConnection.delete(localPublisher, filterToCreate);
    }

    public void testDeleteFilter() throws Exception {
        gnipConnection.create(localPublisher, filterToCreate);

        waitForServerWorkToComplete();

        Filter filter = gnipConnection.getFilter(localPublisher.getName(), filterToCreate.getName());
        assertNotNull(filter);
        gnipConnection.delete(localPublisher, filter);

        waitForServerWorkToComplete();

        try {
            gnipConnection.getFilter(localPublisher.getName(), filterToCreate.getName());
            fail();
        } catch (GnipException e) {
            //expected
        }
    }

    public void testNoSuchFilter() throws Exception {
        try {
            gnipConnection.getFilter(localPublisher.getName(), "nosuchfilter");
            assertFalse("Should have received exception for missing filter", true);
        }
        catch(GnipException e) {
            // expected
        }
    }

    public void testAddRuleToFilter() throws Exception {
        gnipConnection.create(localPublisher, filterToCreate);

        waitForServerWorkToComplete();

        Rule ruleToAdd = new Rule(RuleType.ACTOR, "jojo");
        gnipConnection.update(localPublisher, filterToCreate, ruleToAdd);

        waitForServerWorkToComplete();

        Filter updated = gnipConnection.getFilter(localPublisher.getName(), filterToCreate.getName());
        List<Rule> rules = updated.getRules();
        assertEquals(2, rules.size());

        int idx = "jojo".equals(rules.get(0).getValue()) ? 0 : 1;

        assertEquals(RuleType.ACTOR, rules.get(idx).getType());
        assertEquals("jojo", rules.get(idx).getValue());

        gnipConnection.delete(localPublisher, filterToCreate);
    }

    public void testDeleteRuleFromFilter() throws Exception {
        gnipConnection.create(localPublisher, filterToCreate);

        waitForServerWorkToComplete();

        Rule ruleToDelete = new Rule(RuleType.ACTOR, "jojo");
        gnipConnection.update(localPublisher, filterToCreate, ruleToDelete);

        waitForServerWorkToComplete();

        Filter updated = gnipConnection.getFilter(localPublisher.getName(), filterToCreate.getName());
        List<Rule> rules = updated.getRules();
        assertEquals(2, rules.size());

        int idx = "jojo".equals(rules.get(0).getValue()) ? 0 : 1;
        assertEquals(RuleType.ACTOR, rules.get(idx).getType());
        assertEquals("jojo", rules.get(idx).getValue());

        gnipConnection.delete(localPublisher, filterToCreate, ruleToDelete);

        waitForServerWorkToComplete();

        updated = gnipConnection.getFilter(localPublisher.getName(), filterToCreate.getName());
        rules = updated.getRules();
        assertEquals(1, rules.size());

        assertEquals(RuleType.ACTOR, rules.get(0).getType());
        assertEquals("tom", rules.get(0).getValue());
        
        gnipConnection.delete(localPublisher, filterToCreate);
    }

    private void waitForServerWorkToComplete() throws Exception {
        Thread.sleep(CONFIG.getIdleSeconds());
    }

    private String encodePayload(String string) throws Exception {
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