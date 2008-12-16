package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Container class that wraps a set of {@link Publisher}s.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "publishers")
public class Publishers implements Resource {

    @XmlElement(name = "publisher", required = true, type = Publisher.class)
    private ArrayList<Publisher> publishers;

    /**
     * Basic constructor.
     */
    public Publishers() {
        publishers = new ArrayList<Publisher>();
    }

    /**
     * Get the list of publishers.
     * @return the publishers
     */
    public List<Publisher> getPublishers() {
        return publishers;
    }

    /**
     * Add a publisher.
     * @param publisher the Publisher to add
     * @return a reference to this object
     */
    public Publishers addPublisher(Publisher publisher) {
        publishers.add(publisher);
        return this;
    }
}