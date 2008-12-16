package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.*;

/**
 * A model object that represents a Gnip Rule.  A set of rules are set on a {@link Filter} and are used to match
 * {@link Activity} objects that flow through a {@link Publisher publishers}.
 */
@XmlRootElement(name = "rule")
@XmlAccessorType(XmlAccessType.FIELD)
public class Rule implements Resource {

    @XmlAttribute(required = true)
    @XmlSchemaType(name = "type")
    private RuleType type;

    @XmlAttribute(name = "value", required = true)
    private String value;

    @SuppressWarnings({"UnusedDeclaration"})
    private Rule() {
        // private constructor for jaxb
    }

    /**
     * A simple constructor.
     * @param type the rule type
     * @param value the rule's value
     */
    public Rule(RuleType type, String value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Retrieves the rule's type.
     * @return the type
     */
    public RuleType getType() {
        return type;
    }

    /**
     * Retrieves the rule's value.
     * @return the value
     */
    public String getValue() {
        return value;
    }

    @SuppressWarnings({"RedundantIfStatement"})
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Rule rule = (Rule) o;

        if (type != null ? !type.equals(rule.type) : rule.type != null) return false;
        if (value != null ? !value.equals(rule.value) : rule.value != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (type != null ? type.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }
}