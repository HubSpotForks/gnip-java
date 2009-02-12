package com.gnipcentral.client.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Model object that represents a Gnip activity.  An activity is approximately equivalent to an event
 * that occurs on a Publisher; for example, on Twitter a tweet is an activity and digging an article is an activity
 * on Digg.
 * <br/>
 * <br/>
 * An Activity may represent a simple "notification" of an event that can be read from a Publisher's notification
 * stream or from the notification stream for a {@link Filter} that does not support full data.
 * <br/>
 * <br/>
 * An Activity may also represent full activity data for an event and can be read from a {@link Filter} that
 * is configured to support full data.
 * <br/>
 * <br/>
 */
@XmlRootElement(name = "activity")
@XmlAccessorType(XmlAccessType.FIELD)
public class Activity implements Resource {

    private static DatatypeFactory datatypeFactory;

    static {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @XmlElement(required = true)
    private XMLGregorianCalendar at;
    @XmlElement(required = true)
    private String action;
    @XmlElement
    private String activityID;
    @XmlElement(name = "URL")
    @XmlSchemaType(name = "anyURI")
    private String url;
    @XmlElement(name = "source", type = String.class)
    private List<String> sources;
    @XmlElement(name = "keyword", type = String.class)
    private List<String> keywords;
    @XmlElement(name = "place", type = Place.class)
    private List<Place> places;
    @XmlElement(name = "actor", type = Actor.class)
    private List<Actor> actors;
    @XmlElement(name = "destinationURL", type = GnipUrl.class)
    private List<GnipUrl> destinationUrls;
    @XmlElement(name = "tag", type = GnipValue.class)
    private List<GnipValue> tags;
    @XmlElement(name = "to", type = GnipValue.class)
    private List<GnipValue> tos;
    @XmlElement(name = "regardingURL", type = GnipUrl.class)
    private List<GnipUrl> regardingUrls;
    @XmlElement(type = Payload.class)
    private Payload payload;

    @SuppressWarnings("unused")
    private Activity() {
        // empty constructor for jaxb
    }

    /**
     * A basic constructor for creating an activity with a specified action. If
     * the required 'at' parameter is not specified, it defaults to now.
     * @param at the required time that represents when the activity occurred.
     * @param action the required action performed.
     */
    public Activity(DateTime at, String action) {
        if (action == null) {
            throw new IllegalArgumentException("Invalid action specified '"+action+"'");
        }

        this.at = toXMLGregorianCalendar(at);
        this.action = action;
    }

    /**
     * A basic constructor for creating an activity with a specified action
     * and activity payload. If the required 'at' parameter is not specified,
     * it defaults to now.
     * @param at the required time that represents when the activity occurred.
     * @param action the required action performed.
     * @param payload the optional data associated with the activity
     */
    public Activity(DateTime at, String action, Payload payload) {
        this(at, action);
        this.payload = payload;
    }

    /**
     * A basic constructor for creating an activity from a specific Actor
     * and an action, often something the actor did. The required 'at'
     * activity parameter is assumed to be now.
     * @param actor the Actor.
     * @param action the required action performed by the actor.
     */
    public Activity(Actor actor, String action) {
        this(new DateTime(), action);
        if (actor != null)
        {
            this.actors = new ArrayList<Actor>();
            this.actors.add(actor);
        }
    }

    /**
     * A basic constructor for creating an activity from a specific Actor,
     * action, and activity payload, often something the actor did. The
     * required 'at' activity parameter is assumed to be now.
     * @param actor the Actor.
     * @param action the required action performed by the actor.
     * @param payload the data associated with the activity.
     */
    public Activity(Actor actor, String action, Payload payload) {
        this(actor, action);
        this.payload = payload;
    }

    /**
     * Retrieves the time at which this Activity's action occurred.
     * @return the Activity time.
     */
    public DateTime getAt() {
        return fromXMLGregorianCalendar(at);
    }

    /**
     * Sets the time at which this Activity's action occurred.
     * @param at the required Activity time or null to specify now.
     */
    public void setAt(DateTime at) {
        this.at = toXMLGregorianCalendar(at);
    }

    /**
     * Retrieves this Activity's action.
     * @return the Activity action.
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets this Activity's action.
     * @param action the required Activity action to set.
     */
    public void setAction(String action) {
        if (action == null) {
            throw new IllegalArgumentException("Invalid action specified '"+action+"'");
        }

        this.action = action;
    }

    /**
     * Retrieves this Activity's id.
     * @return the Activity id.
     */
    public String getActivityID() {
        return activityID;
    }

    /**
     * Sets this Activity's id.
     * @param activityID the optional Activity activity id to set.
     */
    public void setActivityID(String activityID) {
        this.activityID = activityID;
    }

    /**
     * Retrieves this Activity's url.
     * @return the Activity url.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets this Activity's url.
     * @param url the optional Activity url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Retrieves this Activity's sources list.
     * @return the Activity sources.
     */
    public List<String> getSources() {
        return sources;
    }

    /**
     * Sets this Activity's sources list.
     * @param sources the optional Activity sources to set.
     */
    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    /**
     * Add source to this Activity's sources list.
     * @param source the Activity source to add.
     * @return a reference to this object.
     */
    public Activity addSource(String source) {
        if (sources == null) {
            sources = new ArrayList<String>();
        }
        sources.add(source);
        return this;
    }

    /**
     * Add collection of sources to this Activity's sources list.
     * @param sources the Activity sources to add.
     * @return a reference to this object.
     */
    public Activity addSources(Collection<String> sources) {
        if (sources != null) {
            if (this.sources == null) {
                this.sources = new ArrayList<String>(sources.size());
            }
            this.sources.addAll(sources);
        }
        return this;
    }

    /**
     * Retrieves this Activity's keywords list.
     * @return the Activity keywords list.
     */
    public List<String> getKeywords() {
        return keywords;
    }

    /**
     * Sets this Activity's keywords list.
     * @param keywords the optional Activity keywords to set.
     */
    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    /**
     * Add keyword to this Activity's keywords list.
     * @param keyword the Activity keyword to add.
     * @return a reference to this object.
     */
    public Activity addKeyword(String keyword) {
        if (keywords == null) {
            keywords = new ArrayList<String>();
        }
        keywords.add(keyword);
        return this;
    }

    /**
     * Add collection of keywords to this Activity's keywords list.
     * @param keywords the Activity keywords to add.
     * @return a reference to this object.
     */
    public Activity addKeywords(Collection<String> keywords) {
        if (keywords != null) {
            if (this.keywords == null) {
                this.keywords = new ArrayList<String>(keywords.size());
            }
            this.keywords.addAll(keywords);
        }
        return this;
    }

    /**
     * Retrieves this Activity's places list.
     * @return the Activity places list.
     */
    public List<Place> getPlaces() {
        return places;
    }

    /**
     * Sets this Activity's places list.
     * @param places the optional Activity places to set.
     */
    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    /**
     * Add place to this Activity's places list.
     * @param place the Activity place to add.
     * @return a reference to this object.
     */
    public Activity addPlace(Place place) {
        if (places == null) {
            places = new ArrayList<Place>();
        }
        places.add(place);
        return this;
    }

    /**
     * Add collection of places to this Activity's places list.
     * @param places the Activity places to add.
     * @return a reference to this object.
     */
    public Activity addPlaces(Collection<Place> places) {
        if (places != null) {
            if (this.places == null) {
                this.places = new ArrayList<Place>(places.size());
            }
            this.places.addAll(places);
        }
        return this;
    }

    /**
     * Retrieves this Activity's actors list.
     * @return the Activity actors list.
     */
    public List<Actor> getActors() {
        return actors;
    }

    /**
     * Sets this Activity's actors list.
     * @param actors the optional Activity actors to set.
     */
    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    /**
     * Add actor to this Activity's actors list.
     * @param actor the Activity actor to add.
     * @return a reference to this object.
     */
    public Activity addActor(Actor actor) {
        if (actors == null) {
            actors = new ArrayList<Actor>();
        }
        actors.add(actor);
        return this;
    }

    /**
     * Add collection of actors to this Activity's actors list.
     * @param actors the Activity actors to add.
     * @return a reference to this object.
     */
    public Activity addActors(Collection<Actor> actors) {
        if (actors != null) {
            if (this.actors == null) {
                this.actors = new ArrayList<Actor>(actors.size());
            }
            this.actors.addAll(actors);
        }
        return this;
    }

    /**
     * Retrieves this Activity's destination urls list.
     * @return the Activity destination url list.
     */
    public List<GnipUrl> getDestinationUrls() {
        return destinationUrls;
    }

    /**
     * Sets this Activity's destination urls list.
     * @param destinationUrls the optional Activity destination urls to set
     */
    public void setDestinationUrls(List<GnipUrl> destinationUrls) {
        this.destinationUrls = destinationUrls;
    }

    /**
     * Add destination url to this Activity's destination urls list.
     * @param destinationUrl the Activity destination url to add.
     * @return a reference to this object.
     */
    public Activity addDestinationUrl(GnipUrl destinationUrl) {
        if (destinationUrls == null) {
            destinationUrls = new ArrayList<GnipUrl>();
        }
        destinationUrls.add(destinationUrl);
        return this;
    }

    /**
     * Add collection of destination urls to this Activity's destination urls list.
     * @param destinationUrls the Activity destination urls to add.
     * @return a reference to this object.
     */
    public Activity addDestinationUrls(Collection<GnipUrl> destinationUrls) {
        if (destinationUrls != null) {
            if (this.destinationUrls == null) {
                this.destinationUrls = new ArrayList<GnipUrl>(destinationUrls.size());
            }
            this.destinationUrls.addAll(destinationUrls);
        }
        return this;
    }

    /**
     * Retrieves this Activity's tag values list.
     * @return the Activity tags list.
     */
    public List<GnipValue> getTags() {
        return tags;
    }

    /**
     * Sets this Activity's tags list.
     * @param tags the optional Activity tags to set
     */
    public void setTags(List<GnipValue> tags) {
        this.tags = tags;
    }

    /**
     * Add tag to this Activity's tags list.
     * @param tag the Activity tag to add.
     * @return a reference to this object.
     */
    public Activity addTag(GnipValue tag) {
        if (tags == null) {
            tags = new ArrayList<GnipValue>();
        }
        tags.add(tag);
        return this;
    }

    /**
     * Add collection of tags to this Activity's tags list.
     * @param tags the Activity tags to add.
     * @return a reference to this object.
     */
    public Activity addTags(Collection<GnipValue> tags) {
        if (tags != null) {
            if (this.tags == null) {
                this.tags = new ArrayList<GnipValue>(tags.size());
            }
            this.tags.addAll(tags);
        }
        return this;
    }

    /**
     * Retrieves this Activity's to values list.
     * @return the Activity to values list.
     */
    public List<GnipValue> getTos() {
        return tos;
    }

    /**
     * Sets this Activity's to values list.
     * @param tos the optional Activity to values list to set
     */
    public void setTos(List<GnipValue> tos) {
        this.tos = tos;
    }

    /**
     * Add to value to this Activity's to values list.
     * @param to the Activity to value to add.
     * @return a reference to this object.
     */
    public Activity addTo(GnipValue to) {
        if (tos == null) {
            tos = new ArrayList<GnipValue>();
        }
        tos.add(to);
        return this;
    }

    /**
     * Add collection of to values to this Activity's to values list.
     * @param tos the Activity to values to add.
     * @return a reference to this object.
     */
    public Activity addTos(Collection<GnipValue> tos) {
        if (tos != null) {
            if (this.tos == null) {
                this.tos = new ArrayList<GnipValue>(tos.size());
            }
            this.tos.addAll(tos);
        }
        return this;
    }

    /**
     * Retrieves this Activity's to regarding urls list.
     * @return the Activity regarding urls list.
     */
    public List<GnipUrl> getRegardingUrls() {
        return regardingUrls;
    }

    /**
     * Sets this Activity's regarding urls list.
     * @param regardingUrls the optional Activity regarding urls to set
     */
    public void setRegardingUrls(List<GnipUrl> regardingUrls) {
        this.regardingUrls = regardingUrls;
    }

    /**
     * Add regarding url to this Activity's regarding urls list.
     * @param regardingUrl the Activity regarding url to add.
     * @return a reference to this object.
     */
    public Activity addRegardingUrl(GnipUrl regardingUrl) {
        if (regardingUrls == null) {
            regardingUrls = new ArrayList<GnipUrl>();
        }
        regardingUrls.add(regardingUrl);
        return this;
    }

    /**
     * Add collection of regarding urls to this Activity's regarding urls list.
     * @param regardingUrls the Activity regarding urls to add.
     * @return a reference to this object.
     */
    public Activity addRegardingUrls(Collection<GnipUrl> regardingUrls) {
        if (regardingUrls != null) {
            if (this.regardingUrls == null) {
                this.regardingUrls = new ArrayList<GnipUrl>(regardingUrls.size());
            }
            this.regardingUrls.addAll(regardingUrls);
        }
        return this;
    }

    /**
     * Retrieves this Activity's payload.
     * @return the Activity payload.
     */
    public Payload getPayload() {
        return payload;
    }

    /**
     * Sets this Activity's payload.
     * @param payload the optional Activity payload to set
     */
    public void setPayload(Payload payload) {
        this.payload = payload;
    }

    /**
     * Retrieves the value from the {@link Activity} that is associated with the {@link RuleType}.
     * @param ruleType the rule type
     * @return the values associated with the rule type
     */
    @SuppressWarnings("unchecked")
    public List getValue(RuleType ruleType) {
        switch (ruleType) {
            case ACTOR:
                return actors;
            case REGARDING:
                return regardingUrls;
            case SOURCE:
                return sources;
            case TAG:
                return tags;
            case TO:
                return tos;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Activity activity = (Activity) o;

        // note: do not factor payload into comparison since it is
        // optionally returned as part of an activity based on
        // full or notification forms.
        if (at != null ? !at.equals(activity.at) : activity.at != null) return false;
        if (action != null ? !action.equals(activity.action) : activity.action != null) return false;
        if (activityID != null ? !activityID.equals(activity.activityID) : activity.activityID != null) return false;
        if (url != null ? !url.equals(activity.url) : activity.url != null) return false;
        if (sources != null ? !sources.equals(activity.sources) : activity.sources != null) return false;
        if (keywords != null ? !keywords.equals(activity.keywords) : activity.keywords != null) return false;
        if (places != null ? !places.equals(activity.places) : activity.places != null) return false;
        if (actors != null ? !actors.equals(activity.actors) : activity.actors != null) return false;
        if (destinationUrls != null ? !destinationUrls.equals(activity.destinationUrls) : activity.destinationUrls != null) return false;
        if (tags != null ? !tags.equals(activity.tags) : activity.tags != null) return false;
        if (tos != null ? !tos.equals(activity.tos) : activity.tos != null) return false;

        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {        
        // note: do not factor payload into hashCode since it is
        // optionally returned as part of an activity based on
        // full or notification forms.
        int result = (at != null ? at.hashCode() : 0);
        result = 31 * result + (action != null ? action.hashCode() : 0);
        result = 31 * result + (activityID != null ? activityID.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (sources != null ? sources.hashCode() : 0);
        result = 31 * result + (keywords != null ? keywords.hashCode() : 0);
        result = 31 * result + (places != null ? places.hashCode() : 0);
        result = 31 * result + (actors != null ? actors.hashCode() : 0);
        result = 31 * result + (destinationUrls != null ? destinationUrls.hashCode() : 0);
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = 31 * result + (tos != null ? tos.hashCode() : 0);
        result = 31 * result + (regardingUrls != null ? regardingUrls.hashCode() : 0);
        return result;
    }

    private static XMLGregorianCalendar toXMLGregorianCalendar(DateTime dateTime) {
        return datatypeFactory.newXMLGregorianCalendar((dateTime != null ? dateTime : new DateTime()).toGregorianCalendar());
    }

    private static DateTime fromXMLGregorianCalendar(XMLGregorianCalendar xmlTime) {
        GregorianCalendar calendar = xmlTime.toGregorianCalendar();
        return new DateTime(calendar.getTimeInMillis(), DateTimeZone.forTimeZone(calendar.getTimeZone()));
    }
}