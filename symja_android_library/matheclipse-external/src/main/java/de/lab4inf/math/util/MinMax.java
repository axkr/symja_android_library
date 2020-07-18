/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2012,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for computer sciences (Lab4Inf).
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
package de.lab4inf.math.util;

import java.util.Collection;

import de.lab4inf.math.L4MObject;

import static java.lang.Math.abs;

/**
 * Utility class to calculate minimum and maximum of numbers.
 *
 * @author nwulff
 * @version $Id: MinMax.java,v 1.2 2015/01/27 18:18:41 nwulff Exp $
 * @since 22.08.2012
 */
public final class MinMax extends L4MObject {
    private static final String NO_ARGUMENTS = "No Arguments";

    /**
     * No instances of utility class are allowed.
     */
    private MinMax() {
    }

    /**
     * Calculate the minimum of a and b.
     *
     * @param a   first argument
     * @param b   second argument
     * @param <T> type of the numbers
     * @return minimum of the arguments
     */
    public static <T extends Number> T min(final T a, final T b) {
        if (a.doubleValue() < b.doubleValue()) {
            return a;
        }
        return b;
    }

    /**
     * Calculate the maximum of a and b.
     *
     * @param a   first argument
     * @param b   second argument
     * @param <T> type of the numbers
     * @return maximum of the arguments
     */
    public static <T extends Number> T max(final T a, final T b) {
        if (a.doubleValue() < b.doubleValue()) {
            return b;
        }
        return a;
    }

    /**
     * Calculate the minimum of the arguments.
     *
     * @param x   the arguments
     * @param <T> type of the numbers
     * @return minimum of the arguments
     */
    public static <T extends Number> T min(final T... x) {
        if (x == null || x.length == 0) {
            throw new IllegalArgumentException(NO_ARGUMENTS);
        }
        int n = x.length;
        T ret = x[0];
        for (int j = 1; j < n; ret = min(ret, x[j]), j++) ;
        return ret;
    }

    /**
     * Calculate the minimum of the arguments.
     *
     * @param x   the arguments
     * @param <T> type of the numbers
     * @return minimum of the arguments
     */
    public static <T extends Number> T min(final Collection<T> x) {
        if (x == null || x.size() == 0) {
            throw new IllegalArgumentException(NO_ARGUMENTS);
        }
        T ret = x.iterator().next();
        for (T y : x) {
            ret = min(ret, y);
        }
        return ret;
    }

    /**
     * Calculate the maximum of the arguments.
     *
     * @param x   the arguments
     * @param <T> type of the numbers
     * @return maximum of the arguments
     */
    public static <T extends Number> T max(final T... x) {
        if (x == null || x.length == 0) {
            throw new IllegalArgumentException(NO_ARGUMENTS);
        }
        int n = x.length;
        T ret = x[0];
        for (int j = 1; j < n; ret = max(ret, x[j]), j++) ;
        return ret;
    }

    /**
     * Calculate the maximum of the arguments.
     *
     * @param x   the arguments
     * @param <T> type of the numbers
     * @return maximum of the arguments
     */
    public static <T extends Number> T max(final Collection<T> x) {
        if (x == null || x.size() == 0) {
            throw new IllegalArgumentException(NO_ARGUMENTS);
        }
        T ret = x.iterator().next();
        for (T y : x) {
            ret = max(ret, y);
        }
        return ret;
    }

    // /**
    // * Calculate the minimum of the arguments.
    // * @param x the arguments
    // * @return minimum of the arguments
    // */
    // public static double min(final double... x) {
    // if (x==null||x.length==0) {
    // throw new IllegalArgumentException(NO_ARGUMENTS);
    // }
    // int n = x.length;
    // double ret = x[0];
    // for (int j = 1; j<n; ret = min(ret, x[j]), j++);
    // return ret;
    // }
    //
    // /**
    // * Calculate the maximum of the arguments.
    // * @param x the arguments
    // * @return maximum of the arguments
    // */
    // public static double max(final double... x) {
    // if (x==null||x.length==0) {
    // throw new IllegalArgumentException(NO_ARGUMENTS);
    // }
    // int n = x.length;
    // double ret = x[0];
    // for (int j = 1; j<n; ret = max(ret, x[j]), j++);
    // return ret;
    // }

    /**
     * Calculate the minimum of the absolute arguments.
     *
     * @param x the arguments
     * @return minimum(|x1|,|x2|, ...)
     */
    public static double absmin(final double... x) {
        if (x == null || x.length == 0) {
            throw new IllegalArgumentException(NO_ARGUMENTS);
        }
        int n = x.length;
        double ret = abs(x[0]);
        for (int j = 1; j < n; ret = min(ret, abs(x[j])), j++) ;
        return ret;
    }

    /**
     * Calculate the maximum of the absolute arguments.
     *
     * @param x the arguments
     * @return maximum(|x1|,|x2|, ...)
     */
    public static double absmax(final double... x) {
        if (x == null || x.length == 0) {
            throw new IllegalArgumentException(NO_ARGUMENTS);
        }
        int n = x.length;
        double ret = abs(x[0]);
        for (int j = 1; j < n; ret = max(ret, abs(x[j])), j++) ;
        return ret;
    }

}
 