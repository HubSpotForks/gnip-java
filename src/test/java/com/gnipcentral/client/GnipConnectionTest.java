package com.gnipcentral.client;

import com.gnipcentral.client.resource.*;
import org.joda.time.DateTime;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class GnipConnectionTest extends BaseTestCase {

    private final static String GNIP_USER = "@gnipcentral.com";
    private final static String GNIP_PASSWD = "";
    private final static String GNIP_HOST = "https://s.gnipcentral.com";

    private GnipConnection gnipConnection;
    private Collection collectionToCreate;
    private Collection existingCollection;
    private Activities activities;
    private Publisher existingPublisher;
    private Activity activity1;
    private Activity activity2;

    protected void setUp() throws Exception {
        super.setUp();
        Config config = new Config(GNIP_USER, GNIP_PASSWD, new URL(GNIP_HOST));
        gnipConnection = new GnipConnection(config);

        existingPublisher = new Publisher("nytimes");

        collectionToCreate = new Collection("tomsCollection");
        collectionToCreate.addUid(new Uid("tom", "nytimes"));

        existingCollection = new Collection("existingCollection");
        existingCollection.addUid(new Uid("joe", existingPublisher.getName()));
        existingCollection.addUid(new Uid("jane", existingPublisher.getName()));
        gnipConnection.create(existingCollection);

        activities = new Activities();
        activity1 = new Activity("joe", "update1");
        activities.addActivity(activity1);
        activity2 = new Activity("tom", "update2");
        activities.addActivity(activity2);
    }

    protected void tearDown() throws Exception {
        gnipConnection.delete(existingCollection);
        super.tearDown();
    }

    public void testPublishActivityToGnip() throws Exception {
        gnipConnection.publish(existingPublisher, activities);
    }

    public void testGetActivityForPublisherFromGnip() throws Exception {
        gnipConnection.publish(existingPublisher, activities);
        Activities activities = gnipConnection.getActivities(existingPublisher);
        assertNotNull(activities);
        List<Activity> activitiesList = activities.getActivities();
        assertEquals(activity1.getType(), activitiesList.get(0).getType());
        assertEquals(activity2.getType(), activitiesList.get(1).getType());
    }

    public void testGetActivityForPublisherFromGnipWithTime() throws Exception {
        gnipConnection.publish(existingPublisher, activities);
        Activities activities = gnipConnection.getActivities(existingPublisher, new DateTime());
        assertNotNull(activities);
        List<Activity> activitiesList = activities.getActivities();
        assertEquals(activity1.getType(), activitiesList.get(0).getType());
        assertEquals(activity2.getType(), activitiesList.get(1).getType());
    }

    public void testGetActivityForCollectionFromGnip() throws Exception {
        gnipConnection.publish(existingPublisher, activities);
        Activities activities = gnipConnection.getActivities(existingCollection);
        assertNotNull(activities);
        List<Activity> activitiesList = activities.getActivities();
        assertEquals(activity1.getType(), activitiesList.get(0).getType());
        assertEquals(existingPublisher.getName(), activitiesList.get(0).getPublisherName());
    }

    public void testGetActivityForCollectionFromGnipWithTime() throws Exception {
        gnipConnection.publish(existingPublisher, activities);
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
        Publisher publisher = gnipConnection.getPublisher(existingPublisher.getName());
        assertNotNull(publisher);
        assertEquals(existingPublisher.getName(), publisher.getName());
    }

    public void testGetPublishers() throws Exception {
        Publishers publishers = gnipConnection.getPublishers();
        assertNotNull(publishers);
        assertContains(existingPublisher, publishers.getPublishers());
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
        gnipConnection.create(existingCollection, new Uid("newUid", existingPublisher.getName()));
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
