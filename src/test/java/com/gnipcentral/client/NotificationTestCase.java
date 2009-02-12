package com.gnipcentral.client;

import com.gnipcentral.client.resource.*;

import java.util.List;

import org.joda.time.DateTime;

public class NotificationTestCase extends GnipTestCase {

    public void testGetNotificationForPublisherFromGnip() throws Exception {
        waitForPublishTimeBucketStart();

        Result result = gnipConnection.publish(localPublisher, activities);
        assertTrue(result.isSuccess());

        waitForServerWorkToComplete();

        Activities activities = gnipConnection.getNotifications(localPublisher);
        assertNotNull(activities);
        List<Activity> activitiesList = activities.getActivities();
        assertNotNull(activitiesList);
        assertTrue(activitiesList.size() >= 2);
        int idx = activitiesList.size()-1;
        assertEquals(activity1.getAction(), activitiesList.get(idx-2).getAction());
        assertEquals(activity2.getAction(), activitiesList.get(idx-1).getAction());
    }

    public void testGetNotificationForPublisherFromGnipWithTime() throws Exception {
        waitForPublishTimeBucketStart();

        DateTime bucketTime = new DateTime();
        Result result = gnipConnection.publish(localPublisher, activities);
        assertTrue(result.isSuccess());

        waitForServerWorkToComplete();

        Activities activities = gnipConnection.getNotifications(localPublisher, bucketTime);
        assertNotNull(activities);
        List<Activity> activitiesList = activities.getActivities();
        assertNotNull(activitiesList);
        assertTrue(activitiesList.size() >= 2);
        int idx = activitiesList.size()-1;
        assertEquals(activity1.getAction(), activitiesList.get(idx-2).getAction());
        assertEquals(activity2.getAction(), activitiesList.get(idx-1).getAction());
    }

    public void testGetNotificationForFilterFromGnip() throws Exception {
        assertFalse(notificationFilterToCreate.isFullData());
        boolean failed = false;
        try {
            Result result = gnipConnection.create(localPublisher, notificationFilterToCreate);
            assertTrue(result.isSuccess());

            waitForServerWorkToComplete();

            Filter filter = gnipConnection.getFilter(localPublisher, notificationFilterToCreate.getName());
            assertNotNull(filter);

            waitForPublishTimeBucketStart();

            result = gnipConnection.publish(localPublisher, activities);
            assertTrue(result.isSuccess());

            waitForServerWorkToComplete();

            Activities activities = gnipConnection.getActivities(localPublisher, notificationFilterToCreate);
            assertNotNull(activities);
            List<Activity> activityList = activities.getActivities();
            assertNotNull(activityList);
            assertFalse(activityList.isEmpty());
            assertEquals(activity3.getAction(), activityList.get(0).getAction());
        }
        catch(Exception e) {
            failed = true;
            throw e;
        }
        finally {
            Result result = gnipConnection.delete(localPublisher, notificationFilterToCreate);
            assertTrue(result.isSuccess() || failed);
        }
    }

    public void testGetNotificationForFilterFromGnipWithTime() throws Exception {
        assertFalse(notificationFilterToCreate.isFullData());
        boolean failed = false;
        try {
            Result result = gnipConnection.create(localPublisher, notificationFilterToCreate);
            assertTrue(result.isSuccess());

            waitForServerWorkToComplete();

            Filter filter = gnipConnection.getFilter(localPublisher, notificationFilterToCreate.getName());
            assertNotNull(filter);

            waitForPublishTimeBucketStart();
            
            DateTime bucketTime = new DateTime();
            result = gnipConnection.publish(localPublisher, activities);
            assertTrue(result.isSuccess());

            waitForServerWorkToComplete();

            Activities activities = gnipConnection.getActivities(localPublisher, notificationFilterToCreate, bucketTime);
            assertNotNull(activities);
            List<Activity> activityList = activities.getActivities();
            assertNotNull(activityList);
            assertFalse(activityList.isEmpty());
            int idx = activityList.size()-1;
            assertEquals(activity3.getAction(), activityList.get(idx).getAction());
        }
        catch(Exception e) {
            failed = true;
            throw e;
        }
        finally {
            Result result = gnipConnection.delete(localPublisher, notificationFilterToCreate);
            assertTrue(result.isSuccess() || failed);
        }
    }    
}
