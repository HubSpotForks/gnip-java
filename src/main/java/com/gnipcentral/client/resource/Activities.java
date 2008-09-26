package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"activities"})
@XmlRootElement(name = "activities")
public class Activities implements Resource {

    @XmlElement(name = "activity", required = false, type = Activity.class)
    private List<Activity> activities;

    public List<Activity> getActivities() {
        if (activities == null) {
            activities = new ArrayList<Activity>();
        }
        return this.activities;
    }

    public boolean isEmpty() {
        return getActivities().isEmpty();
    }

    public void add(Activity activity) {
        getActivities().add(activity);
    }

    public void addAll(Activities activities) {
        getActivities().addAll(activities.getActivities());
    }

    public void addAll(List<Activity> activities) {
        getActivities().addAll(activities);
    }
}
