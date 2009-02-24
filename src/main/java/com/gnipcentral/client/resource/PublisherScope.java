package com.gnipcentral.client.resource;

/**
 * Enumeration of the publisher scopes that are supported by Gnip {@link GnipConnection} APIs.
 */
public enum PublisherScope
{
    /**
     * <i>My</i> publishers.
     */
    MY("my"),
    
    /**
     * <i>Gnip</i> publishers.
     */
    GNIP("gnip");

    //
    // Public publishers not yet implemented.
    // 
    // Public("public");
    //

    private final String requestScope;

    private PublisherScope(String requestScope) {
        this.requestScope = requestScope;
    }

    /**
     * Retrieves the request scope associated with this {@link PublisherScope}.
     * @return request scope
     */
    public String requestScope() {
        return requestScope;
    }
}
