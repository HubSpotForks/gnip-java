package com.gnipcentral.client.resource;

import javax.xml.bind.annotation.*;

@XmlRootElement(name="error")
@XmlAccessorType(XmlAccessType.FIELD)
public class Error {

    @XmlValue
    private String message;

    public Error() {
        // private ctor for jaxb
    }

    public String getMessage() {
        return message;
    }
}
