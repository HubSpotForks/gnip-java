package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

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

    public Filter(String name) {
        this.name = name;
    }

    public Filter(String name, String postUrl) {
        this(name);
        this.postUrl = postUrl;
    }

    public Filter(String name, String postUrl, boolean fullData) {
        this(name, postUrl);
        this.fullData = fullData;
    }

    public List<Rule> getRules() {
        if (rules == null) {
            rules = new ArrayList<Rule>();
        }
        return this.rules;
    }

    public void addRule(Rule rule) {
        getRules().add(rule);
    }

    public void removeRule(Rule rule) {
        getRules().remove(rule);
    }

    public String getName() {
        return name;
    }

    public boolean isFullData() {
        return fullData;
    }

    public void setFullData(boolean fullData) {
        this.fullData = fullData;
    }

    public String getPostUrl() {
        return postUrl;
    }

    public void setPostUrl(String value) {
        this.postUrl = value;
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