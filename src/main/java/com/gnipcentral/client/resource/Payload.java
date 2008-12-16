package com.gnipcentral.client.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.GZIPInputStream;
import java.nio.charset.Charset;
import javax.xml.bind.annotation.XmlInlineBinaryData;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;

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

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    private String body;

    @XmlElement
    @XmlInlineBinaryData
    private String raw;

    private String decodedRaw = null;

    @SuppressWarnings({"UnusedDeclaration"})
    private Payload() {
        // empty constructor for jaxb
    }

    /**
     * Create a basic payload object.  Typically, this method would be called when creating an {@link Activity}
     * to publish into Gnip.  The <code>raw</code> passed here should <i>not</i> be Base64 encoded; if already
     * encoded, it will be re-encoded.  To pass an encoded <code>raw</code> use the {@link #Payload(String, String, boolean)}
     * constructor. 
     *
     * @param body the body of the activity
     * @param raw an un-encoded representation of the activity payload
     */
    public Payload(String body, String raw) {
        this(body, raw, false);
    }

    /**
     * Create a payload object.  This constructor can be used to create a Payload with an already encoded raw or
     * with an unencoded raw by setting the {@param isEncoded} flag correctly.
     * 
     * @param body the value of the body
     * @param raw the value of the activity's raw data
     * @param isEncoded a flag set for whether the raw data is encoded
     */
    public Payload(String body, String raw, boolean isEncoded) {
        this.body = body;
        this.raw = isEncoded ? raw : encode(raw);
    }

    /**
     * Retrieves the payload's body.
     * @return the body string
     */
    public String getBody() {
        return body;
    }

    /**
     * Retrieves the payload's raw data.  This value will be Base64 encoded.
     * @return the raw, encoded string
     */
    public String getRaw() {
        return raw;
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