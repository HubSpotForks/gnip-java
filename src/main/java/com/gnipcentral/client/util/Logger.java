package com.gnipcentral.client.util;

/**
 *
 */
public class Logger {

    private static Logger LOG = null;

    public static Logger getInstance() {
        if(LOG == null)
            LOG = new Logger(new NoopLogger());
        return LOG;
    }

    public static Logger getInstance(Logger logger) {
        if(LOG == null)
            LOG = new Logger(logger);
        return logger;
    }

    private Logger delegate;

    public Logger() {
    }

    public Logger(Logger logger) {
        delegate = logger;
    }

    public boolean isLogEnabled() {
        return delegate.isLogEnabled();
    }

    public void log(Object object, Object ... args) {
        if(isLogEnabled())
            delegate.log(object, args);
    }

    public static class NoopLogger extends Logger {

        @Override
        public boolean isLogEnabled() {return false;}

        @Override
        public void log(Object object, Object ... args) {}
    }

    public static class ConsoleLogger extends Logger {

        @Override
        public boolean isLogEnabled() {return true;}

        @Override
        public void log(Object object, Object ... args) {
            out(object, args);
        }

        private void out(Object object, Object ... args) {
            if(object == null) return;

            System.out.printf(object.toString(), args);
        }
    }
}