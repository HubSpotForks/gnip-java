package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.*;

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

    public Rule(RuleType type, String value) {
        this.type = type;
        this.value = value;
    }

    public RuleType getType() {
        return type;
    }

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