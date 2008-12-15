package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Container class that wraps a set of {@link Activity} instances that will be sent to a Gnip server
 * or were read from a Gnip server.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"activities"})
@XmlRootElement(name = "activities")
public class Activities implements Resource {

    @XmlElement(name = "activity", required = false, type = Activity.class)
    private ArrayList<Activity> activities;

    @XmlAttribute(name="publisher",required=false)
    private String publisherName;

    /**
     * Default constructor.
     */
    public Activities() {
        activities = new ArrayList<Activity>();
    }

    /**
     * Construct an {@link Activities} instance with a set of {@link com.gnipcentral.client.resource.Activity activities}.
     * @param activities
     */
    public Activities(Activity ... activities) {
        this();
        for(Activity activity : activities) {
            add(activity);
        }
    }

    /**
     * Retrieves a list of activities.
     * @return the list of activities or an empty list if no activities exist
     */
    public List<Activity> getActivities() {
        return activities;
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
        return activities.isEmpty();
    }

    /**
     * Add an activity to the list of activities.
     * @param activity the activity to add
     * @return a reference to this object
     */
    public Activities add(Activity activity) {
        activities.add(activity);
        return this;
    }

    /**
     * Add all of the activities from another {@link Activities} object.
     * @param activities the activities to add
     * @return a reference to this object
     */
    public Activities addAll(Activities activities) {
        activities.addAll(activities.getActivities());
        return this;
    }

    /**
     * Add all of the activities from a {@link List} of activities.
     * @param activities the activities to add
     * @return a reference to this object
     */
    public Activities addAll(List<Activity> activities) {
        activities.addAll(activities);
        return this;
    }
}