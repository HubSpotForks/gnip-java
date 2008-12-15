package com.gnipcentral.client;

/**
 * Exception thrown when problems result interacting with a Gnip server.  This exception can be thrown
 * when data is marshalled or unmarshalled.  It can also be thrown when errors are returned from the
 * Gnip server including responses with non-200 status codes including problems logging in, access
 * to non-existent XML documents, etc.
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
