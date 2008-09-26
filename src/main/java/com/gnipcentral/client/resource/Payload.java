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

    public Payload(String body, String raw) {
        this.body = body;
        this.raw = raw;
    }

    public Payload(String body, String raw, boolean isEncoded) {
        this.body = body;
        this.raw = isEncoded ? raw : encode(raw);
    }

    public String getBody() {
        return body;
    }

    public String getRaw() {
        return raw;
    }

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
            String line = null;
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