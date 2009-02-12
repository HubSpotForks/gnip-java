package com.gnipcentral.client.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.GZIPInputStream;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.*;

import org.apache.commons.codec.binary.Base64;

/**
 * Model object that represents the Payload of a Gnip {@link com.gnipcentral.client.resource.Activity}.  For
 * activities that support full-data, as opposed to just simple notifications, the {@link Payload} contains
 * the raw activity data that was originally sent to the {@link Publisher}.
 * <br/>
 * <br/>
 * Typically, a Gnip user would create a payload object in order to publish data into Gnip and would receive a
 * payload object from an {@link Activity} retrieved via a {@link com.gnipcentral.client.GnipConnection}.
 * <br/>
 * <br/>
 * A payload's body is expected to be formatted as a String.  A payload's raw can be either a normal or a
 * Base64 encoded String in order to allow the caller to control the raw encoding.  When this object is serialized
 * into XML, the raw String will <i>always</i> be Base64 encoded.
 */
@XmlRootElement(name = "payload")
@XmlAccessorType(XmlAccessType.FIELD)
public class Payload {

    @XmlElement
    private String title;
    @XmlElement
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String body;
    @XmlElement(name = "mediaURL", type = MediaUrl.class)
    private List<MediaUrl> mediaUrls;
    @XmlElement(required = true)
    @XmlInlineBinaryData
    private String raw;

    @XmlTransient
    private String decodedRaw;

    @SuppressWarnings("unused")
    private Payload() {
        // empty constructor for jaxb
    }

    /**
     * Create a basic payload object with specified title, body, and raw data.  Typically, this method would
     * be called when creating an {@link Activity} to publish into Gnip.  The <code>raw</code> passed here
     * should <i>not</i> be Base64 encoded; if already encoded, it will be re-encoded. To pass an encoded
     * <code>raw</code> use the {@link #Payload(String, String, String, boolean)} constructor. 
     * @param title the optional value of the title.
     * @param body the body of the activity
     * @param raw an un-encoded representation of the activity payload
     */
    public Payload(String title, String body, String raw) {
        this(title, body, raw, false);
    }

    /**
     * Create a payload object with specified title, body, and raw data.  This constructor can be used to
     * create a Payload with an already encoded raw or with an unencoded raw by setting the {@param isEncoded}
     * flag correctly.
     * @param title the optional value of the title.
     * @param body the optional value of the body
     * @param raw the required value of the activity's raw data
     * @param isEncoded a flag set for whether the raw data is encoded
     */
    public Payload(String title, String body, String raw, boolean isEncoded) {
        this(title, body, null, raw, isEncoded);
    }

    /**
     * Create a payload object with all arguments.  This constructor can be used to create a Payload with
     * an already encoded raw or with an unencoded raw by setting the {@param isEncoded} flag correctly.
     * @param title the optional value of the title.
     * @param body the optional value of the body.
     * @param mediaUrls the optional media urls list.
     * @param raw the required value of the activity's raw data.
     * @param isEncoded flag indicating whether raw data is encoded.
     */
    public Payload(String title, String body, List<MediaUrl> mediaUrls, String raw, boolean isEncoded) {
        if (raw == null) {
            throw new IllegalArgumentException("Invalid raw payload specified '"+raw+"'");
        }

        this.title = title;
        this.body = body;
        this.mediaUrls = mediaUrls;
        this.raw = isEncoded ? raw : encode(raw);
    }

    /**
     * Retrieves this payload's title.
     * @return the title string.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets this payload's title.
     * @param body the body title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Retrieves this payload's body.
     * @return the body string.
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets this payload's body.
     * @param body the body string.
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Retrieves this payload's list of media urls.
     * @return the list of media urls.
     */
    public List<MediaUrl> getMediaUrls() {
        return mediaUrls;
    }

    /**
     * Set this payload's list of media urls.
     * @param mediaUrls the list of media urls.
     */
    public void setMediaUrls(List<MediaUrl> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }    
    
    /**
     * Add media url to this payload's list of media urls.
     * @param mediaUrl the media url to add.
     * @return a reference to this object.
     */
    public Payload addMediaUrl(MediaUrl mediaUrl) {
        if (mediaUrls == null) {
            mediaUrls = new ArrayList<MediaUrl>();
        }
        mediaUrls.add(mediaUrl);
        return this;
    }    
    
    /**
     * Add a collection of media urls to this payload's list of media urls.
     * @param mediaUrls the list of media url to add.
     * @return a reference to this object.
     */
    public Payload addMediaUrls(Collection<MediaUrl> mediaUrls) {
        if (mediaUrls != null) {
            if (this.mediaUrls == null) {
                this.mediaUrls = new ArrayList<MediaUrl>(mediaUrls.size());
            }
            this.mediaUrls.addAll(mediaUrls);
        }
        return this;
    }    
    
    /**
     * Retrieves this Payload's raw data. This value will be Base64 encoded.
     * @return the raw, encoded string.
     */
    public String getRaw() {
        return raw;
    }

    /**
     * Set this Payload's raw data. This value will be Base64 encoded.
     * @param raw the raw, encoded string.
     */
    public void setRaw(String raw) {
        this.raw = raw;
        decodedRaw = null;
    }

    /**
     * Retrieves the payload's raw value after being Base64 decoded.
     * @return
     */
    public String getDecodedRaw() {
        return decodedRaw != null ? decodedRaw : (decodedRaw = decode(raw));
    }

    private String encode(String string) {
        if(null == string)
            return "";

        ByteArrayOutputStream stream = null;
        try {
            stream = new ByteArrayOutputStream();
            GZIPOutputStream gos = new GZIPOutputStream(stream);
            byte[] bytes = string.getBytes();
            gos.write(bytes, 0, bytes.length);
            gos.finish();
            bytes = Base64.encodeBase64(stream.toByteArray());
            return new String(bytes);
        }
        catch(IOException e) {
            throw new IllegalStateException("Exception occurred attempting to encode a payload body", e);
        }
        finally {
            try{
                if(stream != null) stream.close();
            }
            catch(IOException e) {/*ignore*/}
        }
    }
    
    private String decode(String string) {
        ByteArrayInputStream stream = null;
        try {
            byte[] bytes = string.getBytes(Charset.forName("UTF-8"));
            bytes = Base64.decodeBase64(bytes);
            stream = new ByteArrayInputStream(bytes);
            GZIPInputStream gis = new GZIPInputStream(stream);
            BufferedReader reader = new BufferedReader(new InputStreamReader(gis));
            String line;
            StringBuilder sb = new StringBuilder();
            while((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
        catch(IOException e) {
            throw new IllegalStateException("Exception occurred attempting to decode a payload body", e);
        }
        finally {
            try{
                if(stream != null) stream.close();
            }
            catch(IOException e) {/*ignore*/}
        }
    }
}