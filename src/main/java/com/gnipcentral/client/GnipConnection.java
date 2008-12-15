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

/**
 * Represents a client connection to a Gnip service.  It encapsulates all protocol
 * level interactions with a Gnip service and provides a higher-level abstraction for writing data to and reading
 * data from Gnip.
 * <br/>
 * <br/>
 * Consumers of data will be specifically interested in:
 * <ul>
 * <li><b>Reading notifications for a Publisher</b></li>
 * <li><b>Creating a Filter</b></li>
 * <li><b>Reading activities for a fulldata Filter</b></li>
 * <li><b>Reading notifications for a non-fulldata Filter</b></li> 
 * </ul>
 * <br/>
 * <br/>
 * Publishers of data will be specifically interested in:
 * <ul>
 * <li><b>Creating a Publisher</b></li>
 * <li><b>Publishing activities to the Publisher</b></li> 
 * </ul>
 */
public class GnipConnection {

    private static final long BUCKET_SIZE = 60 * 1000;
    private static final Logger LOG = LoggerFactory.getInstance();
    
    private final HTTPConnection connection;
    private final Config config;

    public GnipConnection(Config config) {
        this.connection = new HTTPConnection(config);
        this.config = config;
    }

    /**
     * Retrieves the {@link com.gnipcentral.client.Config configuration} used to establish and
     * authenticate a Gnip connection.
     * @return the config object
     */
    public Config getConfig() {
        return config;
    }

    /**
     * Retrieves the {@link com.gnipcentral.client.util.HTTPConnection HTTP connection} used to
     * send / receive HTTP requests with Gnip.
     * @return the HTTP connection
     */
    public HTTPConnection getHTTPConnection() {
        return connection;
    }

    /**
     * Create a new {@link Publisher}.
     * @param publisher
     * @throws GnipException if the Publisher already exists, if there were problems authenticating with a Gnip
     *                       server, or if another error occurred.  
     */
    public void create(Publisher publisher) throws GnipException {
        try {
            byte[] data = convertToBytes(publisher);
            connection.doPost(getPublishersUrl(), data);
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred creating Publisher", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred creating Publisher", e);
        }
    }

    /**
     * Create a {@link Filter} on a {@link Publisher}.
     *
     * @param publisher the publisher that owns the filter
     * @param filter the filter to create
     * @throws GnipException if the Filter already exists, if there were problems authenticating with a Gnip
     *                       server, or if another error occurred.
     */
    public void create(Publisher publisher, Filter filter) throws GnipException {
        try {
            byte[] data = convertToBytes(filter);
            connection.doPost(getFilterCreateUrl(publisher.getName()), data);
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred creating Filter", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred creating Filter", e);
        }

    }

    /**
     * Retrieves the list of Publishers avaialble from Gnip.
     * @return the list of {@link Publishers}
     * @throws GnipException if there were problems authenticating with the Gnip server or if another error occurred.
     */
    public Publishers getPublishers() throws GnipException {
        try {
            InputStream response = connection.doGet(getPublishersUrl() + ".xml");
            return Translator.parsePublishers(new InputSource(response));
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred getting Publishers", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred getting Publishers", e);
        }
    }

    /**
     * Retrieves a Publisher named <code>publisherName</code>.{@param publisherName}
     * @param publisherName name of the publisher to get
     * @return the {@link Publisher} if it exists
     * @throws GnipException if the publisher doesn't exist, if there were problems authentiating with the Gnip server,
     *                       or if another error occurred.                       
     */
    public Publisher getPublisher(String publisherName) throws GnipException {
        try {
            InputStream response = connection.doGet(getPublishersUrl(publisherName));
            return Translator.parsePublisher(new InputSource(response));
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred getting Publisher", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred getting Publisher", e);
        }

    }

    /**
     * Retrieves the Filter named {@link com.gnipcentral.client.resource.Filter#getName()} from the {@link Publisher}
     * named {@link com.gnipcentral.client.resource.Publisher#getName()}
     * @param publisher the publisher that owns the filter
     * @param filter the filter to get
     * @return the {@link Filter} if it exists
     * @throws GnipException if the {@link Filter} doesn't exist, if there were problems authenticating with the Gnip server,
     *                       or if another error occurred. 
     */
    public Filter getFilter(Publisher publisher, Filter filter) throws GnipException {
        if(publisher == null) {
            throw new IllegalArgumentException("Publisher cannot be null");
        }

        if(filter == null) {
            throw new IllegalArgumentException("Filter cannot be null");
        }

        return getFilter(publisher.getName(), filter.getName());
    }    

    public Filter getFilter(String publisherName, String filterName) throws GnipException {
        try {
            InputStream response = connection.doGet(getFilterUrl(publisherName, filterName));
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
            byte[] data = convertToBytes(filter);
            if(config.isTunnelOverPost()) {
                connection.doPost(tunnelEditOverPost(getFilterUrl(publisher.getName(), filter.getName())), data);
            }
            else {
                connection.doPut(getFilterUrl(publisher.getName(), filter.getName()), data);
            }
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
            byte[] data = convertToBytes(rule);
            connection.doPost(getRulesUrl(publisher.getName(), filter.getName()), data);
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
            if(config.isTunnelOverPost()) {
                connection.doPost(tunnelDeleteOverPost(getFilterUrl(publisher.getName(), filter.getName())), new byte[0]);
            }
            else {
                connection.doDelete(getFilterUrl(publisher.getName(), filter.getName()));
            }
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred deleting Filter", e);
        }
    }

    public void delete(Publisher publisher, Filter filter, Rule rule) throws GnipException {
        try {
            String url = getRulesDeleteUrl(publisher, filter, rule);
            if(config.isTunnelOverPost()) {
                connection.doPost(url, null);
            }
            else {
                connection.doDelete(url);
            }
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred deleting Rule", e);
        }
    }

    public void publish(Publisher publisher, Activities activities) throws GnipException {
        if (activities == null || activities.getActivities().isEmpty())
            return;

        try {
            byte[] data = convertToBytes(activities);
            connection.doPost(getActivitiesPublishUrl(publisher), data);
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
            InputStream inputStream = connection.doGet(getActivityUrl(publisher, false));
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
            InputStream inputStream = connection.doGet(getActivityUrl(publisher, false, date));
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
            InputStream inputStream = connection.doGet(getActivityUrl(publisher, true));
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
            InputStream inputStream = connection.doGet(getActivityUrl(publisher, true, date));
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
            InputStream inputStream = connection.doGet(getActivitiesUrl(publisher, filter));
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
            InputStream inputStream = connection.doGet(getActivitiesUrl(publisher, filter, date));
            return Translator.parseActivities(new InputSource(inputStream));
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred getting activities for Filter", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred getting activities for Filter", e);
        }
    }

    /**
     * Convert a Gnip model object such as a {@link Publisher} or a {@link Filter} to XML and then
     * into a byte array.  If the {@link Config} is configured to use compression, the byte array will
     * be gzipp'ed.
     * 
     * @param resource the resource to convert
     * @return a byte array that represents the serialized XML document and may be gzipp'ed
     * @throws JAXBException when the document fails to marshal into XML
     * @throws IOException when an exception occurs writing data to bytes
     */
    public byte[] convertToBytes(Resource resource) throws JAXBException, IOException {
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

    private String getPublishersUrl() {
        return config.getGnipServer() + "/publishers";
    }

    private String getPublishersUrl(String publisherName) {
        return getPublishersUrl() + "/" + publisherName + ".xml";
    }

    private String getPublisherUrl(String publisherName) {
        return getPublishersUrl() + "/" + publisherName;
    }

    private String getFilterCreateUrl(String publisherName) {
        return getPublishersUrl() + "/" + publisherName + "/filters";
    }

    private String getFilterUrl(String publisherName, String filterName) {
        return getPublishersUrl() + "/" + publisherName + "/filters/" + filterName + ".xml";
    }

    private String getRulesUrl(String publisherName, String filterName) {
        return getPublishersUrl() + "/" + publisherName + "/filters/" + filterName + "/rules";
    }

    private String getRulesDeleteUrl(Publisher publisher, Filter filter, Rule rule) throws UnsupportedEncodingException {
        String url = getRulesUrl(publisher.getName(), filter.getName());
        if(config.isTunnelOverPost()) {
            url = tunnelDeleteOverPost(url);
        }        
        return url + "?type=" + encodeUrlParameter(rule.getType().toString()) + "&value=" + encodeUrlParameter(rule.getValue());
    }

    private String getActivitiesPublishUrl(Publisher publisher) {
        return getPublisherUrl(publisher.getName()) + "/activity";
    }

    private String getActivityUrl(Publisher publisher, boolean isNotification) {
        String endpoint = isNotification ? "notification" : "activity";
        return getPublisherUrl(publisher.getName()) + "/" + endpoint + "/current.xml";
    }

    private String getActivityUrl(Publisher publisher, boolean isNotification, DateTime date) {
        String endpoint = isNotification ? "notification" : "activity";
        return getPublisherUrl(publisher.getName()) + "/" + endpoint + "/" + getDateString(date) + ".xml";
    }

    private String getActivitiesUrl(Publisher publisher, Filter filter) {
        String endpoint = filter.isFullData() ? "activity" : "notification";
        return getFilterCreateUrl(publisher.getName()) + "/" + filter.getName() + "/" + endpoint + "/current.xml";
    }

    private String getActivitiesUrl(Publisher publisher, Filter filter, DateTime date) {
        String endpoint = filter.isFullData() ? "activity" : "notification";
        return getFilterCreateUrl(publisher.getName()) + "/" + filter.getName() + "/" + endpoint + "/" + getDateString(date) + ".xml";
    }

    private String tunnelEditOverPost(String url) {
        return url + ";edit";
    }

    private String tunnelDeleteOverPost(String url) {
        return url + ";delete";
    }

    private String encodeUrlParameter(String string) throws UnsupportedEncodingException {
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