package com.gnipcentral.client.resource;

import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

public class Translator {

    private static JAXBContext context;

    static {
        try {
            context = JAXBContext.newInstance(Activities.class, Activity.class, Error.class, Publishers.class, Publisher.class, Filter.class, Rule.class, Rules.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static Activity parseActivity(String input) throws JAXBException {
        return (Activity) getUnmarshaller().unmarshal(new ByteArrayInputStream(input.getBytes(Charset.forName("UTF-8"))));
    }

    public static Error parseError(InputStream input) throws JAXBException {
        return (Error) getUnmarshaller().unmarshal(input);
    }

    public static Activities parseActivities(InputSource input) throws JAXBException {
        return (Activities) getUnmarshaller().unmarshal(input);
    }

    public static Activities parseActivities(InputStream input) throws JAXBException {
        return (Activities) getUnmarshaller().unmarshal(input);
    }

    public static Filter parseFilter(InputSource input) throws JAXBException {
        return (Filter) getUnmarshaller().unmarshal(input);
    }

    public static Publisher parsePublisher(InputSource input) throws JAXBException {
        return (Publisher) getUnmarshaller().unmarshal(input);
    }

    public static Publishers parsePublishers(InputSource input) throws JAXBException {
        return (Publishers) getUnmarshaller().unmarshal(input);
    }

    public static Resource parseResource(InputStream inputStream) throws JAXBException {
        return (Resource) getUnmarshaller().unmarshal(inputStream);
    }

    public static void marshall(Object object, OutputStream outputStream) throws JAXBException {
        marshall(object, outputStream, false);
    }

    public static void marshall(Object object, OutputStream outputStream, boolean fragment) throws JAXBException {
        getMarshaller(fragment).marshal(object, outputStream);
    }

    public static String marshall(Object object) throws JAXBException {
        return marshall(object, false);
    }

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
