package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.*;

/**
 * Model object that represents a generic value of a Gnip {@link Activity}. For activities
 * and simple notifications, the {@link GnipValue} contains information that was originally
 * sent to the {@link Publisher}.
 * <br/>
 * <br/>
 * Typically, a Gnip user would create a {@link GnipValue} object in order to publish data
 * into Gnip and would receive one from an {@link Activity} retrieved via a
 * {@link com.gnipcentral.client.GnipConnection}.
 */
@XmlRootElement(name = "gnipValue")
@XmlAccessorType(XmlAccessType.FIELD)
public class GnipValue {
    
    @XmlValue
    private String value;
    @XmlAttribute(name = "metaURL")
    @XmlSchemaType(name = "anyURI")
    private String metaUrl;
    
    protected GnipValue() {
        /* private ctor for jaxb */
    }
    
    /**
     * Create an Activity GnipValue object with the specified value.
     * @param value the value.
     */
    public GnipValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Invalid value specified '"+value+"'");
        }

        this.value = value;
    }

    /**
     * Create an Activity GnipValue object with the specified value
     * and meta URL.
     * @param value the GnipValue value.
     * @param metaUrl the optional meta URL associated with the GnipValue.
     */
    public GnipValue(String value, String metaUrl) {
        this(value);
        this.metaUrl = metaUrl;
    }
    
    /**
     * Retrieves this GnipValue's value.
     * @return the value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Retrieves the meta URL associated with this GnipValue.
     * @return the meta URL.
     */
    public String getMetaUrl() {
        return metaUrl;
    }

    /**
     * Set the meta URL associated with this GnipValue.
     * @param metaUrl the meta URL.
     */
    public void setMetaUrl(String metaUrl) {
        this.metaUrl = metaUrl;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof GnipValue)) return false;

        GnipValue gnipValue = (GnipValue) o;

        if (value != null ? !value.equals(gnipValue.value) : gnipValue.value != null) return false;
        if (metaUrl != null ? !metaUrl.equals(gnipValue.metaUrl) : gnipValue.metaUrl != null) return false;

        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = (value != null ? value.hashCode() : 0);
        result = 31 * result + (metaUrl != null ? metaUrl.hashCode() : 0);
        return result;
    }
}
