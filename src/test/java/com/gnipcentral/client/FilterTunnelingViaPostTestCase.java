package com.gnipcentral.client;

/**
 * 
 */
public class FilterTunnelingViaPostTestCase extends FilterTestCase {

    public void setUp() throws Exception {
        super.setUp();
        config.setTunnelOverPost(true);
    }    
}
