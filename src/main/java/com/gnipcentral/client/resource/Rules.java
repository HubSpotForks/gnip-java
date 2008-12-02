package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "rules")
public class Rules implements Resource {

    @XmlElement(name = "rule", required = true, type = Rule.class)
    private HashSet<Rule> rules;

    public Rules() {
    }

    public Set<Rule> getRules() {
        if (rules == null) {
            rules = new HashSet<Rule>();
        }
        return rules;
    }

    public Rules addRule(Rule rule) {
        getRules().add(rule);
        return this;
    }

    public Rules addRules(Collection<Rule> newRules) {
        getRules().addAll(newRules);
        return this;
    }
}
