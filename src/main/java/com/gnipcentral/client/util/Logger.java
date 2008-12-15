package com.gnipcentral.client.util;

/**
 * A logging abstraction that should be adapted to a specific logging implementation based on the needs of the client.
 * The library defaults to using an empty {@link Logger} in order to avoid requiring a specific logging implementation.
 * All {@link Logger} instances are created via {@link com.gnipcentral.client.util.LoggerFactory#getInstance()}
 * and are configured in the environment as documented in {@link com.gnipcentral.client.util.LoggerFactory}.
 * <br/>
 * <br/>
 * Messages that are logged via the logger can be in <i>printf</i> format and appear as:
 * <pre>
 *   logger.log("My favorite cookie is %s.", "chocolate chip"); 
 * </pre>
 */
public abstract class Logger {

    private Logger delegate;

    /**
     * Default constructor.
     */
    Logger() {
    }

    /**
     * Package protected constructor that is
     * @param logger the logger delegate
     */
    Logger(Logger logger) {
        if(logger == null) {
            throw new IllegalArgumentException("A Logger cannot be configured with a null delegate");
        }

        delegate = logger;
    }

    /**
     * Check to see if logging is enabled.
     * 
     * @return <code>true</code> if logging is enabled; <code>false</code> otherwise.
     */
    public boolean isLogEnabled() {
        return delegate.isLogEnabled();
    }

    /**
     * Log a message with the provided message and parameters.  Messages are expected to be formatted
     * using Java's formatted printing features provided by {@link java.util.Formatter}.
     *  
     * @param message the message object
     * @param args arguments that are used to format the message argument.
     */
    public void log(Object message, Object ... args) {
        if(isLogEnabled()) {
            delegate.log(message, args);
        }
    }

    static final class NoopLogger extends Logger {

        @Override
        public final boolean isLogEnabled() {
            return false;
        }

        @Override
        public final void log(Object object, Object ... args) {
        }
    }

    static final class ConsoleLogger extends Logger {

        @Override
        public final boolean isLogEnabled() {
            return true;
        }

        @Override
        public final void log(Object object, Object ... args) {
            out(object, args);
        }

        private void out(Object object, Object ... args) {
            if(object == null) {
                return;
            }

            System.out.printf(object.toString(), args);
        }
    }
}