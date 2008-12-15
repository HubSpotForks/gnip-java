package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;

/**
 * Container class that wraps a set of {@link Rule}s.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "rules")
public class Rules implements Resource {

    @XmlElement(name = "rule", required = true, type = Rule.class)
    private HashSet<Rule> rules;

    /**
     * Default constructor.
     */
    public Rules() {
        rules = new HashSet<Rule>();
    }

    /**
     * Construct with a set of {@link Rule}s.
     * @param rules the rules to start with
     */
    public Rules(Rule ... rules) {
        this();
        assert rules != null;        
        for(Rule rule : rules) {
            addRule(rule);
        }
    }

    /**
     * Get the set of {@link Rule}s.
     * @return
     */
    public Set<Rule> getRules() {
        assert rules != null;
        return rules;
    }

    /**
     * Add a single {@link Rule}.
     * @param rule
     * @return a reference to this object
     */
    public Rules addRule(Rule rule) {
        assert rules != null;
        rules.add(rule);
        return this;
    }

    /**
     * Add a collection of {@link Rule}s.
     * @param rules the
     * @return a reference to this object
     */
    public Rules addRules(Collection<Rule> rules) {
        assert rules != null;
        this.rules.addAll(rules);
        return this;
    }
}
