package com.gnipcentral.client.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.*;

/**
 * Container class that wraps a set of {@link Activity} instances that will be sent to a Gnip server
 * or were read from a Gnip server.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "activities")
public class Activities implements Resource {

    @XmlElement(name = "activity", type = Activity.class)
    private List<Activity> activities;
    @XmlAttribute(name = "publisher")
    private String publisherName;

    /**
     * Default constructor.
     */
    public Activities() {
    }

    /**
     * Construct an {@link Activities} instance with a set of {@link com.gnipcentral.client.resource.Activity activities}.
     * @param activities
     */
    public Activities(Activity ... activities) {
        this.activities = new ArrayList<Activity>();
        for (Activity activity : activities) {
            this.activities.add(activity);
        }
    }

    /**
     * Retrieves a list of activities.
     * @return the list of activities
     */
    public List<Activity> getActivities() {
        return activities;
    }

    /**
     * Set the list of activities.
     * @param activities the list of activities
     */
    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    /**
     * If these activities came from a Publisher, retrieves the name of the publisher
     * @return the name of the {@link com.gnipcentral.client.resource.Publisher} or <code>null</code> if the activities were created in the client.
     */
    public String getPublisherName() {
        return publisherName;
    }

    /**
     * Retrieves whether this activities object has one or more activity instances.
     * @return <code>true</code> if this contains activities; <code>false</code> otherwise
     */
    public boolean isEmpty() {
        return (activities == null || activities.isEmpty());
    }

    /**
     * Add an activity to the list of activities.
     * @param activity the activity to add
     * @return a reference to this object
     */
    public Activities add(Activity activity) {
        if (activities == null) {
            activities = new ArrayList<Activity>();
        }
        activities.add(activity);
        return this;
    }

    /**
     * Add all of the activities from another {@link Activities} object.
     * @param activities the activities to add
     * @return a reference to this object
     */
    public Activities addAll(Activities activities) {
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
     * @param activities the collection of activities to add
     * @return a reference to this object
     */
    public Activities addAll(Collection<Activity> activities) {
        if (activities != null) {
            if (this.activities == null) {
                this.activities = new ArrayList<Activity>(activities.size());
            }
            this.activities.addAll(activities);
        }
        return this;
    }
}