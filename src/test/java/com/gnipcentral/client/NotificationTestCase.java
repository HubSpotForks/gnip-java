package com.gnipcentral.client;

import com.gnipcentral.client.resource.Filter;
import com.gnipcentral.client.resource.Activities;
import com.gnipcentral.client.resource.Activity;

import java.util.List;

import org.joda.time.DateTime;

public class NotificationTestCase extends GnipTestCase {

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

    public void testGetNotificationForFilterFromGnip() throws Exception {
        assertFalse(notificationFilterToCreate.isFullData());
        try {
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
        }
        finally {
            gnipConnection.delete(localPublisher, notificationFilterToCreate);
        }
    }

    public void testGetNotificationForFilterFromGnipWithTime() throws Exception {
        assertFalse(notificationFilterToCreate.isFullData());
        try {
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
        }
        finally {
            gnipConnection.delete(localPublisher, notificationFilterToCreate);
        }
    }    
}
