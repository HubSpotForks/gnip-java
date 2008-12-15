package com.gnipcentral.client.util;

import java.util.List;

/**
 * A factory used to configure a Gnip {@link Logger}.  By default, the Gnip Java client library is
 * a simple, no-op that doesn't emit log messages.  In order to emit logging messages, extend
 * this class and register the Logger implementation in one of two ways:
 * <ol>
 * <li>
 * Provide a custom extension of {@link Logger} that adapts Gnip's {@link Logger} to a specific
 * logging framework.  Then, register the custom extension's implementation class using the JAR
 * META-INF/services mechanism which requires that a file named:
 * <pre>
 * com.gnipcentral.client.util.Logger
 * </pre>
 * be available in classpath in META-INF/services containing a single, uncommented line
 * that lists the classname of the Logger implementation.  For example, the contents of this
 * file might appear as:
 * <pre>
 * org.example.MyCoolLogger
 * </pre>
 * </li>
 * <li>
 * Register a system property called {@value #LOGGER_CLASS_SYSTEM_PROPERTY} that references a
 * custom extension of {@link Logger} that adapts Gnip's {@link Logger} abstraction to a
 * specific logging framework.
 * </li>
 * </ol>
 * <br/>
 * If both the system property and <code>META-INF/services</code> mechanisms are used to configure
 * valid loggers, the class specified by the system property takes precedence. 
 */
public class LoggerFactory {

    public static final String LOGGER_CLASS_SYSTEM_PROPERTY = "gnip.logger.class";

    private static Logger LOG = null;

    /**
     * Create an instance of a {@link Logger}.
     * @return By default, this returns a no-op {@link Logger}.  If a valid, custom implementation class is
     *         specified in the environment it will be returned.
     */
    public static Logger getInstance() {
        if(LOG != null)
            return LOG;

        Class serviceImpl = null;
        String loggerProperty = System.getProperty(LOGGER_CLASS_SYSTEM_PROPERTY);
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