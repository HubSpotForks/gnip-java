package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;

/**
 * Container class that wraps a set of {@link Rule}s.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "rules")
public class Rules implements Resource {

    @XmlElement(name = "rule", required = true, type = Rule.class)
    private Set<Rule> rules;

    /**
     * Default constructor.
     */
    public Rules() {
    }

    /**
     * Construct with a set of {@link Rule}s.
     * @param rules the rules to start with
     */
    public Rules(Rule ... rules) {
        this.rules = new HashSet<Rule>();
        for (Rule rule : rules) {
            this.rules.add(rule);
        }
    }

    /**
     * Get the set of {@link Rule}s.
     * @return the set of rules.
     */
    public Set<Rule> getRules() {
        return rules;
    }

    /**
     * Set the set of {@link Rule}s.
     * @param rules the set of rules.
     */
    public void setRules(Set<Rule> rules) {
        this.rules = rules;
    }

    /**
     * Add a single {@link Rule}.
     * @param rule
     * @return a reference to this object
     */
    public Rules add(Rule rule) {
        if (rules == null) {
            rules = new HashSet<Rule>();            
        }
        rules.add(rule);
        return this;
    }

    /**
     * Add a collection of {@link Rule}s.
     * @param rules the
     * @return a reference to this object
     */
    public Rules addAll(Collection<Rule> rules) {
        if (rules != null) {
            if (this.rules == null) {
                this.rules = new HashSet<Rule>();            
            }
            this.rules.addAll(rules);
        }
        return this;
    }
}
