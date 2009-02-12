package com.gnipcentral.client.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.*;

/**
 * Container class that wraps a set of {@link Publisher}s.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "publishers")
public class Publishers implements Resource {

    @XmlElement(name = "publisher", required = true, type = Publisher.class)
    private List<Publisher> publishers;

    /**
     * Basic constructor.
     */
    public Publishers() {
    }

    /**
     * Get the list of publishers.
     * @return the publishers
     */
    public List<Publisher> getPublishers() {
        return publishers;
    }

    /**
     * Set the list of publishers.
     * @return the publishers
     */
    public void setPublishers(List<Publisher> publishers) {
        this.publishers = publishers;
    }

    /**
     * Add a publisher.
     * @param publisher the Publisher to add
     * @return a reference to this object
     */
    public Publishers add(Publisher publisher) {
        if (publishers == null) {
            publishers = new ArrayList<Publisher>();
        }
        publishers.add(publisher);
        return this;
    }

    /**
     * Add a publisher.
     * @param publishers the collection of Publisher to add
     * @return a reference to this object
     */
    public Publishers addAll(Collection<Publisher> publishers) {
        if (publishers != null) {
            if (this.publishers == null) {
                this.publishers = new ArrayList<Publisher>(publishers.size());
            }
            this.publishers.addAll(publishers);
        }
        return this;
    }
}