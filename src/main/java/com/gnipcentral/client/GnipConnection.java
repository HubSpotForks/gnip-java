package com.gnipcentral.client;

import com.gnipcentral.client.resource.*;
import com.gnipcentral.client.util.HTTPConnection;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.URLEncoder;
import java.util.zip.GZIPOutputStream;

public class GnipConnection {
    private final HTTPConnection connection;
    private final Config config;

    public GnipConnection(Config config) {
        this.connection = new HTTPConnection(config);
        this.config = config;
    }

    public void create(Publisher publisher) throws JAXBException, IOException {
        byte[] data = getData(publisher);
        connection.doPost(getPublishersURL(), data);
    }

    public void create(Collection collection) throws JAXBException, IOException {
        byte[] data = getData(collection);
        connection.doPost(getCollectionsURL(), data);
    }

    public void create(Collection collection, Uid uid) throws JAXBException, IOException {
        byte[] data = getData(uid);
        connection.doPost(getUidsURL(collection), data);
    }

    public Publisher getPublisher(String publisherName) throws JAXBException, IOException {
        InputStream response = connection.doGet(getPublisherURL(publisherName));
        return Translator.parsePublisher(new InputSource(response));
    }

    public Publishers getPublishers() throws JAXBException, IOException {
        InputStream response = connection.doGet(getPublishersURL() + ".xml");
        return Translator.parsePublishers(new InputSource(response));
    }

    public Collection getCollection(String collectionName) throws JAXBException, IOException {
        InputStream response = connection.doGet(getCollectionURL(collectionName));
        return Translator.parseCollection(new InputSource(response));
    }

    public void update(Collection collection) throws JAXBException, IOException {
        byte[] data = getData(collection);
        connection.doPut(getURL(collection), data);
    }

    public void delete(Collection collection) throws IOException {
        connection.doDelete(getURL(collection));
    }

    public void delete(Collection collection, Uid uid) throws JAXBException, IOException {
        connection.doDelete(getUidsURL(collection, uid));
    }

    public void publish(Publisher publisher, Activities activities) throws JAXBException, IOException {
        if (activities == null || activities.getActivities().isEmpty()) return;
        byte[] data = getData(activities);
        connection.doPost(getActivitiesPublishURL(publisher), data);
    }

    public Activities getActivities(Publisher publisher) throws JAXBException, IOException {
        InputStream inputStream = connection.doGet(getActivitiesURL(publisher));
        return Translator.parseActivities(new InputSource(inputStream));
    }

    public Activities getActivities(Publisher publisher, DateTime date) throws JAXBException, IOException {
        InputStream inputStream = connection.doGet(getActivitiesURL(publisher, date));
        return Translator.parseActivities(new InputSource(inputStream));
    }

    public Activities getActivities(Collection collection) throws JAXBException, IOException {
        InputStream inputStream = connection.doGet(getActivitiesURL(collection));
        return Translator.parseActivities(new InputSource(inputStream));
    }

    public Activities getActivities(Collection collection, DateTime date) throws JAXBException, IOException {
        InputStream inputStream = connection.doGet(getActivitiesURL(collection, date));
        return Translator.parseActivities(new InputSource(inputStream));
    }

    private byte[] getData(Object resource) throws JAXBException, IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OutputStream stream;
        if (config.useGzip()) {
            stream = new GZIPOutputStream(byteArrayOutputStream);
        } else {
            stream = byteArrayOutputStream;
        }
        Translator.marshall(resource, stream);
        stream.flush();
        if (config.useGzip()) {
            //noinspection ConstantConditions
            ((GZIPOutputStream) stream).finish();
        }
        return byteArrayOutputStream.toByteArray();
    }

    private String getPublishersURL() {
        return config.getGnipServer() + "/publishers";
    }

    private String getCollectionsURL() {
        return config.getGnipServer() + "/collections";
    }

    private String getUidsURL(Collection collection, Uid uid) throws UnsupportedEncodingException {
        return getCollectionsURL() + "/" + collection.getName() + "/uids?uid=" + encode(uid.getName()) + "&publisher.name=" + encode(uid.getPublisherName());
    }

    private String encode(String string) throws UnsupportedEncodingException {
        return URLEncoder.encode(string, "UTF-8");
    }

    private String getUidsURL(Collection collection) {
        return getCollectionsURL() + "/" + collection.getName() + "/uids";
    }

    private String getPublisherURL(String publisherName) {
        return getPublishersURL() + "/" + publisherName + ".xml";
    }

    private String getCollectionURL(String collectionName) {
        return getCollectionsURL() + "/" + collectionName + ".xml";
    }

    private String getURL(Publisher publisher) {
        return getPublishersURL() + "/" + publisher.getName();
    }

    private String getURL(Collection collection) {
        return getCollectionsURL() + "/" + collection.getName();
    }

    private String getActivitiesPublishURL(Publisher publisher) {
        return getURL(publisher) + "/activity";
    }

    private String getActivitiesURL(Publisher publisher) {
        return getURL(publisher) + "/activity/current.xml";
    }

    private String getActivitiesURL(Publisher publisher, DateTime date) {
        return getURL(publisher) + "/activity/" + getDateString(date) + ".xml";
    }

    private String getActivitiesURL(Collection collection) {
        return getURL(collection) + "/activity/current.xml";
    }

    private String getActivitiesURL(Collection collection, DateTime date) {
        return getURL(collection) + "/activity" + getDateString(date) + ".xml";
    }

    private String getDateString(DateTime date) {
        DateTime flooredDate = fiveMinuteFloor(date.toDateTime(DateTimeZone.UTC));
        return flooredDate.toString("yyyyMMddHHmm");
    }

    private static DateTime fiveMinuteFloor(DateTime date) {
        long floor = new Double(Math.floor(date.getMillis() / FIVE_MINUTES)).longValue();
        return new DateTime(floor * FIVE_MINUTES, DateTimeZone.UTC);
    }

    private static final long FIVE_MINUTES = 300000;
}