package com.gnipcentral.client;

import com.gnipcentral.client.resource.*;
import org.joda.time.DateTime;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

public class GnipConnectionTest extends BaseTestCase {

    private final static String GNIP_USER;
    private final static String GNIP_PASSWD;
    private final static String GNIP_HOST;

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
    }

    private GnipConnection gnipConnection;
    private Collection collectionToCreate;
    private Collection existingCollection;
    private Activities activities;
    private Publisher localPublisher;
    private Activity activity1;
    private Activity activity2;

    protected void setUp() throws Exception {
        super.setUp();
        
        System.out.println("Test setUp() start");
        System.out.printf("Attempting to connect to Gnip at %s using username %s\n", GNIP_HOST, GNIP_USER);
        Config config = new Config(GNIP_USER, GNIP_PASSWD, new URL(GNIP_HOST));
        gnipConnection = new GnipConnection(config);

        // create a publisher to use in these tests as the destination
        // for publishing test activities.  this publisher is owned by the
        // user that invokes the tests.
        String localPublisherId = "gnipjavaclienttest243641921b0e4687932a89e17fbc7d75";
        try {
            localPublisher = gnipConnection.getPublisher(localPublisherId);            
        }
        catch(IOException ignore) {}
        
        if(localPublisher == null) {
            localPublisher = new Publisher(localPublisherId);
            gnipConnection.create(localPublisher);
        }

        collectionToCreate = new Collection("tomsCollection");
        collectionToCreate.addUid(new Uid("tom", "nytimes"));

        // ensure that a Collection with this name doesn't exist.  if
        // it does, delete it.  This collection may exist when a previous
        // test run fails.  
        String existingCollectionId = "existingCollection";
        try {
            existingCollection = gnipConnection.getCollection(existingCollectionId);
            gnipConnection.delete(existingCollection);
            existingCollection = gnipConnection.getCollection(existingCollectionId);
        }
        catch(IOException ignore) {}        
        assertNull("Collection 'existingCollection' should not exist", existingCollection);
        existingCollection = new Collection(existingCollectionId);

        existingCollection.addUid(new Uid("joe", localPublisher.getName()));
        existingCollection.addUid(new Uid("jane", localPublisher.getName()));
        gnipConnection.create(existingCollection);

        activities = new Activities();
        activity1 = new Activity("joe", "update1");
        activities.add(activity1);
        activity2 = new Activity("tom", "update2");
        activities.add(activity2);

        System.out.println("Test setUp() end");
    }

    protected void tearDown() throws Exception {
        System.out.println("Test tearDown() start");
        gnipConnection.delete(existingCollection);
        System.out.println("Test tearDown() end");
        super.tearDown();
    }

    public void testEmpty() throws Exception {
        // this space intentionally left blank.

        // ensures that the setUp() and tearDown() method pair work when run by themselves
    }

    public void testPublishActivityToGnip() throws Exception {
        gnipConnection.publish(localPublisher, activities);
    }

    public void testGetActivityForPublisherFromGnip() throws Exception {
        gnipConnection.publish(localPublisher, activities);
        Activities activities = gnipConnection.getActivities(localPublisher);
        assertNotNull(activities);
        List<Activity> activitiesList = activities.getActivities();
        assertEquals(activity1.getType(), activitiesList.get(0).getType());
        assertEquals(activity2.getType(), activitiesList.get(1).getType());
    }

    public void testGetActivityForPublisherFromGnipWithTime() throws Exception {
        gnipConnection.publish(localPublisher, activities);
        Activities activities = gnipConnection.getActivities(localPublisher, new DateTime());
        assertNotNull(activities);
        List<Activity> activitiesList = activities.getActivities();
        assertEquals(activity1.getType(), activitiesList.get(0).getType());
        assertEquals(activity2.getType(), activitiesList.get(1).getType());
    }

/*
    // This test is disabled because Gnip v1.0 stopped setting the publisher
    // name on activities.  
    public void testGetActivityForCollectionFromGnip() throws Exception {
        gnipConnection.publish(localPublisher, activities);
        Activities activities = gnipConnection.getActivities(existingCollection);
        assertNotNull(activities);
        List<Activity> activitiesList = activities.getActivities();
        assertEquals(activity1.getType(), activitiesList.get(0).getType());
        assertEquals(localPublisher.getName(), activitiesList.get(0).getPublisherName());
    }
*/    

    public void testGetActivityForCollectionFromGnipWithTime() throws Exception {
        gnipConnection.publish(localPublisher, activities);
        Activities activities = gnipConnection.getActivities(existingCollection, new DateTime());
        assertNotNull(activities);
        List<Activity> activitiesList = activities.getActivities();
        assertEquals(activity1.getType(), activitiesList.get(0).getType());
    }

    public void xtestCreatePublisher() throws Exception {
        Publisher toCreate = new Publisher("tomPublisher");
        gnipConnection.create(toCreate);
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

    public void testGetCollection() throws Exception {
        Collection existing = gnipConnection.getCollection(existingCollection.getName());
        assertNotNull(existing);
        assertEquals(2, existing.getUids().size());
    }

    public void testCreateCollection() throws Exception {
        gnipConnection.create(collectionToCreate);
        Collection collection = gnipConnection.getCollection(collectionToCreate.getName());
        assertNotNull(collection);
        assertEquals("tomsCollection", collection.getName());

        List<Uid> list = collection.getUids();
        assertEquals(1, list.size());
        Uid uid = list.get(0);
        assertEquals("tom", uid.getName());
        assertEquals("nytimes", uid.getPublisherName());
        gnipConnection.delete(collectionToCreate);
    }

    public void testUpdateCollection() throws Exception {
        Collection existing = gnipConnection.getCollection(existingCollection.getName());
        assertEquals(2, existing.getUids().size());
        existing.removeUid(existing.getUids().get(0));
        assertEquals(1, existing.getUids().size());
        gnipConnection.update(existing);
        existing = gnipConnection.getCollection(existingCollection.getName());
        assertNotNull(existing);
        assertEquals(1, existing.getUids().size());
    }

    public void testAddUidToCollection() throws Exception {
        Collection existing = gnipConnection.getCollection(existingCollection.getName());
        assertEquals(2, existing.getUids().size());
        gnipConnection.create(existingCollection, new Uid("newUid", localPublisher.getName()));
        existing = gnipConnection.getCollection(existingCollection.getName());
        assertNotNull(existing);
        assertEquals(3, existing.getUids().size());
    }

    public void testRemoveUidFromCollection() throws Exception {
        Collection existing = gnipConnection.getCollection(existingCollection.getName());
        assertEquals(2, existing.getUids().size());
        gnipConnection.delete(existingCollection, existingCollection.getUids().get(0));
        existing = gnipConnection.getCollection(existingCollection.getName());
        assertNotNull(existing);
        assertEquals(1, existing.getUids().size());
    }

    public void testDeleteCollection() throws Exception {
        gnipConnection.create(collectionToCreate);
        Collection collection = gnipConnection.getCollection(collectionToCreate.getName());
        assertNotNull(collection);
        gnipConnection.delete(collection);
        try {
            gnipConnection.getCollection(collectionToCreate.getName());
            fail();
        } catch (IOException e) {
            //expected
        }
    }
}
