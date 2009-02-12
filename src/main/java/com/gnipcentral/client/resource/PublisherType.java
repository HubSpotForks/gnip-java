package com.gnipcentral.client.resource;

import com.gnipcentral.client.GnipConnection;

/**
 * Enumeration of the publisher types that are supported by Gnip {@link GnipConnection} APIs.
 */
public enum PublisherType
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

    private PublisherType(String requestScope) {
        this.requestScope = requestScope;
    }

    /**
     * Retrieves the request scope associated with this {@link PublisherType}.
     * @return request scope
     */
    public String requestScope() {
        return requestScope;
    }
}
