package com.gnipcentral.client.resource;

import java.util.Set;
import java.util.HashSet;

import javax.xml.bind.annotation.*;

/**
 * A model object that references a Gnip publisher.  All publishers are scoped by publisher type.  A Gnip
 * Publisher receives incoming {@link Activity} data and passes that data along in two forms:
 * <ol>
 * <li>as a stream of activity notifications</li>
 * <li>to {@link Filter} objects associated with the publisher which in turn make activity data available
 *     as either activities or notifications based on whether the {@link Filter} is configured as
 *     {@link Filter#setFullData(boolean)}.
 * </li>
 * </ol>
 * <br/>
 * This model object is typically used to create a new Publisher or to get the details of an existing Publisher.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "publisher")
public class Publisher implements Resource {

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "uriSafeType")    
    private String name;
    @XmlElementWrapper(name="supportedRuleTypes")
    @XmlElement(name = "type", required = true)
    private Set<RuleType> ruleTypes;
    
    @XmlTransient
    private PublisherType type;
    
    @SuppressWarnings("unused")
    private Publisher() {
        // private ctor for jaxb
    }

    /**
     * Basic constructor.
     * @param type the required type of the publisher
     * @param name the required name of the publisher
     */
    public Publisher(PublisherType type, String name) {
        if (type == null) {
            throw new IllegalArgumentException("Invalid type specified '"+type+"'");
        }
        if (name == null) {
            throw new IllegalArgumentException("Invalid name specified '"+name+"'");
        }

        this.type = type;
        this.name = name;
    }

    /**
     * Create a Publisher model object with the given {@param name} and {@param ruleTypes}.  Note, calling
     * this constructor does <b>not</b> create the publisher on a Gnip server.
     *
     * @param type the required type of the publisher
     * @param name the required publisher's name
     * @param ruleTypes the publisher's rule types
     */
    public Publisher(PublisherType type, String name, Set<RuleType> ruleTypes) {
        this(type, name);
        this.ruleTypes = ruleTypes;
    }

    /**
     * Create a Publisher model object with the given {@param name} and {@param ruleTypes}.  Note, calling
     * this constructor does <b>not</b> create the publisher on a Gnip server.
     *
     * @param type the required type of the publisher
     * @param name the required publisher's name
     * @param ruleTypes the publisher's rule types
     */
    public Publisher(PublisherType type, String name, RuleType ... ruleTypes) {
        this(type, name);
        this.ruleTypes = new HashSet<RuleType>(ruleTypes.length);
        for(RuleType ruleType : ruleTypes) {
            this.ruleTypes.add(ruleType);
        }
    }

    /**
     * Retrieves the name of this publisher.
     * @return the name of the publisher
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this publisher.
     * @param the name of the publisher
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the type of this publisher.
     * @return the type of the publisher
     */
    public PublisherType getType() {
        return type;
    }

    /**
     * Set the type of this publisher.
     * @param the type of the publisher
     */
    public void setType(PublisherType type) {
        this.type = type;
    }

    /**
     * Get the {@link Set} of {@link RuleType rule types} that this publisher supports.  A publisher
     * declares its rule types in order to constrain the types of {@link Rule rules} that can be
     * used in {@link Filter filters} associated with the publisher.
     * @return set of supported rule types.
     */
    public Set<RuleType> getSupportedRuleTypes() {
        return ruleTypes;
    }

    /**
     * Set the {@link Set} of {@link RuleType rule types} that this publisher supports.  A publisher
     * declares its rule types in order to constrain the types of {@link Rule rules} that can be
     * used in {@link Filter filters} associated with the publisher.
     */
    public void setSupportedRuleTypes(Set<RuleType> ruleTypes) {
        this.ruleTypes = ruleTypes;
    }

    /**
     * Add a new rule type for this publisher.  Note, calling this method does <b>not</b> change the publisher
     * on a Gnip server.  Typically, only the publisher's owner can call this method.
     * @param ruleType the rule type to add
     * @return a reference to this object
     */
    public Publisher addSupportedRuleType(RuleType ruleType) {
        if (ruleTypes == null) {
            ruleTypes = new HashSet<RuleType>();
        }
        ruleTypes.add(ruleType);
        return this;
    }

    /**
     * Remove a rule type from this publisher.  Note, calling this method does <b>not</b> change the publisher on the
     * Gnip server.  Typically, only the publisher's owner can call this method.
     * @param ruleType the rule type to remove
     * @return a reference to this object
     */
    public Publisher removeSupportedRuleType(RuleType ruleType) {
        ruleTypes.remove(ruleType);
        return this;
    }

    /**
     * Check to see if the this publisher supports the given {@link RuleType}.  Note, this method checks the
     * publisher's supported rule types if this {@link Publisher publisher's} representation was requested from
     * a Gnip server via {@link com.gnipcentral.client.GnipConnection#getPublisher(String)}.  This method can be
     * used to ensure that {@link Rule rules} added to a {@link Filter filter} are supported by the publisher.
     *  
     * @param ruleType the rule type to check
     * @return <code>true</code> if the publisher supports the rule type; <code>false</code> otherwise.
     */
    public boolean hasSupportedRuleType(RuleType ruleType) {
        return (ruleTypes != null && ruleTypes.contains(ruleType));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Publisher publisher = (Publisher) o;

        if (name != null ? !name.equals(publisher.name) : publisher.name != null) return false;
        if (ruleTypes != null ? !ruleTypes.equals(publisher.ruleTypes) : publisher.ruleTypes != null) return false;
        if (type != null ? !type.equals(publisher.type) : publisher.type != null) return false;

        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (ruleTypes != null ? ruleTypes.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}