package com.gnipcentral.client.util;

import java.util.List;

/**
 * 
 */
public class LoggerFactory {

    private static Logger LOG = null;

    public static Logger getInstance() {
        if(LOG != null)
            return LOG;

        List<Class> serviceImpls = ServiceLoader.loadServices(Logger.class);
        if(serviceImpls.size() == 0) {
            LOG = new Logger.NoopLogger();
        }
        else {
            Class c = null;
            try {
                c = serviceImpls.get(0);
                LOG = Logger.class.cast(c.newInstance());
            } catch (IllegalAccessException e) {
                System.err.printf("Unable to create logger of type " + c.getName(), e);
            } catch (InstantiationException e) {
                System.err.printf("Unable to create logger of type " + c.getName(), e);                                
            }
        }

        return LOG;
    }    
}
