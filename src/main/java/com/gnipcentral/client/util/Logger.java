package com.gnipcentral.client.util;

/**
 * A basic class implementing Gnip-scoped logging functionality.  By default, the Gnip-scoped
 * Logger is a no-op and doesn't print anything.  In order to have the Gnip Java client emit
 * logging messages, extend this class and register the implementation class using the JAR
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
 */
public abstract class Logger {

    private Logger delegate;

    Logger() {
    }

    Logger(Logger logger) {
        delegate = logger;
        assert delegate != null;
    }

    public boolean isLogEnabled() {
        return delegate.isLogEnabled();
    }

    public void log(Object object, Object ... args) {
        if(isLogEnabled()) {
            delegate.log(object, args);
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