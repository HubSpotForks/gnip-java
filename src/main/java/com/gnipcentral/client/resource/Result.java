package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.*;

/**
 * Represents a result message returned to a client from the Gnip server. This object provides
 * a way to get any information that the server sent in response to a request.
 */
@XmlRootElement(name="result")
@XmlAccessorType(XmlAccessType.FIELD)
public class Result {

    public static final String SUCCESS_MESSAGE = "Success";

    @XmlValue
    private String message;

    private Result() {
        /* private ctor for jaxb */
    }

    /**
     * Retrieves the result message.
     * @return message string.
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Tests result message for success.
     * @return flag indicating result success.
     */
    public boolean isSuccess() {
        return (SUCCESS_MESSAGE.equals(message));
    }
}
