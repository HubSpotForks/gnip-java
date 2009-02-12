package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.*;

/**
 * Model object that represents a media URL of a Gnip {@link Payload}. For activities
 * and simple notifications, the {@link MediaUrl} contains information that was originally
 * sent to the {@link Publisher}.
 * <br/>
 * <br/>
 * Typically, a Gnip user would create a {@link MediaUrl} object in order to publish data
 * into Gnip and would receive one from an {@link Activity} {@link Payload} retrieved via
 * a {@link com.gnipcentral.client.GnipConnection}.
 */
@XmlRootElement(name = "mediaURL")
@XmlAccessorType(XmlAccessType.FIELD)
public class MediaUrl {
    
    @XmlValue
    @XmlSchemaType(name = "anyURI")
    private String url;
    @XmlAttribute
    private String width;
    @XmlAttribute
    private String height;
    @XmlAttribute
    private String duration;
    @XmlAttribute
    private String mimeType;
    @XmlAttribute
    private String type;
    
    @SuppressWarnings("unused")
    private MediaUrl() {
        /* private ctor for jaxb */
    }
    
    /**
     * Create an Activity MediaUrl object with the specified URL.
     * @param url the URL.
     */
    public MediaUrl(String url) {
        if (url == null) {
            throw new IllegalArgumentException("Invalid url specified '"+url+"'");
        }
        
        this.url = url;
    }

    /**
     * Create an Activity MediaUrl object with the specified URL and
     * all optional attributes.
     * @param url the MediaUrl URL.
     * @param width the optional MediaUrl width.
     * @param height the optional MediaUrl height.
     * @param duration the optional MediaUrl duration.
     * @param mimeType the optional MediaUrl mimeType.
     * @param type the optional MediaUrl type.
     */
    public MediaUrl(String url, String width, String height, String duration, String mimeType, String type) {
        this(url);
        this.width = width;
        this.height = height;
        this.duration = duration;
        this.mimeType = mimeType;
        this.type = type;
    }
    
    /**
     * Retrieves this MediaUrl's URL.
     * @return the URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Retrieves this MediaUrl' width.
     * @return the MediaUrl width.
     */
    public String getWidth() {
        return width;
    }

    /**
     * Set this MediaUrl's width.
     * @param width the MediaUrl width.
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * Retrieves this MediaUrl' height.
     * @return the MediaUrl height.
     */
    public String getHeight() {
        return height;
    }

    /**
     * Set this MediaUrl's height.
     * @param height the MediaUrl height.
     */
    public void setHeight(String height) {
        this.height = height;
    }

    /**
     * Retrieves this MediaUrl' duration.
     * @return the MediaUrl duration.
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Set this MediaUrl's duration.
     * @param duration the MediaUrl duration.
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * Retrieves this MediaUrl' mime type.
     * @return the MediaUrl mime type.
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Set this MediaUrl's mime type.
     * @param mimeType the MediaUrl mime type.
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Retrieves this MediaUrl' type.
     * @return the MediaUrl type.
     */
    public String getType() {
        return type;
    }

    /**
     * Set this MediaUrl's type.
     * @param type the MediaUrl type.
     */
    public void setType(String type) {
        this.type = type;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MediaUrl mediaUrl = (MediaUrl) o;

        if (url != null ? !url.equals(mediaUrl.url) : mediaUrl.url != null) return false;
        if (width != null ? !width.equals(mediaUrl.width) : mediaUrl.width != null) return false;
        if (height != null ? !height.equals(mediaUrl.height) : mediaUrl.height != null) return false;
        if (duration != null ? !duration.equals(mediaUrl.duration) : mediaUrl.duration != null) return false;
        if (mimeType != null ? !mimeType.equals(mediaUrl.mimeType) : mediaUrl.mimeType != null) return false;
        if (type != null ? !type.equals(mediaUrl.type) : mediaUrl.type != null) return false;

        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = (url != null ? url.hashCode() : 0);
        result = 31 * result + (width != null ? width.hashCode() : 0);
        result = 31 * result + (height != null ? height.hashCode() : 0);
        result = 31 * result + (duration != null ? duration.hashCode() : 0);
        result = 31 * result + (mimeType != null ? mimeType.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
