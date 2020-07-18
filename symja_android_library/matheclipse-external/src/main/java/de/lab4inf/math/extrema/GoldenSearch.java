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

package de.lab4inf.math.extrema;

import de.lab4inf.math.Function;
import de.lab4inf.math.L4MObject;

import static de.lab4inf.math.util.Accuracy.hasConverged;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sqrt;
import static java.lang.String.format;

/**
 * Find the minimum or maximum of a function via the golden search method.
 * Kind of a bisection method with slightly different borders.
 *
 * @author nwulff
 * @version $Id: GoldenSearch.java,v 1.21 2014/11/16 21:47:23 nwulff Exp $
 * @since 01.07.2009
 */
public final class GoldenSearch extends L4MObject {
    private static final int MAX_ITERATIONS = 50;
    private static final double A1 = (3.0 - sqrt(5.0)) / 2.0;
    private static final double A2 = 1 - A1;
    private static boolean debugFlag = false;
    private static int maxIterations = MAX_ITERATIONS;

    /**
     * Hidden constructor, as no instances of this utility class are allowed.
     */
    private GoldenSearch() {
    }

    /**
     * Get the value of the maxIterations attribute.
     *
     * @return the maxIterations
     */
    public static int getMaxIterations() {
        return maxIterations;
    }

    /**
     * Set the value of the maxIterations attribute.
     *
     * @param maxIterations the maxIterations to set
     */
    public static void setMaxIterations(final int maxIterations) {
        GoldenSearch.maxIterations = maxIterations;
    }

    /**
     * Find the maximum of function within the interval [a,b].
     *
     * @param fct to find the maximum for
     * @param a   the left border
     * @param b   the right border
     * @param eps the relative precision of x0
     * @return x0 the maximum
     */
    public static double maximum(final Function fct, final double a, final double b, final double eps) {
        return optima(fct, a, b, eps, false);
    }

    /**
     * Find the minimum of function within the interval [a,b].
     *
     * @param fct to find the minimum for
     * @param a   the left border
     * @param b   the right border
     * @param eps the relative precision of x0
     * @return x0 the minimum
     */
    public static double minimum(final Function fct, final double a, final double b, final double eps) {
        return optima(fct, a, b, eps, true);
    }

    /**
     * Internal optimization using the golden search method.
     *
     * @param fct    to find the minimum for
     * @param a      the left border
     * @param b      the right border
     * @param eps    the relative precision of x0
     * @param minima indicate if minimum or maximum is required
     * @return x0 the optimum = maximum or minimum
     */
    private static double optima(final Function fct, final double a, final double b, final double eps,
                                 final boolean minima) {
        final double ma = min(a, b), mb = max(a, b);
        double xa = ma, xb = mb, x1, x2, x = 0;
        double f1, f2;
        int ite = 0;
        // if(debugFlag && getLogger().isInfoEnabled()) {
        // getLogger().info(format("START: [%.5f - %.5f]",xa,xb));
        // }
        do {
            x1 = xa + A1 * (xb - xa);
            x2 = xa + A2 * (xb - xa);
            f1 = fct.f(x1);
            f2 = fct.f(x2);
            if (minima) {
                if (f1 < f2) {
                    xb = x2;
                } else {
                    xa = x1;
                }
            } else {
                if (f1 > f2) {
                    xb = x2;
                } else {
                    xa = x1;
                }
            }
            x = (xa + xb) / 2;
            checkEnclosure(ma, mb, xa, xb);
            // if(debugFlag && getLogger().isInfoEnabled()) {
            // getLogger().info(format("%2d: f(%.5f)=%f [%.5f - %.5f]", ite, x,
            // fct.f(x),xa,xb));
            // }
        } while (!hasConverged(xa, xb, eps, ++ite, maxIterations));
        if (debugFlag && getLogger().isInfoEnabled()) {
            getLogger().info(format("opt: f(%.5f)=%f", x, fct.f(x)));
        }
        return x;
    }

    /**
     * Check if the condition a &le; l &lt; r &le; b is fulfilled.
     * Otherwise the algorithm has left the initial interval.
     *
     * @param a the original left interval border
     * @param b the original right interval border
     * @param l the new left interval border
     * @param r the new right interval border
     * @return boolean flag indication if condition fulfilled
     */
    protected static boolean checkEnclosure(final double a, final double b, final double l, final double r) {
        final String fmt = "%f <= %f < %f <= %f violated";
        if (a <= l && l < r && r <= b) {
            return true;
        }
        final String msg = format(fmt, a, l, r, b);
        getLogger().error(msg);
        throw new ArithmeticException(msg);
    }

    /**
     * Get the value of the debug attribute.
     *
     * @return the debug
     */
    public static boolean isDebug() {
        return debugFlag;
    }

    /**
     * Set the value of the debug attribute.
     *
     * @param debug the debug to set
     */
    public static void setDebug(final boolean debug) {
        debugFlag = debug;
    }
}
 