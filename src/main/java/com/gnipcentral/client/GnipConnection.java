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

    public static final long BUCKET_SIZE_MILLIS = 60 * 1000;
    
    private static final Logger LOG = LoggerFactory.getInstance();
    
    private final HTTPConnection connection;
    private final Config config;
    
    private long timeCorrection;

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
     * Retrieves the time correction value in milliseconds that is added to the DateTime passed to 
     * getActivities(..., DateTime) and getNotifications(..., DateTime). Typically this is 
     * either set to 0, (the default), or getServerTimeDelta(). 
     * 
     * @return time correction that will be applied to future date bucket parameters.
     */
    public long getTimeCorrection() {
        return timeCorrection;
    }
    
    /**
     * Sets a time correction value in milliseconds that is added to the DateTime passed to 
     * getActivities(..., DateTime) and getNotifications(..., DateTime). Typically this is 
     * either set to 0, (the default), or getServerTimeDelta(). 
     * <br/>
     * <br/>
     * When activities are published to date buckets, they are published according to
     * the Gnip server GMT time. Thus, when passing a client generated dateTime as a parameter to
     * the methods mentioned above, you may not get expected results if your client time is 
     * different than that of the server, which it likely is. For instance, say you want all the
     * activities published one minute ago. you would get the current time and subtract one minute.
     * However, that time is likely to be, at the very least, a little different than the server 
     * time. You have two options to adjust that time. You can add the results of getServerTimeDelta()
     * to the local time, or you can set time correction to getServerTimeDelta() and the GnipConnection
     * will automatically use it to adjust the the dateTime passed to the getActivities and 
     * getNotifications methods.
     * 
     * @param timeCorrection time correction to apply to future date bucket parameters.
     */
    public void setTimeCorrection(long timeCorrection) {
        this.timeCorrection = timeCorrection;
    }
    
    /**
     * This method gets the number of milliseconds difference between the client time and the server time. 
     * Adding this delta to the local machine time should approximate the servers actual time. This value
     * can then used to adjust times when getting time sensitive data such as getting activities from
     * buckets by .
     * 
     * @return current server time delta in milliseconds or 0 if an exception or error occurred.
     */
    public long getServerTimeDelta() {
        long serverTimeDelta = 0L;
        try {
            serverTimeDelta = connection.getServerTimeDelta();
            LOG.log("Server time delta: %dms\n", serverTimeDelta);
        }
        catch(IOException e) {
            LOG.log("Exception getting server time delta: %s\n", e.toString());
        }
        return serverTimeDelta;
    }
    
    /**
     * Create a new {@link Publisher}.
     * @param publisher
     * @return result message object from Gnip server.
     * @throws GnipException if the Publisher already exists, if there were problems authenticating with a Gnip
     *                       server, or if another error occurred.  
     */
    public Result create(Publisher publisher) throws GnipException {
        try {
            byte[] data = convertToBytes(publisher);
            InputStream response = connection.doPost(getPublishersUrl(publisher.getScope()), data);
            return Translator.parseResult(response);
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
     * @return result message object from Gnip server.
     * @throws GnipException if the Filter already exists, if there were problems authenticating with a Gnip
     *                       server, or if another error occurred.
     */
    public Result create(Publisher publisher, Filter filter) throws GnipException {
        try {
            byte[] data = convertToBytes(filter);
            InputStream response = connection.doPost(getFilterCreateUrl(publisher.getScope(), publisher.getName()), data);
            return Translator.parseResult(response);
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred creating Filter", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred creating Filter", e);
        }

    }

    /**
     * Retrieves the list of Publishers available from Gnip. All publishers are scoped
     * by publisher scope.
     * @param publisherScope the publisher scope of publishers to retrieve.
     * @return the list of {@link Publishers}
     * @throws GnipException if there were problems authenticating with the Gnip server or if another error occurred.
     */
    public Publishers getPublishers(PublisherScope publisherScope) throws GnipException {
        try {
            InputStream response = connection.doGet(getPublishersUrl(publisherScope) + ".xml");
            Publishers publishers = Translator.parsePublishers(new InputSource(response));
            for (Publisher publisher : publishers.getPublishers()) {
                publisher.setScope(publisherScope);
            }
            return publishers;
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred getting Publishers", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred getting Publishers", e);
        }
    }

    /**
     * Retrieves a Publisher named <code>publisherName</code>. All publishers are scoped
     * by publisher scope.
     * @param publisherScope the publisher scope of publisher to retrieve.
     * @param publisherName name of the publisher to get
     * @return the {@link Publisher} if it exists
     * @throws GnipException if the publisher doesn't exist, if there were problems authenticating with the Gnip server,
     *                       or if another error occurred.                       
     */
    public Publisher getPublisher(PublisherScope publisherScope, String publisherName) throws GnipException {
        try {
            InputStream response = connection.doGet(getPublishersUrl(publisherScope, publisherName));
            Publisher publisher = Translator.parsePublisher(new InputSource(response));
            publisher.setScope(publisherScope);
            return publisher;
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

        return getFilter(publisher, filter.getName());
    }

    /**
     * Retrieves the Filter named {@link com.gnipcentral.client.resource.Filter#getName()} from the {@link Publisher}
     * named {@link com.gnipcentral.client.resource.Publisher#getName()}
     * @param publisher the publisher that owns the filter
     * @param filterName the filter name to retrieve
     * @return the {@link Filter} if it exists
     * @throws GnipException if the {@link Filter} doesn't exist, if there were problems authenticating with the Gnip server,
     *                       or if another error occurred. 
     */
    public Filter getFilter(Publisher publisher, String filterName) throws GnipException {
        if(publisher == null) {
            throw new IllegalArgumentException("Publisher cannot be null");
        }

        if(filterName == null) {
            throw new IllegalArgumentException("Filter name cannot be null");
        }

        return getFilter(publisher.getScope(), publisher.getName(), filterName);
    }

    /**
     * Retrieves the Filter named {@param filterName} from the {@link Publisher} named {@param publisherName}.
     * @param publisherScope the publisher scope of publisher.
     * @param publisherName the name of the publisher
     * @param filterName the filter to retrieve
     * @return the {@link Filter} if it exists
     * @throws GnipException if the {@link Filter} doesn't exist, if there were problems authenticating with the Gnip server,
     *                       or if another error occurred.
     */
    public Filter getFilter(PublisherScope publisherScope, String publisherName, String filterName) throws GnipException {
        if(filterName == null) {
            throw new IllegalArgumentException("Filter name cannot be null");
        }

        try {
            InputStream response = connection.doGet(getFilterUrl(publisherScope, publisherName, filterName));
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
     * @return result message object from Gnip server.
     * @throws GnipException if the {@link Filter} doesn't exist, if there were problems authenticating with the Gnip server,
     *                       or if another error occurred. 
     */
    public Result update(Publisher publisher, Filter filter) throws GnipException {
        try {
            byte[] data = convertToBytes(filter);
            InputStream response;
            if(config.isTunnelOverPost()) {
                response = connection.doPost(tunnelEditOverPost(getFilterUrl(publisher, filter.getName())), data);
            }
            else {
                response = connection.doPut(getFilterUrl(publisher, filter.getName()), data);
            }
            return Translator.parseResult(response);
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
     * @return result message object from Gnip server.
     * @throws GnipException if the {@link Filter} doesn't exist, if there were problems authenticating with the Gnip server,
     *                       or if another error occurred.
     */
    public Result update(Publisher publisher, Filter filter, Rule rule) throws GnipException {
        try {
            byte[] data = convertToBytes(rule);
            InputStream response = connection.doPost(getRulesUrl(publisher, filter.getName()), data);
            return Translator.parseResult(response);
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
     * @return result message object from Gnip server.
     * @throws GnipException if the {@link Filter} doesn't exist, if there were problems authenticating with the Gnip server,
     *                       or if another error occurred.
     */
    public Result update(Publisher publisher, Filter filter, Rules rules) throws GnipException {
        try {
            byte[] data = convertToBytes(rules);
            InputStream response = connection.doPost(getRulesUrl(publisher, filter.getName()), data);
            return Translator.parseResult(response);
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred updating Rule", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred updating Rule", e);
        }        
    }

    /**
     * Delete an existing {@link Publisher}.
     * @param publisher to delete.
     * @return result message object from Gnip server.
     * @throws GnipException if the Publisher does not exist, if there were problems authenticating with a Gnip
     *                       server, or if another error occurred.  
     */
    public Result delete(Publisher publisher) throws GnipException {
        return delete(publisher.getScope(), publisher.getName());
    }

    /**
     * Delete an existing {@link Publisher} by publisher scope and publisher name.
     * @param publisherScope the publisher scope of publisher to delete.
     * @param publisherName name of the publisher to delete.
     * @return result message object from Gnip server.
     * @throws GnipException if the Publisher does not exist, if there were problems authenticating with a Gnip
     *                       server, or if another error occurred.  
     */
    public Result delete(PublisherScope publisherScope, String publisherName) throws GnipException {
        try {
            InputStream response;
            if(config.isTunnelOverPost()) {
                response = connection.doPost(tunnelDeleteOverPost(getPublisherUrl(publisherScope, publisherName)), new byte[0]);
            }
            else {
                response = connection.doDelete(getPublisherUrl(publisherScope, publisherName));
            }
            return Translator.parseResult(response);
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred deleting Publisher", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred deleting Publisher", e);
        }
    }

    /**
     * Delete the filter {@link Filter} from the {@link Publisher}.
     * @param publisher the publisher from which to delete the filter
     * @param filter the filter to delete
     * @return result message object from Gnip server.
     * @throws GnipException if the {@link Filter} doesn't exist, if there were problems authenticating with the Gnip server,
     *                       or if another error occurred.
     */
    public Result delete(Publisher publisher, Filter filter) throws GnipException {
        try {
            InputStream response;
            if(config.isTunnelOverPost()) {
                response = connection.doPost(tunnelDeleteOverPost(getFilterUrl(publisher, filter.getName())), new byte[0]);
            }
            else {
                response = connection.doDelete(getFilterUrl(publisher, filter.getName()));
            }
            return Translator.parseResult(response);
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred deleting Filter", e);
        }
        catch(JAXBException e) {
            throw new GnipException("Exception occurred deleting Filter", e);
        }        
    }

    /**
     * Delete the rule from the {@link Filter} associated with the {@link Publisher}.    
     * @param publisher the publisher from which to delete a filter's rule
     * @param filter the filter from which to remove a rule
     * @param rule the rule to remove
     * @return result message object from Gnip server.
     * @throws GnipException if the {@link Filter} doesn't exist, if there were problems authenticating with the Gnip server,
     *                       or if another error occurred.
     */
    public Result delete(Publisher publisher, Filter filter, Rule rule) throws GnipException {
        try {
            String url = getRulesDeleteUrl(publisher, filter, rule);
            InputStream response;
            if(config.isTunnelOverPost()) {
                response = connection.doPost(url, null);
            }
            else {
                response = connection.doDelete(url);
            }
            return Translator.parseResult(response);
        }
        catch(IOException e) {
            throw new GnipException("Exception occurred deleting Rule", e);
        }
        catch(JAXBException e) {
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
     * @return result message object from Gnip server or null if there were no activities to publish.
     * @throws GnipException if the {@link Filter} doesn't exist, if there were problems authenticating with the Gnip server,
     *                       or if another error occurred.
     */
    public Result publish(Publisher publisher, Activities activities) throws GnipException {
        if (activities == null || activities.getActivities().isEmpty())
            return null;

        try {
            byte[] data = convertToBytes(activities);
            InputStream response = connection.doPost(getActivitiesPublishUrl(publisher), data);
            return Translator.parseResult(response);
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
     * credentials set in the {@link Config} instance used to configure this {@link GnipConnection}.
     * Note, not all {@link Publisher publishers} have a timeline of activity data; for an up-to-date
     * list of publishers that make such data available, check the <a href="https://prod.gnipcentral.com">
     * Gnip Developer</a> website.  Additionally, not all publishers provide access to complete
     * activity data and instead typically just provide access to notifications.
     * <br/>
     * <br/>
     * Most Gnip users will need to use {@link #getNotifications(com.gnipcentral.client.resource.Publisher)}
     * or {@link #getNotifications(com.gnipcentral.client.resource.Publisher, org.joda.time.DateTime)} to
     * get the notifications for a {@link Publisher}.
     * <br/>
     * <br/>
     * This method uses the current time correction to adjust bucket start times sent to the Gnip server.
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
     * <br/>
     * <br/>
     * This method uses the current time correction value to adjust bucket start times sent to the Gnip server.
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
     * <br/>
     * <br/>
     * This method uses the current time correction value to adjust bucket start times sent to the Gnip server.
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
     * <br/>
     * <br/>
     * This method uses the current time correction value to adjust bucket start times sent to the Gnip server.
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
     * <br/>
     * <br/>
     * This method uses the current time correction value to adjust bucket start times sent to the Gnip server.
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
     * <br/>
     * <br/>
     * This method uses the current time correction value to adjust bucket start times sent to the Gnip server.
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

    private String getPublishersUrl(PublisherScope publisherScope) {
        return config.getGnipServer() + "/" + publisherScope.requestScope() + "/publishers";
    }

    private String getPublishersUrl(PublisherScope publisherScope, String publisherName) {
        return getPublishersUrl(publisherScope) + "/" + publisherName + ".xml";
    }

    private String getPublisherUrl(PublisherScope publisherScope, String publisherName) {
        return getPublishersUrl(publisherScope) + "/" + publisherName;
    }

    private String getFilterCreateUrl(PublisherScope publisherScope, String publisherName) {
        return getPublishersUrl(publisherScope) + "/" + publisherName + "/filters";
    }

    private String getFilterUrl(PublisherScope publisherScope, String publisherName, String filterName) {
        return getPublishersUrl(publisherScope) + "/" + publisherName + "/filters/" + filterName + ".xml";
    }

    private String getFilterUrl(Publisher publisher, String filterName) {
        return getFilterUrl(publisher.getScope(), publisher.getName(), filterName);
    }

    private String getRulesUrl(PublisherScope publisherScope, String publisherName, String filterName) {
        return getPublishersUrl(publisherScope) + "/" + publisherName + "/filters/" + filterName + "/rules";
    }

    private String getRulesUrl(Publisher publisher, String filterName) {
        return getRulesUrl(publisher.getScope(), publisher.getName(), filterName);
    }

    private String getRulesDeleteUrl(Publisher publisher, Filter filter, Rule rule) throws UnsupportedEncodingException {
        String url = getRulesUrl(publisher, filter.getName());
        if(config.isTunnelOverPost()) {
            url = tunnelDeleteOverPost(url);
        }        
        return url + "?type=" + encodeUrlParameter(rule.getType().toString()) + "&value=" + encodeUrlParameter(rule.getValue());
    }

    private String getActivitiesPublishUrl(Publisher publisher) {
        return getPublisherUrl(publisher.getScope(), publisher.getName()) + "/activity";
    }

    private String getActivityUrl(Publisher publisher, boolean isNotification, DateTime date) {
        String bucket = date == null ? "current" : getDateString(date);
        String endpoint = isNotification ? "notification" : "activity";
        return getPublisherUrl(publisher.getScope(), publisher.getName()) + "/" + endpoint + "/" + bucket + ".xml";
    }

    private String getActivityUrl(Publisher publisher, Filter filter, DateTime date) {
        String bucket = date == null ? "current" : getDateString(date);
        String endpoint = filter.isFullData() ? "activity" : "notification";
        return getFilterCreateUrl(publisher.getScope(), publisher.getName()) + "/" + filter.getName() + "/" + endpoint + "/" + bucket + ".xml";
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

    private DateTime getBucketFloor(DateTime date) {
        long floor = new Double(Math.floor((date.getMillis() + timeCorrection) / BUCKET_SIZE_MILLIS)).longValue();
        return new DateTime(floor * BUCKET_SIZE_MILLIS, DateTimeZone.UTC);
    }
}
