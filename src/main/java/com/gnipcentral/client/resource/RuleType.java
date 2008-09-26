package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlType(name = "concernType")
@XmlEnum
public enum RuleType {

    @XmlEnumValue("actor")
    ACTOR("actor"),

    @XmlEnumValue("tag")
    TAG("tag"),

    @XmlEnumValue("to")
    TO("to"),

    @XmlEnumValue("regarding")
    REGARDING("regarding"),

    @XmlEnumValue("source")
    SOURCE("source");

    private final String value;

    private RuleType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RuleType fromValue(String v) {
        for (RuleType c : RuleType.values()) {
            if (c.value.equalsIgnoreCase(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}