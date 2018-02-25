/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
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

import java.lang.reflect.Array;
import java.util.Locale;

import static de.lab4inf.math.Constants.DEF_LOCALE;
import static de.lab4inf.math.Constants.L4MLOGGER;
import static de.lab4inf.math.Constants.VERSION_LABEL;
import static de.lab4inf.math.Constants.VERSION_MAJOR;
import static de.lab4inf.math.Constants.VERSION_MINOR;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import static java.lang.String.format;

/**
 * Common base for all Lab4Math classes.
 *
 * @author nwulff
 * @version $Id: L4MObject.java,v 1.33 2015/01/29 14:50:15 nwulff Exp $
 * @since 07.12.2009
 */
public abstract class L4MObject {
    protected static final String NOT_COMPLEX_S = "not complex %s";
    protected static final String NOT_IMPLEMENTED_YET = "not implemented yet...";
    protected static final boolean DEBUG = Constants.DEBUG;
    /**
     * Common Lab4Math logger instance to use.
     */
    protected static final L4MLogger LOGGER = L4MLogger.getLogger(L4MLOGGER);

    static {
        try {
            Locale.setDefault(DEF_LOCALE);
        } catch (Throwable error) {
            LOGGER.warning("couldn't set Locale " + error);
        }
    }

    protected final L4MLogger logger;

    /**
     * Bean constructor for derived classes.
     */
    protected L4MObject() {
        logger = LOGGER;
    }

    /**
     * Public access to our logger.
     *
     * @return Logger in use
     */
    public static L4MLogger getLogger() {
        return LOGGER;
    }

    /**
     * Get the current Lab4Math version as String.
     *
     * @return version
     */
    public static String getVersion() {
        return format("%d.%d.%d", VERSION_MAJOR, VERSION_MINOR, VERSION_LABEL);
    }

    /**
     * Test if the given generic is a complex type.
     *
     * @param x   instance to check
     * @param <T> type of the generic
     * @return complex indicator
     */
    protected static <T extends Numeric<T>> boolean isAComplex(final T x) {
        return Complex.class.isInstance(x);
    }

    /**
     * Test if the given argument is a complex array.
     *
     * @param x   array to check
     * @param <T> type of the generic
     * @return complex indicator
     */
    protected static <T extends Numeric<T>> boolean isAComplex(final T[] x) {
        return isAComplex(x[0]);
    }

    /**
     * Test if the given argument is a complex matrix.
     *
     * @param x   instance to check
     * @param <T> type of the generic
     * @return complex indicator
     */
    protected static <T extends Numeric<T>> boolean isAComplex(final T[][] x) {
        return isAComplex(x[0]);
    }

    /**
     * Helper method to cast the generic to a complex.
     *
     * @param x   generic to cast
     * @param <T> type of the generic
     * @return x as complex
     */
    protected static <T extends Numeric<T>> Complex asComplex(final T x) {
        if (!isAComplex(x))
            throw new IllegalAccessError(format(NOT_COMPLEX_S, x));
        return Complex.class.cast(x);
    }

    /**
     * Helper method to cast the generic array to a complex one.
     *
     * @param x   generic array to cast
     * @param <T> type of the generic
     * @return x as complex array
     */
    protected static <T extends Numeric<T>> Complex[] asComplex(final T[] x) {
        if (!isAComplex(x))
            throw new IllegalAccessError(format(NOT_COMPLEX_S, x.getClass().toString()));
        return Complex[].class.cast(x);
    }

    /**
     * Helper method to cast the generic matrix to a complex matrix.
     *
     * @param x   generic matrix to cast
     * @param <T> type of the generic
     * @return x as complex matrix
     */
    protected static <T extends Numeric<T>> Complex[][] asComplex(final T[][] x) {
        if (!isAComplex(x))
            throw new IllegalAccessError(format(NOT_COMPLEX_S, x.getClass().toString()));
        return Complex[][].class.cast(x);
    }

    /**
     * Create a vector of type T.
     *
     * @param x   T the type
     * @param n   the dimension
     * @param <T> the type of the field elements
     * @return T[] the new array
     */
    public static <T extends Field<T> & Factory<T>> T[] create(final T x, final int n) {
        @SuppressWarnings("unchecked")
        T[] y = (T[]) Array.newInstance(x.getClass(), n);
        return y;
    }

    /**
     * Create a vector of type T.
     *
     * @param x   T[] the type indicator
     * @param n   the dimension
     * @param <T> the type of the field elements
     * @return T[] the new array
     */
    public static <T extends Field<T> & Factory<T>> T[] create(final T[] x, final int n) {
        return create(x[0], n);
    }

    /**
     * Create a vector of type T.
     *
     * @param x   T[][] the type indicator
     * @param n   the dimension
     * @param <T> the type of the field elements
     * @return T[] the new array
     */
    public static <T extends Field<T> & Factory<T>> T[] create(final T[][] x, final int n) {
        return create(x[0], n);
    }

    /**
     * Create a matrix of type T.
     *
     * @param x   T the type
     * @param n   the first dimension
     * @param m   the second dimension
     * @param <T> the type of the field elements
     * @return T[][] the new matrix
     */
    public static <T extends Field<T> & Factory<T>> T[][] create(final T x, final int n, final int m) {
        @SuppressWarnings("unchecked")
        T[][] y = (T[][]) Array.newInstance(x.getClass(), n, m);
        return y;
    }

    /**
     * Create a matrix of type T.
     *
     * @param x   T[] the type indicator
     * @param n   the first dimension
     * @param m   the second dimension
     * @param <T> the type of the field elements
     * @return T[][] the new matrix
     */
    public static <T extends Field<T> & Factory<T>> T[][] create(final T[] x, final int n, final int m) {
        return create(x[0], n, m);
    }

    /**
     * Create a matrix of type T.
     *
     * @param x   T[][] the type indicator
     * @param n   the first dimension
     * @param m   the second dimension
     * @param <T> the type of the field elements
     * @return T[][] the new matrix
     */
    public static <T extends Field<T> & Factory<T>> T[][] create(final T[][] x, final int n, final int m) {
        return create(x[0], n, m);
    }

    /**
     * Helper method to transform the object into a generic type.
     *
     * @param o   prototype of T
     * @param x   object to cast
     * @param <T> type of the generic
     * @return x as a T instance
     */
    @SuppressWarnings("unchecked")
    protected static <T extends Numeric<T>> T asT(final T o, final Object x) {
        return (T) o.getClass().cast(x);
    }

    /**
     * Helper method to transform the array into a generic type.
     *
     * @param o   prototype of T[]
     * @param x   array to transform
     * @param <T> type of the generic
     * @return x as a T[] array
     */
    protected static <T extends Numeric<T>> T[] asT(final T[] o, final Object[] x) {
        T[] ret = create(o, x.length);
        for (int j = 0; j < x.length; j++)
            ret[j] = asT(o[0], x[j]);
        return ret;
    }

    /**
     * Helper method to transform the matrix into a generic type.
     *
     * @param o   prototype of T[][]
     * @param x   matrix to transform
     * @param <T> type of the generic
     * @return x as a T[][] matrix
     */
    protected static <T extends Numeric<T>> T[][] asT(final T[][] o, final Object[][] x) {
        T[][] ret = create(o, x.length, x[0].length);
        for (int j = 0; j < x.length; j++)
            ret[j] = asT(o[j], x[j]);
        return ret;
    }

    /**
     * Helper method to calculate sqrt(x*x + y*y) without over- or underflow.
     *
     * @param x 1.st argument
     * @param y 2.nd argument
     * @return sqrt(x*x + y*y)
     */
    public static double hypot(final double x, final double y) {
        double tmp, ax = abs(x), ay = abs(y);
        if (ax > ay) {
            tmp = ax;
            ax = ay;
            ay = tmp;
        }
        if (ay > 0)
            ax /= ay;
        return ay * sqrt(1 + ax * ax);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return format("%s-%s", getClass().getSimpleName(), getVersion());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (null == obj) {
            return false;
        } else if (this == obj) {
            return true;
        } else if (getClass() == obj.getClass()) {
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Utility method to load an implementation of the interface type T.
     * Internally this method uses the ServiceLoader/L4MLoader mechanism.
     *
     * @param interfaceType the interface of T
     * @param <T>           interface type to resolve
     * @return T implementation
     * @see de.lab4inf.math.L4MLoader
     */
    protected <T> T resolve(final Class<T> interfaceType) {
        return L4MLoader.load(interfaceType);
    }
}
 