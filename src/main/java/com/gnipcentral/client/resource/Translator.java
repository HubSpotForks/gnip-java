package com.gnipcentral.client.resource;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.xml.sax.InputSource;

/**
 * A translation abstraction for converting between Gnip's Java model objects and XML in the form of strings and streams.
 * This class is public but is <b>not</b> intended for external use.  
 */
public class Translator {

    private static JAXBContext context;

    static {
        try {
            context = JAXBContext.newInstance(Error.class, Results.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parse {@link Activities} from an {@link InputSource}.
     * @param input the XML input
     * @return the model object
     * @throws JAXBException if an error occurs unmarshalling the object from XML
     */
    public static Results parseResults(InputSource input) throws JAXBException {
        return (Results) getUnmarshaller().unmarshal(input);
    }

    /**
     * Parse {@link Activities} from an {@link InputStream}.
     * @param input the XML input
     * @return the model object
     * @throws JAXBException if an error occurs unmarshalling the object from XML
     */
    public static Results parseResults(InputStream input) throws JAXBException {
        return (Results) getUnmarshaller().unmarshal(input);
    }

    /**
     * Parse an {@link Error} from a {@link InputStream}.
     * @param input the XML input
     * @return the model object
     * @throws JAXBException if an error occurs unmarshalling the object from XML
     */
    public static Error parseError(InputStream input) throws JAXBException {
        return (Error) getUnmarshaller().unmarshal(input);
    }

    /**
     * Marshall an Object into XML and send it directly into the {@link OutputStream}.
     * @param object the Gnip model object to marshall
     * @param output the stream to write to
     * @throws JAXBException if an exception occurs during marshalling
     */
    public static void marshall(Object object, OutputStream output) throws JAXBException {
        marshall(object, output, false);
    }

    /**
     * Marshall an Object into XML and send it directly into the {@link OutputStream}.
     * @param object the Gnip model object to marshall
     * @param output the stream to write to
     * @param fragment whether the XML is a fragment
     * @throws JAXBException if an exception occurs during marshalling
     */
    public static void marshall(Object object, OutputStream output, boolean fragment) throws JAXBException {
        getMarshaller(fragment).marshal(object, output);
    }

    /**
     * Marshall an Object into an XML {@link String}.
     * @param object the Gnip model object to marshall
     * @return marshalled XML
     * @throws JAXBException if an exception occurs during marshalling
     */
    public static String marshall(Object object) throws JAXBException {
        return marshall(object, false);
    }

    /**
     * Marshall an Object into an XML {@link String}.
     * @param object the Gnip model object to marshall
     * @param fragment whether the model object is a fragment
     * @return marshalled XML
     * @throws JAXBException if an exception occurs during marshalling
     */
    public static String marshall(Object object, boolean fragment) throws JAXBException {
        StringWriter stringWriter = new StringWriter();
        getMarshaller(fragment).marshal(object, stringWriter);
        return stringWriter.toString();
    }

    private static Marshaller getMarshaller(boolean fragment) throws JAXBException {
        Marshaller marshaller = context.createMarshaller();
        marshaller.setSchema(GnipSchema.schema);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, fragment);
        return marshaller;
    }


    private static Unmarshaller getUnmarshaller() throws JAXBException {
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setSchema(GnipSchema.schema);
        return unmarshaller;
    }
}
