package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.*;

/**
 * Represents an error returned to a client from the Gnip server.  This object provides a way to get any
 * error information that the server sent in response to a request that caused a server-side error.
 */
@XmlRootElement(name="error")
@XmlAccessorType(XmlAccessType.FIELD)
public class Error {

    @XmlValue
    private String message;

    private Error() {
        /* private ctor for jaxb */
    }

    /**
     * Retrieves the error message.
     */
    public String getMessage() {
        return message;
    }
}
