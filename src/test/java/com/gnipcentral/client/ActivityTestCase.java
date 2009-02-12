package com.gnipcentral.client;

import com.gnipcentral.client.resource.*;

import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.joda.time.DateTime;

/**
 * 
 */
public class ActivityTestCase extends GnipTestCase {

    public void testActivityEncodeDecode() throws Exception {
        String title = "title";
        String body = "foo";
        String decodedRaw = "bar";
        String raw = encodePayload(decodedRaw);
        List<MediaUrl> mediaUrls = Arrays.asList(new MediaUrl[]{
                new MediaUrl("http://www.media.gnip.com/video.mp4", "100", "100", "20", "video/mp4", "video"),
                new MediaUrl("http://www.media.gnip.com/image.jpg", "100", "100", null, "image/jpeg", "image")
        });
        String actorUid = "jj";
        String actorMetaUrl = "http://www.gnip.com/profiles/jojo";
        String activityID = "235156";
        String url = "http://www.gnip.com/article/235156";
        List<String> sources = Arrays.asList(new String[]{"source-publisher", "gnip-tabloid"});
        List<String> keywords = Arrays.asList(new String[]{"armor"});
        List<Place> places = Arrays.asList(new Place[]{
                new Place(new double[]{0.3435, 1.346}),
                new Place(new double[]{0.5790075}, 5280.0, 2, "building", "Gnip Office", "workplace")
        });
        List<GnipUrl> destinationUrls = Arrays.asList(new GnipUrl[]{
                new GnipUrl("http://www.gnip.com/article/345667", "http://www.gnip.com/article-info/345667")
        });
        List<GnipValue> tags = Arrays.asList(new GnipValue[]{
                new GnipValue("jousting", "http://www.gnip.com/tags/jousting"),
                new GnipValue("knight", "http://www.gnip.com/tags/knight"),
                new GnipValue("horse", "http://www.gnip.com/tags/hourse"),
        });
        List<GnipValue> tos = Arrays.asList(new GnipValue[]{
                new GnipValue("fred", "http://www.gnip.com/profiles/fred")
        });
        List<GnipUrl> regardingUrls = Arrays.asList(new GnipUrl[]{
                new GnipUrl("http://www.gnip.com/article/34456", "http://www.gnip.com/article-info/34456")
        });

        // Minimal Activity with Payload created with an encoded "raw"
        Payload payload = new Payload(title, body, decodedRaw);
        Activity activity = createActivityWithPayload(payload);

        assertNotNull(activity.getAt());
        assertNotNull(activity.getAction());
        assertNotNull(activity.getActors());
        assertEquals(1, activity.getActors().size());
        assertEquals(title, payload.getTitle());
        assertEquals(body, payload.getBody());
        assertNull(payload.getMediaUrls());
        assertEquals(raw, payload.getRaw());
        assertEquals(decodedRaw, payload.getDecodedRaw());

        Activity decoded = Translator.parseActivity(Translator.marshall(activity));
        assertEquals(decoded.getAt(), activity.getAt());
        assertEquals(decoded.getAction(), activity.getAction());
        assertNotNull(decoded.getActors());
        assertEquals(decoded.getActors().size(), activity.getActors().size());
        for (int i = 0, limit = activity.getActors().size(); (i < limit); i++) {
            assertTrue(decoded.getActors().contains(activity.getActors().get(i)));
        }
        assertEquals(decoded.getPayload().getTitle(), payload.getTitle());
        assertEquals(decoded.getPayload().getBody(), payload.getBody());
        assertNull(decoded.getPayload().getMediaUrls());
        assertEquals(decoded.getPayload().getRaw(), payload.getRaw());
        assertEquals(decoded.getPayload().getDecodedRaw(), payload.getDecodedRaw());
        assertEquals(decoded, activity);

        // Full Activity with full Payload created with an un-encoded "raw"
        payload = new Payload(title, body, mediaUrls, decodedRaw, false);
        activity = createActivityWithPayload(payload);
        activity.getActors().get(0).setUid(actorUid);
        activity.getActors().get(0).setMetaUrl(actorMetaUrl);
        activity.setActivityID(activityID);
        activity.setUrl(url);
        activity.setSources(sources);
        activity.setKeywords(keywords);
        activity.setPlaces(places);
        activity.setDestinationUrls(destinationUrls);
        activity.setTags(tags);
        activity.setTos(tos);
        activity.setRegardingUrls(regardingUrls);

        assertNotNull(activity.getAt());
        assertNotNull(activity.getAction());
        assertNotNull(activity.getActors());
        assertEquals(1, activity.getActors().size());
        assertEquals(actorUid, activity.getActors().get(0).getUid());
        assertEquals(actorMetaUrl, activity.getActors().get(0).getMetaUrl());
        assertEquals(activityID, activity.getActivityID());
        assertEquals(url, activity.getUrl());
        assertNotNull(activity.getSources());
        assertEquals(2, activity.getSources().size());
        assertTrue(activity.getSources().contains(sources.get(0)));        
        assertTrue(activity.getSources().contains(sources.get(1)));        
        assertNotNull(activity.getKeywords());
        assertEquals(1, activity.getKeywords().size());
        assertTrue(activity.getKeywords().contains(keywords.get(0)));
        assertNotNull(activity.getPlaces());
        assertEquals(2, activity.getPlaces().size());
        assertTrue(activity.getPlaces().contains(places.get(0)));
        assertTrue(activity.getPlaces().contains(places.get(1)));
        assertNotNull(activity.getDestinationUrls());
        assertEquals(1, activity.getDestinationUrls().size());
        assertTrue(activity.getDestinationUrls().contains(destinationUrls.get(0)));
        assertNotNull(activity.getTags());
        assertEquals(3, activity.getTags().size());
        assertTrue(activity.getTags().contains(tags.get(0)));
        assertTrue(activity.getTags().contains(tags.get(1)));
        assertTrue(activity.getTags().contains(tags.get(2)));
        assertNotNull(activity.getTos());
        assertEquals(1, activity.getTos().size());
        assertTrue(activity.getTos().contains(tos.get(0)));
        assertNotNull(activity.getRegardingUrls());
        assertEquals(1, activity.getRegardingUrls().size());
        assertTrue(activity.getRegardingUrls().contains(regardingUrls.get(0)));
        assertEquals(title, payload.getTitle());
        assertEquals(body, payload.getBody());
        assertNotNull(payload.getMediaUrls());
        assertEquals(2, payload.getMediaUrls().size());
        assertTrue(payload.getMediaUrls().contains(mediaUrls.get(0)));
        assertTrue(payload.getMediaUrls().contains(mediaUrls.get(1)));
        assertEquals(raw, payload.getRaw());
        assertEquals(decodedRaw, payload.getDecodedRaw());

        decoded = Translator.parseActivity(Translator.marshall(activity));
        assertEquals(decoded.getAt(), activity.getAt());
        assertEquals(decoded.getAction(), activity.getAction());
        assertNotNull(decoded.getActors());
        assertEquals(decoded.getActors().size(), activity.getActors().size());
        for (int i = 0, limit = activity.getActors().size(); (i < limit); i++) {
            assertTrue(decoded.getActors().contains(activity.getActors().get(i)));
        }
        assertEquals(decoded.getActivityID(), activity.getActivityID());
        assertEquals(decoded.getUrl(), activity.getUrl());
        
        assertNotNull(decoded.getSources());
        assertEquals(decoded.getSources().size(), activity.getSources().size());
        for (int i = 0, limit = activity.getSources().size(); (i < limit); i++) {
            assertTrue(decoded.getSources().contains(activity.getSources().get(i)));
        }
        assertNotNull(decoded.getKeywords());
        assertEquals(decoded.getKeywords().size(), activity.getKeywords().size());
        for (int i = 0, limit = activity.getKeywords().size(); (i < limit); i++) {
            assertTrue(decoded.getKeywords().contains(activity.getKeywords().get(i)));
        }
        assertNotNull(decoded.getPlaces());
        assertEquals(decoded.getPlaces().size(), activity.getPlaces().size());
        for (int i = 0, limit = activity.getPlaces().size(); (i < limit); i++) {
            assertTrue(decoded.getPlaces().contains(activity.getPlaces().get(i)));
        }
        assertNotNull(decoded.getDestinationUrls());
        assertEquals(decoded.getDestinationUrls().size(), activity.getDestinationUrls().size());
        for (int i = 0, limit = activity.getDestinationUrls().size(); (i < limit); i++) {
            assertTrue(decoded.getDestinationUrls().contains(activity.getDestinationUrls().get(i)));
        }
        assertNotNull(decoded.getTags());
        assertEquals(decoded.getTags().size(), activity.getTags().size());
        for (int i = 0, limit = activity.getTags().size(); (i < limit); i++) {
            assertTrue(decoded.getTags().contains(activity.getTags().get(i)));
        }
        assertNotNull(decoded.getTos());
        assertEquals(decoded.getTos().size(), activity.getTos().size());
        for (int i = 0, limit = activity.getTos().size(); (i < limit); i++) {
            assertTrue(decoded.getTos().contains(activity.getTos().get(i)));
        }
        assertNotNull(decoded.getRegardingUrls());
        assertEquals(decoded.getRegardingUrls().size(), activity.getRegardingUrls().size());
        for (int i = 0, limit = activity.getRegardingUrls().size(); (i < limit); i++) {
            assertTrue(decoded.getRegardingUrls().contains(activity.getRegardingUrls().get(i)));
        }
        assertEquals(decoded.getPayload().getTitle(), payload.getTitle());
        assertEquals(decoded.getPayload().getBody(), payload.getBody());
        assertNotNull(decoded.getPayload().getMediaUrls());
        assertEquals(decoded.getPayload().getMediaUrls().size(), payload.getMediaUrls().size());
        for (int i = 0, limit = payload.getMediaUrls().size(); (i < limit); i++) {
            assertTrue(decoded.getPayload().getMediaUrls().contains(payload.getMediaUrls().get(i)));
        }
        assertEquals(decoded.getPayload().getRaw(), payload.getRaw());
        assertEquals(decoded.getPayload().getDecodedRaw(), payload.getDecodedRaw());
        assertEquals(decoded, activity);

        // Minimal Activity with Payload created with an encoded "raw"
        payload = new Payload(title, body, raw, true);
        activity = createActivityWithPayload(payload);

        assertNotNull(activity.getAt());
        assertNotNull(activity.getAction());
        assertNotNull(activity.getActors());
        assertEquals(1, activity.getActors().size());
        assertEquals(title, payload.getTitle());
        assertEquals(body, payload.getBody());
        assertEquals(raw, payload.getRaw());
        assertEquals(decodedRaw, payload.getDecodedRaw());

        decoded = Translator.parseActivity(Translator.marshall(activity));
        assertEquals(decoded.getAt(), activity.getAt());
        assertEquals(decoded.getAction(), activity.getAction());
        assertNotNull(decoded.getActors());
        assertEquals(decoded.getActors().size(), activity.getActors().size());
        for (int i = 0, limit = activity.getActors().size(); (i < limit); i++) {
            assertTrue(decoded.getActors().contains(activity.getActors().get(i)));
        }
        assertEquals(decoded.getPayload().getTitle(), payload.getTitle());
        assertEquals(decoded.getPayload().getBody(), payload.getBody());
        assertEquals(decoded.getPayload().getRaw(), payload.getRaw());
        assertEquals(decoded.getPayload().getDecodedRaw(), payload.getDecodedRaw());
        assertEquals(decoded, activity);
    }

    public void testPublishActivityToGnip() throws Exception {
        Result result = gnipConnection.publish(localPublisher, activities);
        assertTrue(result.isSuccess());
    }

    public void testPublishActivityWithPayloadToGnip() throws Exception {
        Activities activities = new Activities();

        String actorName = "joe";
        String action = "update";
        String title = "joe's update";
        String body = "joe's update payload";
        List<MediaUrl> mediaUrls = Arrays.asList(new MediaUrl[]{
                new MediaUrl("http://www.my.com/video.mp4", "100", "100", "20", "video/mp4", "video"),
                new MediaUrl("http://www.my.com/audio.mpg", null, null, "15", "audio/mpeg", "audio")
        });
        String actorUid = "joe3456";
        String actorMetaUrl = "http://www.my.com/profiles/joe3456";
        String activityID = "UB40-34";
        String url = "http://www.my.com/email/UB40-34";
        List<String> sources = Arrays.asList(new String[]{"my-blog"});
        List<String> keywords = Arrays.asList(new String[]{"astrology", "knitting", "reggae"});
        List<Place> places = Arrays.asList(new Place[]{
                new Place(new double[]{0.34334, 0.3468, 0.9232}, 0.23, 7, "car", "My SUV", "commute")
        });
        List<GnipUrl> destinationUrls = Arrays.asList(new GnipUrl[]{
                new GnipUrl("http://www.my.com/mailbox/joe3456", "http://www.my.com/people/joe3456")
        });
        List<GnipValue> tags = Arrays.asList(new GnipValue[]{
                new GnipValue("tires", "http://www.my.com/dictionary/tires"),
                new GnipValue("garage"),
        });
        List<GnipValue> tos = Arrays.asList(new GnipValue[]{
                new GnipValue("jill102", "http://www.my.com/people/jill102")
        });
        List<GnipUrl> regardingUrls = Arrays.asList(new GnipUrl[]{
                new GnipUrl("http://www.my.com/email/UB40-33", "http://www.my.com/mail"),
                new GnipUrl("http://www.my.com/email/UB40")
        });

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gos = new GZIPOutputStream(baos);
        byte[] bytes = body.getBytes();
        gos.write(bytes, 0, bytes.length);
        gos.flush();
        bytes = Base64.encodeBase64(baos.toByteArray());

        Actor actor = new Actor(actorName, actorUid, actorMetaUrl);
        Payload payload = new Payload(title, body, mediaUrls, new String(bytes), true);
        Activity activity = new Activity(actor, action, payload);
        activity.setActivityID(activityID);
        activity.setUrl(url);
        activity.setSources(sources);
        activity.setKeywords(keywords);
        activity.setPlaces(places);
        activity.setDestinationUrls(destinationUrls);
        activity.setTags(tags);
        activity.setTos(tos);
        activity.setRegardingUrls(regardingUrls);
        activities.add(activity);

        Result result = gnipConnection.publish(localPublisher, activities);
        assertTrue(result.isSuccess());
    }

    public void testGetActivityWithPayloadForPublisherFromGnip() throws Exception {
        Activities activities = new Activities();

        String actorName = "joe";
        String action = "update";
        String title = "joe's update";
        String body = "joe's update payload body";
        String raw = "joe's update body raw";
        List<MediaUrl> mediaUrls = Arrays.asList(new MediaUrl[]{
                new MediaUrl("http://www.my.com/video.mp4", "100", "100", "20", "video/mp4", "video"),
                new MediaUrl("http://www.my.com/audio.mpg", null, null, "15", "audio/mpeg", "audio")
        });
        String actorUid = "joe3456";
        String actorMetaUrl = "http://www.my.com/profiles/joe3456";
        String activityID = "ZFH346SD2";
        String url = "http://www.my.com/article/ZFH346SD2";
        List<String> sources = Arrays.asList(new String[]{"my-publisher", "my-newsletter"});
        List<String> keywords = Arrays.asList(new String[]{"my-test"});
        List<Place> places = Arrays.asList(new Place[]{
                new Place(new double[]{20.34, 34.4567}, 42.0, 42, "home", "My Home", "home")
        });
        List<GnipUrl> destinationUrls = Arrays.asList(new GnipUrl[]{
                new GnipUrl("http://www.my.com/article/DH2381S", "http://www.my.com/meta-article/DH2381S")
        });
        List<GnipValue> tags = Arrays.asList(new GnipValue[]{
                new GnipValue("knee-replacement", "http://www.my.com/tags/knee-replacement"),
                new GnipValue("parsec", "http://www.my.com/tags/parsec")
        });
        List<GnipValue> tos = Arrays.asList(new GnipValue[]{
                new GnipValue("john356", "http://www.my.com/people/john356")
        });
        List<GnipUrl> regardingUrls = Arrays.asList(new GnipUrl[]{
                new GnipUrl("http://www.my.com/reference/0023", "http://www.my.com/meta-reference/0023")
        });

        
        Actor actor = new Actor(actorName, actorUid, actorMetaUrl);
        Payload payload = new Payload(title, body, mediaUrls, raw, false);
        Activity activity = new Activity(actor, action, payload);
        activity.setActivityID(activityID);
        activity.setUrl(url);
        activity.setSources(sources);
        activity.setKeywords(keywords);
        activity.setPlaces(places);
        activity.setDestinationUrls(destinationUrls);
        activity.setTags(tags);
        activity.setTos(tos);
        activity.setRegardingUrls(regardingUrls);
        activities.add(activity);

        String encodedRaw = activity.getPayload().getRaw();
        
        waitForPublishTimeBucketStart();

        Result result = gnipConnection.publish(localPublisher, activities);
        assertTrue(result.isSuccess());

        waitForServerWorkToComplete();

        activities = gnipConnection.getActivities(localPublisher);
        assertNotNull(activities);
        List<Activity> activitiesList = activities.getActivities();
        assertNotNull(activitiesList);
        assertFalse(activitiesList.isEmpty());
        Activity updateActivity = activitiesList.get(activitiesList.size()-1);
        
        assertEquals(activity, updateActivity);
        if (updateActivity.getPayload() != null) {
            assertEquals(title, updateActivity.getPayload().getTitle());
            assertEquals(body, updateActivity.getPayload().getBody());
            assertNotNull(updateActivity.getPayload().getMediaUrls());
            assertEquals(2, updateActivity.getPayload().getMediaUrls().size());
            assertTrue(updateActivity.getPayload().getMediaUrls().contains(mediaUrls.get(0)));
            assertTrue(updateActivity.getPayload().getMediaUrls().contains(mediaUrls.get(1)));
            assertEquals(encodedRaw, updateActivity.getPayload().getRaw());
            assertEquals(raw, updateActivity.getPayload().getDecodedRaw());            
        } else {
            LOG.log("Payload not available from Gnip server\n");
        }
    }

    public void testGetActivityForFilterFromGnip() throws Exception {
        Filter existingFilter = new Filter("existingFilter");
        boolean failed = false;
        try {
            existingFilter.addRule(new Rule(RuleType.ACTOR, "joe"));
            existingFilter.addRule(new Rule(RuleType.ACTOR, "jane"));
            Result result = gnipConnection.create(localPublisher, existingFilter);
            assertTrue(result.isSuccess());

            waitForServerWorkToComplete();

            waitForPublishTimeBucketStart();

            result = gnipConnection.publish(localPublisher, activities);
            assertTrue(result.isSuccess());

            waitForServerWorkToComplete();

            Activities activities = gnipConnection.getActivities(localPublisher, existingFilter);
            assertNotNull(activities);
            List<Activity> activityList = activities.getActivities();
            assertNotNull(activityList);
            assertFalse(activityList.isEmpty());
            assertEquals(activity1.getAction(), activityList.get(0).getAction());
        }
        catch(Exception e) {
            failed = true;
            throw e;
        }
        finally {
            Result result = gnipConnection.delete(localPublisher, existingFilter);
            assertTrue(result.isSuccess() || failed);
        }
    }

    public void testGetActivityForFilterFromGnipWithTime() throws Exception {
        Filter existingFilter = new Filter("existingFilter");
        boolean failed = false;
        try {
            existingFilter.addRule(new Rule(RuleType.ACTOR, "joe"));
            existingFilter.addRule(new Rule(RuleType.ACTOR, "jane"));
            Result result = gnipConnection.create(localPublisher, existingFilter);
            assertTrue(result.isSuccess());

            waitForServerWorkToComplete();

            waitForPublishTimeBucketStart();

            DateTime bucketTime = new DateTime();
            result = gnipConnection.publish(localPublisher, activities);
            assertTrue(result.isSuccess());

            waitForServerWorkToComplete();

            Activities activities = gnipConnection.getActivities(localPublisher, existingFilter, bucketTime);
            assertNotNull(activities);
            List<Activity> activityList = activities.getActivities();
            assertNotNull(activityList);
            assertFalse(activityList.isEmpty());
            assertEquals(activity1.getAction(), activityList.get(0).getAction());
        }
        catch(Exception e) {
            failed = true;
            throw e;
        }
        finally {
            Result result = gnipConnection.delete(localPublisher, existingFilter);
            assertTrue(result.isSuccess() || failed);
        }
    }

    private Activity createActivityWithPayload(Payload payload) {
        return new Activity(new Actor("jojo"), "some-simple-update", payload);
    }    
}
