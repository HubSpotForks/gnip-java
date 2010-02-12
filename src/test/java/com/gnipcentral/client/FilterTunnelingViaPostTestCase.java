package com.gnipcentral.client;

/**
 * 
 */
public class FilterTunnelingViaPostTestCase extends FilterTestCase {

    @Override
    public void setUp() throws Exception {
        super.setUp();
        config.setTunnelOverPost(true);
    }    
}
