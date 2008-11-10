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

        Class serviceImpl = null;
        String loggerProperty = System.getProperty("gnip.logger.class");
        if(loggerProperty != null) {
            try {
                serviceImpl = Class.forName(loggerProperty);
            }
            catch(ClassNotFoundException e) {
                System.err.printf("Unable to create logger of type %s due to exception %s\n", loggerProperty, e.getMessage());                
            }
        } else {
            List<Class> serviceImpls = ServiceLoader.loadServices(Logger.class);
            serviceImpl = (serviceImpls.size() > 0 ? serviceImpls.get(0) : null);            
        }
        
        if(serviceImpl == null) {
            LOG = new Logger.NoopLogger();
        } else {
            try {
                LOG = Logger.class.cast(serviceImpl.newInstance());
            } catch (IllegalAccessException e) {
                System.err.printf("Unable to create logger of type %s due to exception %s\n", serviceImpl.getName(), e.getMessage());
            } catch (InstantiationException e) {
                System.err.printf("Unable to create logger of type %s due to exception %s\n", serviceImpl.getName(), e.getMessage());
            }
        }

        return LOG;
    }    
}
