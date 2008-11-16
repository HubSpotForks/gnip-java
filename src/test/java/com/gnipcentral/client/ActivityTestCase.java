package com.gnipcentral.client;

import com.gnipcentral.client.resource.*;

import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;

/**
 * 
 */
public class ActivityTestCase extends GnipTestCase {

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

    public void testGetActivityForFilterFromGnip() throws Exception {
        Filter existingFilter = new Filter("existingFilter");
        try {
            existingFilter.addRule(new Rule(RuleType.ACTOR, "joe"));
            existingFilter.addRule(new Rule(RuleType.ACTOR, "jane"));
            gnipConnection.create(localPublisher, existingFilter);

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
            gnipConnection.delete(localPublisher, existingFilter);
        }
    }

    public void testGetActivityForFilterFromGnipWithTime() throws Exception {
        Filter existingFilter = new Filter("existingFilter");
        try {
            existingFilter.addRule(new Rule(RuleType.ACTOR, "joe"));
            existingFilter.addRule(new Rule(RuleType.ACTOR, "jane"));
            gnipConnection.create(localPublisher, existingFilter);

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
            gnipConnection.delete(localPublisher, existingFilter);
        }
    }    
}
