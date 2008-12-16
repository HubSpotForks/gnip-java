package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A model object that represents a Gnip filter.  A Filter is a stream containing activities from a Publisher
 * that meet subscriber defined criteria.  For example,
 * a Filter would allow a subscriber to create activity streams that contain just the activities from
 * usernames (actors) in which they are interested.  These criteria are defined by adding {@link Rule rules} to
 * a Filter via {@link #addRule(Rule)}.
 * <br/>
 * <br/>
 * A Filter can support either full-data activities or notifications but not both by setting the Filter's
 * {@link #isFullData() full data} flag.  By default, a Filter supports full data.
 * <br/>
 * <br/>
 * By default, a Filter creates an activity stream that is available at an HTTP endpoint on a Gnip server
 * and can be accessed (even polled) using HTTP GET.  If a Filter specifies a post URL, then the Gnip
 * server will push activities from the Filter's activity stream to the post URL using an HTTP POST.
 * <br/>
 * <br/>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"postUrl","rules"})
@XmlRootElement(name = "filter")
public class Filter implements Resource {

    @XmlElement(required = true, name = "rule", type = Rule.class)
    private List<Rule> rules;

    @XmlElement(name="postUrl")
    @XmlSchemaType(name = "anyURI")
    private String postUrl;

    @XmlAttribute(required = true)
    private String name;

    @XmlAttribute(required=true)
    private boolean fullData = true;

    @SuppressWarnings({"UnusedDeclaration"})
    private Filter() {
        // empty constructor for jaxb
    }

    /**
     * Create a Filter with the given name.
     * @param name the filter's name                          
     */
    public Filter(String name) {
        this.name = name;
    }

    /**
     * Create a Filter with the given name and post URL.
     * @param name the filter name
     * @param postUrl the post URL
     */
    public Filter(String name, String postUrl) {
        this(name);
        this.postUrl = postUrl;
    }

    /**
     * Create a Filter with the given name, post url, and full data flag.  If the Filter supports full-data
     * activities, the {@link #fullData} should be set to <code>true</code>.  If set to <code>false</code>,
     * the Filter will just support notifications.
     * 
     * @param name
     * @param postUrl
     * @param fullData
     */
    public Filter(String name, String postUrl, boolean fullData) {
        this(name, postUrl);
        this.fullData = fullData;
    }

    /**
     * Get the list of rules associated with this filter.  {@link Rule Rules} are subscriber-specified
     * criteria used to activities from a Publisher to the Filter's activity stream. 
     * @return the list of activities
     */
    public List<Rule> getRules() {
        if (rules == null) {
            rules = new ArrayList<Rule>();
        }
        return this.rules;
    }

    /**
     * Add a {@link Rule} to the filter.
     * @param rule the rule to add
     * @return a reference to this object
     */
    public Filter addRule(Rule rule) {
        getRules().add(rule);
        return this;
    }

    /**
     * Remove a {@link Rule} from the filter.
     * @param rule the rule to remove
     * @return a reference to this object
     */
    public Filter removeRule(Rule rule) {
        getRules().remove(rule);
        return this;        
    }

    /**
     * Retrieves the name of this Filter.
     * @return the filter's name
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the flag describing whether this filter supports access to full activity data or
     * just notifications.
     * @return <code>true</code> if the filter supports full data; <code>false</code> otherwise if
     * the filter just supports notifications.
     */
    public boolean isFullData() {
        return fullData;
    }

    /**
     * Set the flag describing whether this filter supports access to full activity data or just
     * notifications.  To add full activity data to the Filter's stream, set this value to <code>true</code>;
     * otherwise to only add notifications to the stream, set the flag to <code>false</code>.
     * @param fullData
     */
    public void setFullData(boolean fullData) {
        this.fullData = fullData;
    }

    /**
     * Retrieves the filter's post URL.
     * @return the post URL if set; <code>null</code> otherwise.
     */
    public String getPostUrl() {
        return postUrl;
    }

    /**
     * Sets the post URL for the Filter.  If set, the post URL is used by a Gnip server to send either
     * full data activites or just activity notifications to the subscriber via an HTTP POST.
     * @param postUrl the post URL
     */
    public void setPostUrl(String postUrl) {
        this.postUrl = postUrl;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Filter that = (Filter) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (fullData != that.fullData) return false;
        if (postUrl != null ? !postUrl.equals(that.postUrl) : that.postUrl != null) return false;
        return true;
    }

    public int hashCode() {
        int result;
        result = (rules != null ? rules.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (postUrl != null ? postUrl.hashCode() : 0);
        return result;
    }
}