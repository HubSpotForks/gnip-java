package com.gnipcentral.client.resource;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;

/**
 * A wrapper that provides access to Gnip's XSD document; this class is intended to be used for JAXB marshalling
 * and should not be used by client code.
 */
class GnipSchema {

    public final static Schema schema;

    static {
        try {
            InputStream gnipSchema = Thread.currentThread().getContextClassLoader().getResourceAsStream("www/schema/gnip.xsd");
            schema = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(new StreamSource(gnipSchema));
        } catch (SAXException e) {
            throw new RuntimeException("Unable to find the Gnip schema." + e, e);
        }
    }
}