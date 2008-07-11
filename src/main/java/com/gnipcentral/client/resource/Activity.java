package com.gnipcentral.client.resource;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;

@XmlRootElement(name = "activity")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "activityType")
public class Activity implements Resource {

    private static DatatypeFactory datatypeFactory;

    static {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @XmlAttribute(required = true)
    protected XMLGregorianCalendar at;

    @XmlAttribute(required = true)
    protected String uid;

    @XmlAttribute(required = true)
    protected String type;
    @XmlAttribute
    protected String guid;
    @XmlAttribute(name = "publisher.name")
    protected String publisherName;

    //for jaxb
    @SuppressWarnings({"UnusedDeclaration"})
    private Activity() {
    }

    public Activity(DateTime at, String uid, String type) {
        this.at = toXMLGregorianCalendar(at);
        this.uid = uid;
        this.type = type;
    }

    public Activity(String uid, String type) {
        this(new DateTime(), uid, type);
    }

    public Activity(DateTime at, String uid, String type, String guid) {
        this(at, uid, type);
        this.guid = guid;
    }

    public DateTime getAt() {
        return fromXMLGregorianCalendar(at);
    }

    public String getType() {
        return type;
    }

    public String getGuid() {
        return guid;
    }

    public String getPublisherName() {
        return publisherName;
    }

    private static XMLGregorianCalendar toXMLGregorianCalendar(DateTime dateTime) {
        return datatypeFactory.newXMLGregorianCalendar(dateTime.toGregorianCalendar());
    }

    private static DateTime fromXMLGregorianCalendar(XMLGregorianCalendar xmlTime) {
        GregorianCalendar calendar = xmlTime.toGregorianCalendar();
        return new DateTime(calendar.getTimeInMillis(), DateTimeZone.forTimeZone(calendar.getTimeZone()));
    }

    public void setPublisherName(String name) {
        publisherName = name;
    }

    public Uid getUid() {
        return new Uid(uid, publisherName);
    }
}