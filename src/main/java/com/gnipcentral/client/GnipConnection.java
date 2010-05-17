package com.gnipcentral.client;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBException;

import org.xml.sax.InputSource;

import com.gnipcentral.client.resource.Activity;
import com.gnipcentral.client.resource.Results;
import com.gnipcentral.client.resource.Translator;
import com.gnipcentral.client.util.HTTPConnection;

/**
 * Represents a client connection to a Gnip service. This class encapsulates all read protocol level interactions with a
 * Gnip service and provides a higher-level abstraction for reading data from Gnip.
 */ 
public class GnipConnection {

    public static final long BUCKET_SIZE_MILLIS = 60 * 1000;

    private final HTTPConnection connection;
    private final Config config;

    public GnipConnection(Config config) {
        this.connection = new HTTPConnection(config);
        this.config = config;
    }

    /**
     * Retrieves the {@link com.gnipcentral.client.Config configuration} used to establish and authenticate a Gnip
     * connection.
     * 
     * @return the config object
     */
    public Config getConfig() {
        return config;
    }

    /**
     * Retrieves the {@link com.gnipcentral.client.util.HTTPConnection HTTP connection} used to send / receive HTTP
     * requests with Gnip.
     * 
     * @return the HTTP connection
     */
    public HTTPConnection getHTTPConnection() {
        return connection;
    }

    /**
     * Retrieves the {@link Activity} data for the current activity bucket. 
     * 
     * @return the {@link Results} model, which contains a set of {@link Activity activities}.
     * @throws GnipException
     *             if the user doesn't have access to activity data for the Publisher, if there were problems
     *             authenticating with the Gnip server, or if another error occurred.
     */
    public Results getActivities() throws GnipException {
        try {
            InputStream inputStream = connection.doGet(getActivityUrl());
            Results results = Translator.parseResults(new InputSource(inputStream));

            if (results != null && results.getRefreshUrl() != null && results.getRefreshUrl().length() != 0) {
                config.setCurrentGnipUrl(results.getRefreshUrl());
            }
            
            return results;
        } catch (IOException e) {
            throw new GnipException("Exception occurred getting activities", e);
        } catch (JAXBException e) {
            throw new GnipException("Exception occurred getting activities", e);
        }
    }

    private String getActivityUrl() {
        return config.getCurrentGnipUrl();
    }
}
