package com.gnipcentral.client.util;

/**
 *
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