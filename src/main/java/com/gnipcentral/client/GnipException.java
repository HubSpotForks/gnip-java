package com.gnipcentral.client;

/**
 * Exception thrown when problems occur connecting to a
 * Gnip server or marshalling data. 
 */
public class GnipException extends Exception {

    private static final long serialVersionUID = 1L;

    public GnipException() {
        super();
    }

    public GnipException(String message) {
        super(message);
    }

    public GnipException(Throwable cause) {
        super(cause);
    }

    public GnipException(String message, Throwable cause) {
        super(message, cause);
    }
}
