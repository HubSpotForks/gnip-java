package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@XmlRootElement(name = "uid")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "uidType")
public class Uid implements Resource {

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String name;

    @XmlAttribute(name = "publisher.name", required = true)
    protected String publisherName;

    @XmlTransient
    private static final Pattern resourceIdPattern = Pattern.compile("uid=([^&]+)&publisher.name=(.*)");

    // for jaxb
    @SuppressWarnings({"UnusedDeclaration"})
    private Uid() {
    }

    public Uid(String restUidResourceId) {
        Matcher matcher = resourceIdPattern.matcher(restUidResourceId);
        if (matcher.matches()) {
            name = matcher.group(1);
            publisherName = matcher.group(2);
        }
    }

    public Uid(String name, String publisherName) {
        this.name = name;
        this.publisherName = publisherName;
    }

    public String getName() {
        return name;
    }

    public String getPublisherName() {
        return publisherName;
    }

    @SuppressWarnings({"RedundantIfStatement"})
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Uid uid = (Uid) o;

        if (name != null ? !name.equals(uid.name) : uid.name != null) return false;
        if (publisherName != null ? !publisherName.equals(uid.publisherName) : uid.publisherName != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (publisherName != null ? publisherName.hashCode() : 0);
        return result;
    }
}