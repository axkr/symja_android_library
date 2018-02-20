package org.apache.log4j;

/**
 * Created by Duy on 2/21/2018.
 */

public class Logger {
    static final boolean DEBUG = false;

    public static Logger getLogger(Class<?> aclass) {
        return new Logger();
    }

    public void warn(String message) {

    }

    public boolean isDebugEnabled() {
        return DEBUG;
    }

    public void info(String message) {

    }

    public void debug(String message) {

    }

    public boolean isInfoEnabled() {
        return DEBUG;
    }

    public void error(String message) {

    }
}
