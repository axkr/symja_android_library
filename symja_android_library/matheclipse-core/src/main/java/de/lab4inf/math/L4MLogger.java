/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2011,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer Sciences (Lab4Inf).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

package de.lab4inf.math;

import java.io.OutputStream;
import java.security.Permission;
import java.util.Arrays;
import java.util.IllegalFormatConversionException;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import static de.lab4inf.math.Constants.L4MLOGGER;
import static java.lang.String.format;

/**
 * Lab4Math internal logger implementation. Hiding
 * the log4j oder java.util.Logger facility.
 *
 * @author nwulff
 * @version $Id: L4MLogger.java,v 1.11 2013/06/26 09:59:41 nwulff Exp $
 * @since 04.01.2011
 */
public class L4MLogger extends Logger {
    private static final String LNAME = L4MLogger.class.getName();

    /**
     * Sole constructor.
     */
    public L4MLogger() {
        this(L4MLOGGER, null);
    }

    /**
     * Named constructor.
     *
     * @param name of the logger
     */
    protected L4MLogger(final String name) {
        this(name, null);
    }

    /**
     * Named constructor.
     *
     * @param name               of the logger
     * @param resourceBundleName to use
     */
    protected L4MLogger(final String name, final String resourceBundleName) {
        super(name, resourceBundleName);
        initialize();
    }

    /**
     * Exclusive access to a named Lab4Info Logger.
     *
     * @param name String name for this logger
     * @return Logger of type Lab4InfLogger with given name.
     */
    public static synchronized L4MLogger getLogger(final String name) {
        Logger logger = LogManager.getLogManager().getLogger(name);
        if (logger == null) {
            logger = new L4MLogger(name);
            return (L4MLogger) logger;
        } else {
            if (logger instanceof L4MLogger) {
                return (L4MLogger) logger;
            }
        }
        System.err.printf("WARNING %s is no L4MLogger%n", name);
        return null;
    }

    /**
     * Exclusive access to a named Lab4Info Logger.
     *
     * @param clazz use the class name for this logger
     * @return Logger of type Lab4InfLogger with given name.
     */
    public static L4MLogger getLogger(final Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    /**
     * Default initialization, checking for applet in sandbox.
     */
    protected void initialize() {
        String loggerName = getName();
        boolean runningSandbox = false;
        // check if we are running in an applet sandbox
        SecurityManager sm = System.getSecurityManager();
        if (null != sm) {
            try {
                Permission perm = new java.util.logging.LoggingPermission("control", "");
                sm.checkPermission(perm);
            } catch (SecurityException e) {
                System.out.println("L4M Logger runs in sandbox");
                runningSandbox = true;
            }
        }
        if (!runningSandbox) {
            // do not use parent handlers but our own
            setUseParentHandlers(false);
            // and add the Lab4inf handler to our logger
            addHandler(new Lab4InfHandler());
            // register with the LogManager
        }
        LogManager manager = LogManager.getLogManager();
        Logger aLogger = manager.getLogger(loggerName);
        if (manager.getLogger(loggerName) == null) {
            if (!LogManager.getLogManager().addLogger(this)) {
                System.err.println("failed to add " + this);
                throw new RuntimeException("addding L4MLogger failed");
            }
        } else {
            String msg;
            msg = format("allready registered %s by %s", loggerName, aLogger);
            aLogger.warning(msg);
            msg = format("could not register me: %s", this);
            aLogger.warning(msg);
        }
        setLevel(Level.WARNING);
    }

    /*
     * (non-Javadoc)
     * @see java.util.logging.Logger#log(java.util.logging.LogRecord)
     */
    @Override
    public void log(final LogRecord record) {
        String[] cl = getClassLine();
        record.setSourceClassName(cl[0]);
        record.setSourceMethodName(cl[1]);
        super.log(record);
    }

    /**
     * Calculate the class name and the logging line number.
     *
     * @return array with name and number
     */
    private String[] getClassLine() {
        StackTraceElement[] stack = (new Throwable()).getStackTrace();
        String[] classmethod = {"class", "line"};
        // Search the causing logging class.
        int ix = stack.length - 1;
        while (ix >= 0) {
            StackTraceElement frame = stack[ix];
            String cname = frame.getClassName();
            if (cname.equals(LNAME)) {
                ix++;
                frame = stack[ix];
                cname = frame.getClassName();
                // We've found the relevant frame.
                classmethod[0] = cname;
                classmethod[1] = String.format("%d", frame.getLineNumber());
                break;
            }
            ix--;
        }
        return classmethod;
    }

    /**
     * Make an error log.
     *
     * @param msg   message to log
     * @param error causing the log
     */
    public void error(final String msg, final Throwable error) {
        super.log(Level.SEVERE, msg, error);
    }

    /**
     * Make an error log.
     *
     * @param error causing the log
     */
    public void error(final Throwable error) {
        error(error.toString(), error);
    }

    /**
     * Make an error log.
     *
     * @param msg message to log
     */
    public void error(final String msg) {
        super.severe(msg);
    }

    /**
     * Make a warning log.
     *
     * @param error causing the warning
     */
    public void warn(final Throwable error) {
        warn(error.toString());
    }

    /**
     * Make a warning log.
     *
     * @param msg message to log
     */
    public void warn(final String msg) {
        super.log(Level.WARNING, msg);

    }

    /**
     * Make a warning log.
     *
     * @param msg message to log
     */
    @Override
    public void warning(final String msg) {
        super.log(Level.WARNING, msg);
    }

    /**
     * Make a info log for this object.
     *
     * @param obj message to log
     */
    public void info(final Object obj) {
        if (isInfoEnabled()) {
            if (null == obj) {
                super.log(Level.INFO, "null");
            } else {
                super.log(Level.INFO, obj.toString());
            }
        }
    }
     /*
      * (non-Javadoc)
      * @see java.util.logging.Logger#setLevel(java.util.logging.Level)
      */
    //    @Override
    //    public void setLevel(final Level level) {
    //        Exception e = new Exception();
    //        StackTraceElement[] sts = e.getStackTrace();
    //        System.out.println("setLevel: "+level+" " + sts[1]);
    //        super.setLevel(level);
    //    }

    /**
     * Make a info log.
     *
     * @param msg message to log
     */
    @Override
    public void info(final String msg) {
        // needed for a correct line counting
        if (isInfoEnabled()) {
            super.log(Level.INFO, msg);
        }
    }

    /**
     * Make an info error log.
     *
     * @param error causing the info
     */
    public void info(final Throwable error) {
        info(error.toString(), error);
    }

    /**
     * Make an info error log.
     *
     * @param msg   message to log
     * @param error causing the log
     */
    public void info(final String msg, final Throwable error) {
        if (isInfoEnabled()) {
            super.log(Level.INFO, msg, error);
        }
    }

    /**
     * Make a formated info log
     *
     * @param fmt  format specification
     * @param args objects to log
     */
    public void info(final String fmt, final Object... args) {
        String msg = null;
        if (null != fmt) {
            try {
                msg = String.format(fmt, args);
                super.log(Level.INFO, msg);
            } catch (IllegalFormatConversionException e) {
                msg = "wrong formated logging: " + e.getMessage();
                super.log(Level.WARNING, msg);
            }
        } else {
            error("no format string specified for: " + Arrays.toString(args));
        }
    }

    /**
     * Signal if info logging is enabled.
     *
     * @return true if info logging is enabled
     */
    public boolean isInfoEnabled() {
        return super.isLoggable(Level.INFO);
    }

    /**
     * Signal if warning logging is enabled.
     *
     * @return true if warning logging is enabled
     */
    public boolean isWarnEnabled() {
        return super.isLoggable(Level.WARNING);
    }

    /**
     * Custom logging handler for the Lab4Inf packages.
     */
    static class Lab4InfHandler extends StreamHandler {

        /**
         * Default constructor using the Lab4InfFormatter.
         */
        public Lab4InfHandler() {
            this(System.out, new Lab4InfFormatter());
        }

        /**
         * @param out OutputStream to use
         * @param fmt Formatter to use
         */
        public Lab4InfHandler(final OutputStream out, final Formatter fmt) {
            super(out, fmt);
        }

        /*
         * (non-Javadoc)
         * @see java.util.logging.StreamHandler#publish(java.util.logging.LogRecord)
         */
        @Override
        public synchronized void publish(final LogRecord record) {
            super.publish(record);
            flush();
        }
    }

    static class Lab4InfFormatter extends Formatter {

        /**
         * Standard constructor.
         */
        public Lab4InfFormatter() {
            super();
        }

        /**
         * Helper method to return the class name of a full specified package.class
         * name string.
         *
         * @param name String the full class name
         * @return String the single class name
         */
        protected String className(final String name) {
            int start = name.lastIndexOf('.');
            int end = name.length();
            if (start > 0) {
                start++;
                return name.substring(start, end);
            } else
                return name;
        }

        /**
         * Provide formatted logging strings.
         *
         * @param record LogRecord to use.
         * @return String the formatted output.
         */
        @Override
        public String format(final LogRecord record) {
            String msg;
            String threadInfo = Thread.currentThread().getName();
            Throwable thrown = record.getThrown();
            String clazz = className(record.getSourceClassName());
            String where = String.format("[%s] (%s.java:%s)", threadInfo, clazz,
                    record.getSourceMethodName());
            where = String.format("%s", where);
            if (thrown == null) {
                msg = String.format("%-7s %s - %s %n", record.getLevel(),
                        where, record.getMessage());
            } else {
                StackTraceElement stack = thrown.getStackTrace()[0];
                String errorClazz = stack.getClassName();
                int errorLine = stack.getLineNumber();
                msg = String.format("%-7s %s %s %s:%d %s%n",
                        record.getLevel(), where, record.getMessage(),
                        errorClazz, errorLine, thrown.getMessage());
            }
            return msg;
        }
    }
}
 