package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.*;

/**
 * Model object that represents a generic URL of a Gnip {@link Activity}. For activities
 * and simple notifications, the {@link GnipUrl} contains information that was originally
 * sent to the {@link Publisher}.
 * <br/>
 * <br/>
 * Typically, a Gnip user would create a {@link GnipUrl} object in order to publish data
 * into Gnip and would receive one from an {@link Activity} retrieved via a
 * {@link com.gnipcentral.client.GnipConnection}.
 */
@XmlRootElement(name = "gnipURL")
@XmlAccessorType(XmlAccessType.FIELD)
public class GnipUrl {
    
    @XmlValue
    @XmlSchemaType(name = "anyURI")
    private String url;
    @XmlAttribute(name = "metaURL")
    @XmlSchemaType(name = "anyURI")
    private String metaUrl;
    
    @SuppressWarnings("unused")
    private GnipUrl() {
        /* private ctor for jaxb */
    }
    
    /**
     * Create an Activity GnipUrl object with the specified URL.
     * @param url the URL.
     */
    public GnipUrl(String url) {
        if (url == null) {
            throw new IllegalArgumentException("Invalid url specified '"+url+"'");
        }

        this.url = url;
    }

    /**
     * Create an Activity GnipUrl object with the specified URL and
     * meta URL.
     * @param url the GnipUrl URL.
     * @param metaUrl the optional meta URL associated with the GnipUrl.
     */
    public GnipUrl(String url, String metaUrl) {
        this(url);
        this.metaUrl = metaUrl;
    }
    
    /**
     * Retrieves this GnipUrl's URL.
     * @return the URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Retrieves the meta URL associated with this GnipUrl.
     * @return the meta URL.
     */
    public String getMetaUrl() {
        return metaUrl;
    }

    /**
     * Set the meta URL associated with this GnipUrl.
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
        if (o == null || !(o instanceof GnipUrl)) return false;

        GnipUrl gnipUrl = (GnipUrl) o;

        if (url != null ? !url.equals(gnipUrl.url) : gnipUrl.url != null) return false;
        if (metaUrl != null ? !metaUrl.equals(gnipUrl.metaUrl) : gnipUrl.metaUrl != null) return false;

        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = (url != null ? url.hashCode() : 0);
        result = 31 * result + (metaUrl != null ? metaUrl.hashCode() : 0);
        return result;
    }
}
