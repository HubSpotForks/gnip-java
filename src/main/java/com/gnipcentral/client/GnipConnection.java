package com.gnipcentral.client;

import com.gnipcentral.client.resource.*;
import com.gnipcentral.client.util.HTTPConnection;
import com.gnipcentral.client.util.LoggerFactory;
import com.gnipcentral.client.util.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.xml.sax.InputSource;

import java.io.*;
import java.net.URLEncoder;
import java.util.zip.GZIPOutputStream;
import java.util.Date;
import javax.xml.bind.JAXBException;

public class GnipConnection {

    private static final long BUCKET_SIZE = 60 * 1000;
    private static final Logger LOG = LoggerFactory.getInstance();
    
    private final HTTPConnection connection;
    private final Config config;

    public GnipConnection(Config config) {
        this.connection = new HTTPConnection(config);
        this.config = config;
    }

    public void create(Publisher publisher) throws GnipException {
        try {
            byte[] data = getData(publisher);
            connection.doPost(getPublishersURL(), data);
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred creating Publisher", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred creating Publisher", e);
        }
    }

    public void create(Publisher publisher, Filter filter) throws GnipException {
        try {
            byte[] data = getData(filter);
            connection.doPost(getFilterCreateURL(publisher.getName()), data);
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred creating Filter", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred creating Filter", e);
        }

    }

    public Publishers getPublishers() throws GnipException {
        try {
            InputStream response = connection.doGet(getPublishersURL() + ".xml");
            return Translator.parsePublishers(new InputSource(response));
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred getting Publishers", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred getting Publishers", e);
        }
    }

    public Publisher getPublisher(String publisherName) throws GnipException {
        try {
            InputStream response = connection.doGet(getPublisherXmlURL(publisherName));
            return Translator.parsePublisher(new InputSource(response));
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred getting Publisher", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred getting Publisher", e);
        }

    }

    public Filter getFilter(String publisherName, String filterName) throws GnipException {
        try {
            InputStream response = connection.doGet(getFilterURL(publisherName, filterName));
            return Translator.parseFilter(new InputSource(response));
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred getting Filter", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred getting Filter", e);
        }
    }

    public void update(Publisher publisher, Filter filter) throws GnipException {
        try {
            byte[] data = getData(filter);
            connection.doPut(getFilterURL(publisher, filter), data);
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred updating Filter", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred updating Filter", e);
        }
    }

    public void update(Publisher publisher, Filter filter, Rule rule) throws GnipException {
        try {
            byte[] data = getData(rule);
            connection.doPost(getRulesURL(publisher.getName(), filter.getName()), data);
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred updating Rule", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred updating Rule", e);
        }
    }

    public void delete(Publisher publisher, Filter filter) throws GnipException {
        try {
            connection.doDelete(getFilterURL(publisher, filter));
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred deleting Filter", e);
        }
    }

    public void delete(Publisher publisher, Filter filter, Rule rule) throws GnipException {
        try {
            connection.doDelete(getRulesDeleteURL(publisher, filter, rule));
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred deleting Rule", e);
        }
    }

    public void publish(Publisher publisher, Activities activities) throws GnipException {
        if (activities == null || activities.getActivities().isEmpty())
            return;

        try {
            byte[] data = getData(activities);
            connection.doPost(getActivitiesPublishURL(publisher), data);
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred publishing activities", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred publishing activities", e);
        }
    }

    public Activities getActivities(Publisher publisher) throws GnipException {
        try {
            InputStream inputStream = connection.doGet(getActivityURL(publisher, false));
            return Translator.parseActivities(new InputSource(inputStream));
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred getting activities", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred getting activities", e);
        }
    }

    public Activities getActivities(Publisher publisher, DateTime date) throws GnipException {
        try {
            InputStream inputStream = connection.doGet(getActivityURL(publisher, false, date));
            return Translator.parseActivities(new InputSource(inputStream));
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred getting activities", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred getting activities", e);
        }
    }

    public Activities getNotifications(Publisher publisher) throws GnipException {
        try {
            InputStream inputStream = connection.doGet(getActivityURL(publisher, true));
            return Translator.parseActivities(new InputSource(inputStream));
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred getting activities", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred getting activities", e);
        }
    }

    public Activities getNotifications(Publisher publisher, DateTime date) throws GnipException {
        try {
            InputStream inputStream = connection.doGet(getActivityURL(publisher, true, date));
            return Translator.parseActivities(new InputSource(inputStream));
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred getting activities", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred getting activities", e);
        }
    }

    public Activities getActivities(Publisher publisher, Filter filter) throws GnipException {
        try {
            InputStream inputStream = connection.doGet(getActivitiesURL(publisher, filter));
            return Translator.parseActivities(new InputSource(inputStream));
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred getting activities for Filter", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred getting activities for Filter", e);
        }
    }

    public Activities getActivities(Publisher publisher, Filter filter, DateTime date) throws GnipException {
        try {
            InputStream inputStream = connection.doGet(getActivitiesURL(publisher, filter, date));
            return Translator.parseActivities(new InputSource(inputStream));
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred getting activities for Filter", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred getting activities for Filter", e);
        }
    }

    private byte[] getData(Object resource) throws JAXBException, IOException {
        LOG.log("Starting data marshalling at %s\n", (new Date()).toString());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        OutputStream stream;
        if (config.isUseGzip()) {
            stream = new GZIPOutputStream(byteArrayOutputStream);
        } else {
            stream = byteArrayOutputStream;
        }
        Translator.marshall(resource, stream);
        stream.flush();
        if (config.isUseGzip()) {
            //noinspection ConstantConditions
            ((GZIPOutputStream) stream).finish();
        }
        byte[] bytes = byteArrayOutputStream.toByteArray();
        LOG.log("Finished data marshalling at %s\n", (new Date()).toString());
        return bytes; 
    }

    private String getPublishersURL() {
        return config.getGnipServer() + "/publishers";
    }

    private String getPublisherXmlURL(String publisherName) {
        return getPublishersURL() + "/" + publisherName + ".xml";
    }

    private String getPublisherURL(Publisher publisher) {
        return getPublishersURL() + "/" + publisher.getName();
    }

    private String getFilterCreateURL(String publisherName) {
        return getPublishersURL() + "/" + publisherName + "/filters";
    }

    private String getFilterURL(Publisher publisher, Filter filter) {
        return getFilterURL(publisher.getName(), filter.getName());
    }

    private String getFilterURL(String publisherName, String filterName) {
        return getPublishersURL() + "/" + publisherName + "/filters/" + filterName + ".xml";
    }

    private String getRulesURL(String publisherName, String filterName) {
        return getPublishersURL() + "/" + publisherName + "/filters/" + filterName + "/rules";
    }

    private String getRulesDeleteURL(Publisher publisher, Filter filter, Rule rule) throws UnsupportedEncodingException {
        return getRulesURL(publisher.getName(), filter.getName()) + "?type=" + encode(rule.getType().toString()) + "&value=" + encode(rule.getValue());
    }

    private String getActivitiesPublishURL(Publisher publisher) {
        return getPublisherURL(publisher) + "/activity";
    }

    private String getActivityURL(Publisher publisher, boolean isNotification) {
        return isNotification ?
            getPublisherURL(publisher) + "/notification/current.xml" :
            getPublisherURL(publisher) + "/activity/current.xml";
    }

    private String getActivityURL(Publisher publisher, boolean isNotification, DateTime date) {
        return isNotification ?
            getPublisherURL(publisher) + "/notification/" + getDateString(date) + ".xml" :
            getPublisherURL(publisher) + "/activity/" + getDateString(date) + ".xml";                
    }

    private String getActivitiesURL(Publisher publisher, Filter filter) {
        if(filter.isFullData())
            return getFilterCreateURL(publisher.getName()) + "/" + filter.getName() + "/activity/current.xml";
        else return getFilterCreateURL(publisher.getName()) + "/" + filter.getName() + "/notification/current.xml";
    }

    private String getActivitiesURL(Publisher publisher, Filter filter, DateTime date) {
        if(filter.isFullData())
            return getFilterCreateURL(publisher.getName()) + "/" + filter.getName() + "/activity/" + getDateString(date) + ".xml";
        else return getFilterCreateURL(publisher.getName()) + "/" + filter.getName() + "/notification/" + getDateString(date) + ".xml";
    }

    private String encode(String string) throws UnsupportedEncodingException {
        return URLEncoder.encode(string, "UTF-8");
    }

    private String getDateString(DateTime date) {
        DateTime flooredDate = getBucketFloor(date.toDateTime(DateTimeZone.UTC));
        return flooredDate.toString("yyyyMMddHHmm");
    }

    private static DateTime getBucketFloor(DateTime date) {
        long floor = new Double(Math.floor(date.getMillis() / BUCKET_SIZE)).longValue();
        return new DateTime(floor * BUCKET_SIZE, DateTimeZone.UTC);
    }
}