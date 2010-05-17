package com.gnipcentral.client.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.*;

/**
 * Container class that wraps a set of {@link Activity} instances that will be sent to a Gnip server or were read from a
 * Gnip server.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "results")
public class Results implements Resource {

    @XmlElement(name = "activity", type = Activity.class)
    private List<Activity> activities;
    @XmlElement(name = "pollResponseCode")
    private String pollResponseCode;
    @XmlElement(name = "pollResponseMessage")
    private String pollResponseMessage;
    @XmlElement(name = "count")
    private String count;
    @XmlElement(name = "uniqueCount")
    private String uniqueCount;
    @XmlElement(name = "data_collector_id")
    private String dataCollectorId;
    @XmlElement(name = "publisher")
    private String publisher;
    @XmlElement(name = "endpoint")
    private String endPoint;
    @XmlElement(name = "refreshURL")
    private String refreshUrl;

    /**
     * Default constructor.
     */
    public Results() {
        // Empty constructor
    }

    /**
     * Construct an {@link Results} instance with a set of {@link com.gnipcentral.client.resource.Activity
     * activities}.
     * 
     * @param activities
     */
    public Results(Activity... activities) {
        this.activities = new ArrayList<Activity>();
        for (Activity activity : activities) {
            this.activities.add(activity);
        }
    }

    /**
     * Retrieves a list of activities.
     * 
     * @return the list of activities
     */
    public List<Activity> getActivities() {
        return activities;
    }

    /**
     * Set the list of activities.
     * 
     * @param activities
     *            the list of activities
     */
    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    /**
     * Retrieves whether this activities object has one or more activity instances.
     * 
     * @return <code>true</code> if this contains activities; <code>false</code> otherwise
     */
    public boolean isEmpty() {
        return (activities == null || activities.isEmpty());
    }

    /**
     * Add an activity to the list of activities.
     * 
     * @param activity
     *            the activity to add
     * @return a reference to this object
     */
    public Results add(Activity activity) {
        if (activities == null) {
            activities = new ArrayList<Activity>();
        }
        activities.add(activity);
        return this;
    }

    /**
     * Add all of the activities from another {@link Results} object.
     * 
     * @param activities
     *            the activities to add
     * @return a reference to this object
     */
    public Results addAll(Results activities) {
        if (activities != null && activities.getActivities() != null) {
            if (this.activities == null) {
                this.activities = new ArrayList<Activity>(activities.getActivities().size());
            }
            this.activities.addAll(activities.getActivities());
        }
        return this;
    }

    /**
     * Add all of the activities from a {@link List} of activities.
     * 
     * @param activities
     *            the collection of activities to add
     * @return a reference to this object
     */
    public Results addAll(Collection<Activity> activities) {
        if (activities != null) {
            if (this.activities == null) {
                this.activities = new ArrayList<Activity>(activities.size());
            }
            this.activities.addAll(activities);
        }
        return this;
    }

    /**
     * @return the pollResponseCode
     */
    public String getPollResponseCode() {
        return pollResponseCode;
    }

    /**
     * @param pollResponseCode
     *            the pollResponseCode to set
     */
    public void setPollResponseCode(String pollResponseCode) {
        this.pollResponseCode = pollResponseCode;
    }

    /**
     * @return the pollResponseMessage
     */
    public String getPollResponseMessage() {
        return pollResponseMessage;
    }

    /**
     * @param pollResponseMessage
     *            the pollResponseMessage to set
     */
    public void setPollResponseMessage(String pollResponseMessage) {
        this.pollResponseMessage = pollResponseMessage;
    }

    /**
     * @return the count
     */
    public String getCount() {
        return count;
    }

    /**
     * @param count
     *            the count to set
     */
    public void setCount(String count) {
        this.count = count;
    }

    /**
     * @return the uniqueCount
     */
    public String getUniqueCount() {
        return uniqueCount;
    }

    /**
     * @param uniqueCount
     *            the uniqueCount to set
     */
    public void setUniqueCount(String uniqueCount) {
        this.uniqueCount = uniqueCount;
    }

    public String getDataCollectorId() {
        return dataCollectorId;
    }

    public void setDataCollectorId(String dataCollectorId) {
        this.dataCollectorId = dataCollectorId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getRefreshUrl() {
        return refreshUrl;
    }

    public void setRefreshUrl(String refreshUrl) {
        this.refreshUrl = refreshUrl;
    }
}
