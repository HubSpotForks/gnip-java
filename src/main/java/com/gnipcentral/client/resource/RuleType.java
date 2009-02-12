package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.*;

/**
 * Enumeration of the rule types that are supported by Gnip {@link Filter}s.
 */
@XmlEnum
public enum RuleType {

    /**
     * <i>Actor</i> rules.
     */
    @XmlEnumValue("actor")
    ACTOR("actor"),

    /**
     * <i>Regarding</i> rule.
     */
    @XmlEnumValue("regarding")
    REGARDING("regarding"),

    /**
     * <i>Source</i> rule.
     */
    @XmlEnumValue("source")
    SOURCE("source"),

    /**
     * <i>Tag</i> rules.
     */
    @XmlEnumValue("tag")
    TAG("tag"),

    /**
     * <i>To</i> rule.
     */
    @XmlEnumValue("to")
    TO("to");

    @XmlTransient
    private final String value;

    private RuleType(String v) {
        value = v;
    }

    /**
     * Retrieves the String-based representation of a {@link RuleType}.
     * @return
     */
    public String value() {
        return value;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return value();
    }

    /**
     * Parse a {@link RuleType} from its {@link String} representation.
     * @param value
     * @return the parsed {@link RuleType} if the String matched a valid rule type
     * @throws IllegalArgumentException if the String failed to match a valid rule type
     */
    public static RuleType fromValue(String value) {
        for (RuleType c : RuleType.values()) {
            if (c.value.equalsIgnoreCase(value)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Invalid rule type '" + value + "'");
    }
}