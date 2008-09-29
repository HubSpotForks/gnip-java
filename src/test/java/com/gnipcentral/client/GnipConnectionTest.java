package com.gnipcentral.client;

import com.gnipcentral.client.resource.*;
import com.gnipcentral.client.util.Logger;
import org.joda.time.DateTime;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;

public class GnipConnectionTest extends BaseTestCase {

    private final static String GNIP_USER;
    private final static String GNIP_PASSWD;
    private final static String GNIP_HOST;
    private final static String GNIP_PUBLISHER;
    private final static Logger LOG;

    static {
        Properties p = new Properties();
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("test.properties");
            assertNotNull("Unable to load properties file configuring username/password", is);
            p.load(is);
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to load properties file for test.", e);
        }

        GNIP_USER = p.getProperty("gnip.username");
        GNIP_PASSWD = p.getProperty("gnip.password");
        GNIP_HOST = p.getProperty("gnip.host");
        GNIP_PUBLISHER = p.getProperty("gnip.publisher");

        LOG = Logger.getInstance(new Logger.ConsoleLogger());
    }

    private GnipConnection gnipConnection;
    private Filter filterToCreate;
    private Filter existingFilter;
    private Filter notificationFilterToCreate;
    private Activities activities;
    private Publisher localPublisher;
    private Activity activity1;
    private Activity activity2;
    private Activity activity3;

    protected void setUp() throws Exception {
        super.setUp();
        
        LOG.log("Test setUp() start");
        LOG.log("Attempting to connect to Gnip at %s using username %s\n", GNIP_HOST, GNIP_USER);
        Config config = new Config(GNIP_USER, GNIP_PASSWD, new URL(GNIP_HOST));
        gnipConnection = new GnipConnection(config);

        String localPublisherId = GNIP_PUBLISHER;
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
        
        // ensure that a Filter with this type doesn't exist.  if
        // it does, delete it.  This collection may exist when a previous
        // test run fails.  
        String existingFilterId = "existingFilter";
        try {
            existingFilter = gnipConnection.getFilter(localPublisher.getName(), existingFilterId);
            if(existingFilter != null) {
                gnipConnection.delete(localPublisher, existingFilter);
                existingFilter = gnipConnection.getFilter(localPublisher.getName(), existingFilterId);
            }
        }
        catch(GnipException ignore) {}
        assertNull("Filter 'existingFilter' should not exist", existingFilter);

        existingFilter = new Filter(existingFilterId);
        existingFilter.addRule(new Rule(RuleType.ACTOR, "joe"));
        existingFilter.addRule(new Rule(RuleType.ACTOR, "jane"));
        gnipConnection.create(localPublisher, existingFilter);

        Thread.sleep(5000); // sleep to ensure that the filter is createdn
                            // before starting to run the tests

        LOG.log("Test setUp() end");
    }

    protected void tearDown() throws Exception {
        LOG.log("Test tearDown() start");
        gnipConnection.delete(localPublisher, existingFilter);
        LOG.log("Test tearDown() end");
        super.tearDown();
    }

    public void testEmpty() throws Exception {
        // this space intentionally left blank.

        // ensures that the setUp() and tearDown() method pair work when run by themselves
    }

    public void testGetPublisher() throws Exception {
        Publisher publisher = gnipConnection.getPublisher(localPublisher.getName());
        assertNotNull(publisher);
        assertEquals(localPublisher.getName(), publisher.getName());
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
        Activities activities = gnipConnection.getNotifications(localPublisher, new DateTime());
        assertNotNull(activities);
        List<Activity> activitiesList = activities.getActivities();
        int idx = activitiesList.size()-1;
        assertTrue(activitiesList.size() >= 2);
        assertEquals(activity1.getAction(), activitiesList.get(idx-2).getAction());
        assertEquals(activity2.getAction(), activitiesList.get(idx-1).getAction());
    }

    public void testGetActivityWithPayloadForPublisherFromGnip() throws Exception {
        Activities activities = new Activities();

        String body = "joe's update payload body";
        String raw = "joe's update body raw";
        Activity activity = new Activity("joe", "update", new Payload(body, raw, false));
        activities.add(activity);
        String encodedRaw = activity.getPayload().getRaw();

        gnipConnection.publish(localPublisher, activities);

        activities = gnipConnection.getActivities(localPublisher);
        assertNotNull(activities);
        List<Activity> activitiesList = activities.getActivities();
        int idx = activitiesList.size()-1;
        assertEquals(activity.getAction(), activitiesList.get(idx).getAction());
        assertEquals(body, activitiesList.get(idx).getPayload().getBody());
        assertEquals(encodedRaw, activitiesList.get(idx).getPayload().getRaw());
        assertEquals(raw, activitiesList.get(idx).getPayload().getDecodedRaw());
    }

    public void testGetFilter() throws Exception {
        Filter existing = gnipConnection.getFilter(localPublisher.getName(), existingFilter.getName());
        assertNotNull(existing);
        assertEquals(2, existing.getRules().size());
    }

    public void testCreateFilter() throws Exception {
        gnipConnection.create(localPublisher, filterToCreate);
        Filter filter = gnipConnection.getFilter(localPublisher.getName(), filterToCreate.getName());
        assertNotNull(filter);
        assertEquals(filterToCreate.getName(), filter.getName());

        List<Rule> list = filter.getRules();
        assertEquals(1, list.size());
        Rule rule = list.get(0);
        assertEquals(RuleType.ACTOR, rule.getType());
        assertEquals("tom", rule.getValue());
        gnipConnection.delete(localPublisher, filterToCreate);
    }

    public void testGetNotificationForFilterFromGnip() throws Exception {
        assertFalse(notificationFilterToCreate.isFullData());
        gnipConnection.create(localPublisher, notificationFilterToCreate);
        Filter filter = gnipConnection.getFilter(localPublisher.getName(), notificationFilterToCreate.getName());
        assertNotNull(filter);

        gnipConnection.publish(localPublisher, activities);
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
        Filter filter = gnipConnection.getFilter(localPublisher.getName(), notificationFilterToCreate.getName());
        assertNotNull(filter);

        gnipConnection.publish(localPublisher, activities);
        Activities activities = gnipConnection.getActivities(localPublisher, notificationFilterToCreate, new DateTime());
        assertNotNull(activities);
        List<Activity> activityList = activities.getActivities();
        assertEquals(activity3.getAction(), activityList.get(0).getAction());

        gnipConnection.delete(localPublisher, notificationFilterToCreate);
    }

    // todo
    public void testGetActivityForFilterFromGnip() throws Exception {
        gnipConnection.publish(localPublisher, activities);
        Activities activities = gnipConnection.getActivities(localPublisher, existingFilter);
        assertNotNull(activities);
        List<Activity> activitiesList = activities.getActivities();
        assertEquals(activity1.getAction(), activitiesList.get(0).getAction());
    }

    public void testGetActivityForFilterFromGnipWithTime() throws Exception {
        gnipConnection.publish(localPublisher, activities);
        Activities activities = gnipConnection.getActivities(localPublisher, existingFilter, new DateTime());
        assertNotNull(activities);
        List<Activity> activitiesList = activities.getActivities();
        assertEquals(activity1.getAction(), activitiesList.get(0).getAction());
    }
    
    public void testUpdateFilter() throws Exception {
        gnipConnection.create(localPublisher, filterToCreate);
        Filter filter = gnipConnection.getFilter(localPublisher.getName(), filterToCreate.getName());
        filter.addRule(new Rule(RuleType.ACTOR, "jojo"));
        gnipConnection.update(localPublisher, filter);
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
        Filter filter = gnipConnection.getFilter(localPublisher.getName(), filterToCreate.getName());
        assertNotNull(filter);
        gnipConnection.delete(localPublisher, filter);
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
        Rule ruleToAdd = new Rule(RuleType.ACTOR, "jojo");
        gnipConnection.update(localPublisher, filterToCreate, ruleToAdd);

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
        Rule ruleToDelete = new Rule(RuleType.ACTOR, "jojo");
        gnipConnection.update(localPublisher, filterToCreate, ruleToDelete);

        Filter updated = gnipConnection.getFilter(localPublisher.getName(), filterToCreate.getName());
        List<Rule> rules = updated.getRules();
        assertEquals(2, rules.size());

        int idx = "jojo".equals(rules.get(0).getValue()) ? 0 : 1;
        assertEquals(RuleType.ACTOR, rules.get(idx).getType());
        assertEquals("jojo", rules.get(idx).getValue());

        gnipConnection.delete(localPublisher, filterToCreate, ruleToDelete);

        updated = gnipConnection.getFilter(localPublisher.getName(), filterToCreate.getName());
        rules = updated.getRules();
        assertEquals(1, rules.size());

        assertEquals(RuleType.ACTOR, rules.get(0).getType());
        assertEquals("tom", rules.get(0).getValue());
        
        gnipConnection.delete(localPublisher, filterToCreate);
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