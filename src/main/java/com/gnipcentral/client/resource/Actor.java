package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.*;

/**
 * Model object that represents an actor of a Gnip {@link Activity}. For activities and
 * simple notifications, the {@link Actor} contains information that was originally sent
 * to the {@link Publisher}.
 * <br/>
 * <br/>
 * Typically, a Gnip user would create an {@link Actor} object in order to publish data
 * into Gnip and would receive one from an {@link Activity} retrieved via a
 * {@link com.gnipcentral.client.GnipConnection}.
 * <br/>
 * <br/>
 * Actors can also be specified as part of a {@link Filter} rule. See {@link RuleType}.
 */
@XmlRootElement(name = "actor")
@XmlAccessorType(XmlAccessType.FIELD)
public class Actor extends GnipValue {

    @XmlAttribute
    private String uid;    
    
    @SuppressWarnings("unused")
    private Actor() {
        /* private ctor for jaxb */
    }
    
    /**
     * Create an Activity Actor object with the specified value.
     * @param value the Actor value.
     */
    public Actor(String value) {
        super(value);
    }

    /**
     * Create an Activity Actor object with the specified value,
     * uid, and meta URL.
     * @param value the Actor value.
     * @param uid the optional Actor uid.
     * @param metaUrl the optional meta URL associated with Actor.
     */
    public Actor(String value, String uid, String metaUrl) {
        super(value, metaUrl);
        this.uid = uid;
    }
    
    /**
     * Retrieves this Actor's uid.
     * @return the Actor uid.
     */
    public String getUid() {
        return uid;
    }

    /**
     * Set this Actor's uid.
     * @param uid the Actor uid.
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Actor actor = (Actor) o;

        if (uid != null ? !uid.equals(actor.uid) : actor.uid != null) return false;

        return super.equals(o);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (uid != null ? uid.hashCode() : 0);
        return result;
    }
}
