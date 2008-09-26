package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "publishers")
public class Publishers implements Resource {

    @XmlElement(name = "publisher", required = true, type = Publisher.class)
    private List<Publisher> publishers;

    public Publishers() {
    }

    public List<Publisher> getPublishers() {
        if (publishers == null) {
            publishers = new ArrayList<Publisher>();
        }
        return publishers;
    }

    public void addPublisher(Publisher publisher) {
        getPublishers().add(publisher);
    }
}