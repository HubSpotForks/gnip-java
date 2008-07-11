package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "uids"
        })
@XmlRootElement(name = "collection")
public class Collection implements Resource {

    @XmlElement(required = true, name = "uid", type = Uid.class)
    protected List<Uid> uids;

    @XmlAttribute(required = true)
    protected String name;

    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    protected String postUrl;

    //for jaxb
    @SuppressWarnings({"UnusedDeclaration"})
    private Collection() {
    }

    public Collection(String name) {
        this.name = name;
    }

    public Collection(String name, String postUrl) {
        this(name);
        this.postUrl = postUrl;
    }

    public List<Uid> getUids() {
        if (uids == null) {
            uids = new ArrayList<Uid>();
        }
        return this.uids;
    }

    public void addUid(Uid uid) {
        getUids().add(uid);
    }

    public void removeUid(Uid uid) {
        getUids().remove(uid);
    }

    public String getName() {
        return name;
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

        Collection that = (Collection) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (uids != null ? uids.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (postUrl != null ? postUrl.hashCode() : 0);
        return result;
    }
}