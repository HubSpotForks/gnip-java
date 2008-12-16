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
 * Represents a client connection to a Gnip service.  This class encapsulates all protocol
 * level interactions with a Gnip service and provides a higher-level abstraction for writing
 * data to and reading data from Gnip.
 * <br/>
 * <br/>
 * Users interested in consuming data from Gnip will be specifically interested in reading
 * notifications from a Publisher and/or either notifications or activities from a Filter using:
 * <ul>
 * <li>{@link #getNotifications(com.gnipcentral.client.resource.Publisher)} or {@link #getNotifications(com.gnipcentral.client.resource.Publisher, org.joda.time.DateTime)}
 *     to read notifications from a Publisher
 * </li>
 * <li>{@link #create(com.gnipcentral.client.resource.Publisher, com.gnipcentral.client.resource.Filter)} to create a {@link Filter}</li>
 * <li>{@link #getActivities(com.gnipcentral.client.resource.Publisher, com.gnipcentral.client.resource.Filter)} or {@link #getActivities(com.gnipcentral.client.resource.Publisher, com.gnipcentral.client.resource.Filter, org.joda.time.DateTime)}
 *     to read activities from a Filter (or notifications if the Filter doesn't support full-data.
 * </li> 
 * <br/>
 * <br/>
 * Users interested in publishing data into Gnip will be specifically interested in creating Publishers
 * and sending activities to publishers using:
 * <ul>
 * <li>
 * {@link #create(com.gnipcentral.client.resource.Publisher)} to create a {@link Publisher}.
 * </li>
 * <li>
 * {@link #publish(com.gnipcentral.client.resource.Publisher, com.gnipcentral.client.resource.Activities)} to publish
 * activities into a {@link Publisher}
 * </li>
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
     * @param filter the filter to retrieve
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

    /**
     * Retrieves the Filter named {@param filterName} from the {@link Publisher} named {@param publisherName}.
     * @param publisherName the name of the publisher
     * @param filterName the filter to retrieve
     * @return the {@link Filter} if it exists
     * @throws GnipException if the {@link Filter} doesn't exist, if there were problems authenticating with the Gnip server,
     *                       or if another error occurred.
     */
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

    /**
     * Update the {@link Filter} associated with the {@link Publisher}.  As practiced in the
     * <a href="http://en.wikipedia.org/wiki/Representational_State_Transfer">REST style</a>, this call
     * updates the represetntation of the given Filter by replacing the one that already exists.  It
     * <i>does not</i> do a merge of the two Filter documents.
     * <br/>
     * <br/>
     * To do incremental updates of a {@link Filter}, use
     * {@link #update(com.gnipcentral.client.resource.Publisher, com.gnipcentral.client.resource.Filter, com.gnipcentral.client.resource.Rule)} or
     * {@link #update(com.gnipcentral.client.resource.Publisher, com.gnipcentral.client.resource.Filter, com.gnipcentral.client.resource.Rules)} to
     * add rules to an existing Filter. 
     *
     * @param publisher the publisher that owns the filter
     * @param filter the filter to update
     * @throws GnipException if the {@link Filter} doesn't exist, if there were problems authenticating with the Gnip server,
     *                       or if another error occurred. 
     */
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

    /**
     * Update the {@link Filter} by adding a <i>single</i> rule to it. 
     * @param publisher the publisher that owns the filter
     * @param filter the filter to update
     * @param rule the rule to add to the filter
     * @throws GnipException if the {@link Filter} doesn't exist, if there were problems authenticating with the Gnip server,
     *                       or if another error occurred.
     */
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

    /**
     * Update the {@link Filter} by bulk adding a {@link Rules} document containing many {@link Rule}s.  
     * @param publisher the publisher that owns the filter
     * @param filter the filter to update
     * @param rules the set of rules to add to the filter
     * @throws GnipException if the {@link Filter} doesn't exist, if there were problems authenticating with the Gnip server,
     *                       or if another error occurred.
     */
    public void update(Publisher publisher, Filter filter, Rules rules) throws GnipException {
        try {
            byte[] data = convertToBytes(rules);
            connection.doPost(getRulesUrl(publisher.getName(), filter.getName()), data);
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred updating Rule", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred updating Rule", e);
        }        
    }

    /**
     * Delete the filter {@link Filter} from the {@link Publisher}.
     * @param publisher the publisher from which to delete the filter
     * @param filter the filter to delete
     * @throws GnipException if the {@link Filter} doesn't exist, if there were problems authenticating with the Gnip server,
     *                       or if another error occurred.
     */
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

    /**
     * Delete the rule from the {@link Filter} associated with the {@link Publisher}.    
     * @param publisher the publisher from which to delete a filter's rule
     * @param filter the filter from which to remove a rule
     * @param rule the rule to remove
     * @throws GnipException if the {@link Filter} doesn't exist, if there were problems authenticating with the Gnip server,
     *                       or if another error occurred.
     */
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

    /**
     * Publish {@link Activity} data in an {@link Activities} model to a {@link Publisher}.  In order to publish
     * activities, the credentials set in the {@link Config} instance associated with this {@link GnipConnection}
     * must own the publisher.
     *
     * @param publisher the publisher to publish activities to
     * @param activities the activities to publish
     * @throws GnipException if the {@link Filter} doesn't exist, if there were problems authenticating with the Gnip server,
     *                       or if another error occurred.
     */
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

    /**
     * Retrieves the {@link Activity} data from the given {@link Publisher} for the current
     * activity bucket.  This method expects that the
     * given {@param publisher} has a public timeline and makes full activity data available to the
     * cerdentials set in the {@link Config} instance used to configure this {@link GnipConnection}.
     * Note, not all {@link Publisher publishers} have a timeline of activity data; for an up-to-date
     * list of publishers that make such data available, check the <a href="https://prod.gnipcentral.com">
     * Gnip Developer</a> website.  Additionally, not all publishers provide access to complete
     * activity data and instead typically just provide access to notifications.
     * <br/>
     * <br/>
     * Most Gnip users will need to use {@link #getNotifications(com.gnipcentral.client.resource.Publisher)}
     * or {@link #getNotifications(com.gnipcentral.client.resource.Publisher, org.joda.time.DateTime)} to
     * get the notifications for a {@link Publisher}.
     *
     * @param publisher the publisher whose activities to get
     * @return the {@link Activities} model, which contains a set of {@link Activity activities}.
     * @throws GnipException if the user doesn't have access to activity data for the Publisher, if there were problems
     *                       authenticating with the Gnip server, or if another error occurred.
     */
    public Activities getActivities(Publisher publisher) throws GnipException {
        try {
            InputStream inputStream = connection.doGet(getActivityUrl(publisher, false, null));
            return Translator.parseActivities(new InputSource(inputStream));
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred getting activities", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred getting activities", e);
        }
    }

    /**
     * Retrieves the {@link Activity} data from the given {@link Publisher} for an activity bucket at a given date.
     *
     * This method expects that the given {@param publisher} has a public timeline and that the publisher makes
     * full activity data available to the cerdentials set in the {@link Config} instance used to configure
     * this {@link GnipConnection}.
     *
     * Note, not all {@link Publisher publishers} have a timeline of activity data.
     *
     * For an up-to-date list of publishers that have a public timeline, see the
     * <a href="https://prod.gnipcentral.com"> Gnip Developer</a> website.
     *
     * Additionally, not all publishers provide access to complete
     * activity data and instead typically just provide access to notifications.
     * <br/>
     * <br/>
     * Most Gnip users will need to use {@link #getNotifications(com.gnipcentral.client.resource.Publisher)}
     * or {@link #getNotifications(com.gnipcentral.client.resource.Publisher, org.joda.time.DateTime)} to
     * get the notifications for a {@link Publisher}.
     *
     * @param publisher the publisher whose activities to get
     * @param dateTime the timestamp of the activity bucket to retrieve 
     * @return the {@link Activities} model, which contains a set of {@link Activity activities} or an empty
     *         {@link Activities} object if no notifications where found in the activity bucket
     *         with the provided {@param dateTime timestamp}.
     * @throws GnipException if the user doesn't have access to activity data for the Publisher, if there were problems
     *                       authenticating with the Gnip server, or if another error occurred.
     */
    public Activities getActivities(Publisher publisher, DateTime dateTime) throws GnipException {
        try {
            InputStream inputStream = connection.doGet(getActivityUrl(publisher, false, dateTime));
            return Translator.parseActivities(new InputSource(inputStream));
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred getting activities", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred getting activities", e);
        }
    }

    /**
     * Retrieves the {@link Activity} notifications from the given {@link Publisher} for the current
     * notification bucket.  This method can be invoked against all publishers that have a public timeline.
     *
     * For an up-to-date list of publishers that have a public timeline, see the
     * <a href="https://prod.gnipcentral.com"> Gnip Developer</a> website.
     *
     * Remember, notifications are just that -- notifications that an activity occurred on a {@link Publisher}.
     * A notification <i>does not</i> contain an activity's complete data.  To obtain full activity data,
     * use a {@link Filter} that has {@link Filter#setFullData(boolean)}.
     *
     * @param publisher the publisher whose notifications to get
     * @return the {@link Activities} model, which contains a set of {@link Activity} objects, or an empty
     *         {@link Activities} object if no notifications were found in the current notification bucket.
     * @throws GnipException if the publisher doesn't exist, if there were problems authenticating with the Gnip
     *                       server, or if another error occurred.
     */
    public Activities getNotifications(Publisher publisher) throws GnipException {
        try {
            InputStream inputStream = connection.doGet(getActivityUrl(publisher, true, null));
            return Translator.parseActivities(new InputSource(inputStream));
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred getting activities", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred getting activities", e);
        }
    }

    /**
     * Retrieves the {@link Activity} notifications from the given {@link Publisher} for the notification
     * bucket at the given date.
     *
     * This method can be invoked against all publishers that have a public timeline.
     *
     * For an up-to-date list of publishers that have a public timeline, see the
     * <a href="https://prod.gnipcentral.com"> Gnip Developer</a> website.
     *
     * Remember, notifications are just that -- notifications that an activity occurred on a {@link Publisher}.
     * A notification <i>does not</i> contain an activity's complete data.  To obtain full activity data,
     * use a {@link Filter} that has {@link Filter#setFullData(boolean)}.
     *
     * @param publisher the publisher whose notifications to get
     * @param dateTime the timestamp of the notification bucket to retrieve
     * @return the {@link Activities} model, which contains a set of {@link Activity} objects, or an empty
     *         {@link Activities} object if no notifications were found in the current notification bucket.
     * @throws GnipException if the publisher doesn't exist, if there were problems authenticating with the Gnip
     *                       server, or if another error occurred.
     */
    public Activities getNotifications(Publisher publisher, DateTime dateTime) throws GnipException {
        try {
            InputStream inputStream = connection.doGet(getActivityUrl(publisher, true, dateTime));
            return Translator.parseActivities(new InputSource(inputStream));
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred getting activities", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred getting activities", e);
        }
    }

    /**
     * Retrieves either the notifications or activities from the current bucket for the given {@link Publisher}
     * and {@link Filter} based on whether the filter supports full data.  See the {@link Filter} class for more information
     * about whether a Filter supports notifications or activities.  If the Filter supports notifications, the
     * {@link Activities} object returned here will just have activity notifications.
     *
     * @param publisher the publisher that owns the filter
     * @param filter the filter whose notifications or activities to retrieve
     * @return the notifications or activities in the current bucket or an empty {@link Activities} object if no
     *         notifications or activities were found in the current bucket.
     * @throws GnipException if the publisher or filter don't exist, if there were problems authenticating with the Gnip
     *                       server, or if another error occurred.
     */
    public Activities getActivities(Publisher publisher, Filter filter) throws GnipException {
        try {
            InputStream inputStream = connection.doGet(getActivityUrl(publisher, filter, null));
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
     * Retrieves either the notifications or activities from the bucket with the given timestamp for the given {@link Publisher}
     * and {@link Filter} based on whether the filter supports full data.  See the {@link Filter} class for more information
     * about whether a Filter supports notifications or activities.  If the Filter supports notifications, the
     * {@link Activities} object returned here will just have activity notifications.
     *
     * @param publisher the publisher that owns the filter
     * @param filter the filter whose notifications or activities to retrieve
     * @param dateTime the timestamp of the bucket whose notifications or activities to retrieve 
     * @return the notifications or activities in the current bucket or an empty {@link Activities} object if no
     *         notifications or activities were found in the current bucket.
     * @throws GnipException if the publisher or filter don't exist, if there were problems authenticating with the Gnip
     *                       server, or if another error occurred.
     */
    public Activities getActivities(Publisher publisher, Filter filter, DateTime dateTime) throws GnipException {
        try {
            InputStream inputStream = connection.doGet(getActivityUrl(publisher, filter, dateTime));
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

    private String getActivityUrl(Publisher publisher, boolean isNotification, DateTime date) {
        String bucket = date == null ? "current" : getDateString(date);
        String endpoint = isNotification ? "notification" : "activity";
        return getPublisherUrl(publisher.getName()) + "/" + endpoint + "/" + bucket + ".xml";
    }

    private String getActivityUrl(Publisher publisher, Filter filter, DateTime date) {
        String bucket = date == null ? "current" : getDateString(date);
        String endpoint = filter.isFullData() ? "activity" : "notification";
        return getFilterCreateUrl(publisher.getName()) + "/" + filter.getName() + "/" + endpoint + "/" + bucket + ".xml";
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