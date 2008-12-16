package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.*;
import java.util.Set;
import java.util.HashSet;

/**
 * A model object that references a Gnip publisher.  A Gnip Publisher receives incoming {@link Activity} data
 * and passes that data along in two forms:
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
@XmlType(name = "publisherType")
@XmlRootElement(name = "publisher")
public class Publisher implements Resource {

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "uriTypeSafe")    
    private String name;

    @XmlElementWrapper(name="supportedRuleTypes")
    @XmlElement(name = "type", required = true)
    protected HashSet<RuleType> ruleTypes;
    
    @SuppressWarnings({"UnusedDeclaration"})
    private Publisher() {
        // private ctor for jaxb
    }

    /**
     * Basic constructor.
     * @param name the name of the publisher
     */
    public Publisher(String name) {
        this.name = name;
        this.ruleTypes = new HashSet<RuleType>();
    }

    /**
     * Create a Publisher model object with the given {@param name} and {@param ruleTypes}.  Note, calling
     * this constructor does <b>not</b> create the publisher on a Gnip server.
     *
     * @param name the publisher's name
     * @param ruleTypes the publisher's rule types
     */
    public Publisher(String name, Set<RuleType> ruleTypes) {
        this.name = name;
        this.ruleTypes = new HashSet<RuleType>(ruleTypes);
    }

    /**
     * Create a Publisher model object with the given {@param name} and {@param ruleTypes}.  Note, calling
     * this constructor does <b>not</b> create the publisher on a Gnip server.
     *
     * @param name the publisher's name
     * @param ruleTypes the publisher's rule types
     */
    public Publisher(String name, RuleType ... ruleTypes) {
        this.name = name;
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
     * Get the {@link Set} of {@link RuleType rule types} that this publisher supports.  A publisher
     * declares its rule types in order to constrain the types of {@link Rule rules} that can be
     * used in {@link Filter filters} associated with the publisher.
     * @return
     */
    public Set<RuleType> getSupportedRuleTypes() {
        return new HashSet<RuleType>(ruleTypes);
    }

    /**
     * Add a new rule type for this publisher.  Note, calling this method does <b>not</b> change the publisher
     * on a Gnip server.  Typically, only the publisher's owner can call this method.
     * @param ruleType the rule type to add
     */
    public void addSupportedRuleType(RuleType ruleType) {
        ruleTypes.add(ruleType);
    }

    /**
     * Remove a rule type from this publisher.  Note, calling this method does <b>not</b> change the publisher on the
     * Gnip server.  Typically, only the publisher's owner can call this method.
     * @param ruleType the rule type to remove
     */
    public void removeSupportedRuleType(RuleType ruleType) {
        ruleTypes.remove(ruleType);
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
        return ruleTypes.contains(ruleType);
    }

    @SuppressWarnings({"RedundantIfStatement"})    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Publisher publisher = (Publisher) o;

        if (name != null ? !name.equals(publisher.name) : publisher.name != null) return false;

        return true;
    }

    public int hashCode() {
        return (name != null ? name.hashCode() : 0);
    }
}