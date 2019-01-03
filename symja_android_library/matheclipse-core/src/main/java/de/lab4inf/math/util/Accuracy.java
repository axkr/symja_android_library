/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer sciences (Lab4Inf).
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

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collection;

import de.lab4inf.math.L4MObject;
import de.lab4inf.math.Numeric;

import static java.lang.Math.abs;
import static java.lang.Math.log10;
import static java.lang.Math.pow;
import static java.lang.String.format;

/**
 * The computational accuracy for floating point
 * or double operations.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: Accuracy.java,v 1.41 2014/11/18 22:12:06 nwulff Exp $
 * @since 17.12.2008
 */
public final class Accuracy extends L4MObject {
    /**
     * float valued machine precision.
     */
    public static final BigDecimal QEPS;
    /**
     * double valued machine precision.
     */
    public static final double DEPS;
    /**
     * float valued machine precision.
     */
    public static final float FEPS;
    /**
     * message if maximal number of iterations reached.
     */
    private static final String NO_CONVERGENCE = "No convergence after %d iterations! Limiting value: %f";
    /**
     * the start value for the epsilon search.
     */
    private static final float FEPS_START = 2.E-6f;
    /**
     * powers of 10 used for rounding.
     */
    private static final double[] POWERS = {1, 10, 100, 1E3, 1E4, 1E5, 1E6, 1E7, 1E8, 1E9, 1E10, 1E11, 1E12, 1E13,
            1E14, 1E15, 1E16, 1E17, 1E18, 1E19, 1E20};
    /**
     * powers of 10-K used for digits.
     */
    private static final double[] IPOWERS = {1, 0.1, 0.01, 1E-3, 1E-4, 1E-5, 1E-6, 1E-7, 1E-8, 1E-9, 1E-10, 1E-11,
            1E-12, 1E-13, 1E-14, 1E-15, 1E-16, 1E-17, 1E-18, 1E-19, 1E-20};

    static {
        /**
         * Calculate the machine accuracy,
         * which is the smallest eps with
         * 1<1+eps
         */
        float feps = FEPS_START;
        float fy = 1.0f + feps;
        while (fy > 1.0f) {
            feps /= 2.0f;
            fy = 1.0f + feps;
        }
        FEPS = feps * 8;
        double deps =  ((double)feps) *  ((double)FEPS_START);
        double dy = 1.0 + deps;
        while (dy > 1.0) {
            deps /= 2.0;
            dy = 1.0 + deps;
        }
        DEPS = deps * 8.0;

        // MathContext mc = new MathContext(35,RoundingMode.HALF_EVEN);
        final MathContext mc = MathContext.DECIMAL128;
        final BigDecimal two = new BigDecimal(2);
        BigDecimal qeps = new BigDecimal(DEPS);
        BigDecimal qy = BigDecimal.ONE.add(qeps, mc);
        while (qy.compareTo(BigDecimal.ONE) > 0) {
            qeps = qeps.divide(two, mc);
            qy = BigDecimal.ONE.add(qeps, mc);
        }
        QEPS = qeps.multiply(BigDecimal.TEN, mc);
        // if (DEBUG)
        getLogger().info(format("feps:%8.2E  deps:%8.3G", FEPS, DEPS));
    }

    /**
     * Hidden constructor.
     */
    private Accuracy() {
    }

    /**
     * Convert the given numeric collection into a double array.
     *
     * @param c collection with numeric data
     * @return double array with data
     */
    private static double[] asArray(final Collection<? extends Number> c) {
        int i = 0;
        final int n = c.size();
        final double[] x = new double[n];
        for (final Number y : c) {
            x[i++] = y.doubleValue();
        }
        return x;
    }

    /**
     * Calculate the relative difference between x and y.
     * In case |x+y|/2 is zero the absolute difference is returned.
     *
     * @param x first value
     * @param y second value
     * @return relative error
     */
    public static double relativeDifference(final double x, final double y) {
        double error;
        if (Double.isInfinite(x) && Double.isInfinite(y)) {
            if (Double.compare(x, y) == 0) {
                error = 0;
            } else {
                error = Double.POSITIVE_INFINITY;
            }
        } else {
            final double z = abs(x + y) / 2;
            error = abs(x - y);
            if (z > 0) {
                error /= z;
            }
        }
        return error;
    }

    /**
     * Calculate the relative difference between x and y.
     * In case |x+y|/2 is zero the absolute difference is returned.
     *
     * @param x   first value
     * @param y   second value
     * @param <T> the type of the elements
     * @return relative error
     */
    public static <T extends Numeric<T>> T relativeDifference(final T x, final T y) {
        double delta = x.difference(y);
        if (delta < DEPS)
            return x.getZero();
        final double scale = x.plus(y).difference(x.getZero());
        if (scale > 1)
            delta /= scale;
        return x.getOne().multiply(delta);
    }

    /**
     * Indicate if x and y are exactly equal.
     *
     * @param x first value
     * @param y second value
     * @return true if x==y for every IEEE bit
     */
    public static boolean isEqual(final double x, final double y) {
        return Double.compare(x, y) == 0;
    }

    /**
     * Indicate if x and y are nearly equal, e.g. their relative difference
     * is less than DEPS.
     *
     * @param x first value
     * @param y second value
     * @return true if |x-y|/|x+y| &lt; eps
     */
    public static boolean isSimilar(final double x, final double y) {
        return relativeDifference(x, y) < 2 * DEPS;
    }

    /**
     * Indicate if x and y are nearly equal, e.g. their relative difference
     * is less than DEPS.
     *
     * @param x   first value
     * @param y   second value
     * @param <T> the type of the elements
     * @return true if |x-y|/|x+y| &lt; eps
     */
    public static <T extends Numeric<T>> boolean isSimilar(final T x, final T y) {
        return relativeDifference(x, y).doubleValue() < DEPS;
    }

    /**
     * Indicate if the given x double value is an integer value.
     *
     * @param x value to check
     * @return true if x is an integer
     */
    public static boolean isInteger(final double x) {
        return abs(x - Math.rint(x)) < Double.MIN_VALUE;
    }

    /**
     * Indicate if xn and xo have the relative/absolute accuracy epsilon.
     * In case that the true value is less than one this is based
     * on the absolute difference, otherwise on the relative difference:
     * <pre>
     *     2*|x[n]-x[n-1]|/|x[n]+x[n-1]| &lt; eps
     * </pre>
     *
     * @param xn  the actual argument x[n]
     * @param xo  the older argument x[n-1]
     * @param eps accuracy to reach
     * @return flag indicating if accuracy is reached.
     */
    public static boolean hasReachedAccuracy(final double xn, final double xo, final double eps) {
        final double z = abs(xn + xo) / 2;
        double error = abs(xn - xo);
        if (z > 1) {
            error /= z;
        }
        return error <= eps;
    }

    /**
     * Indicate if xn and xo have the relative/absolute accuracy epsilon.
     * In case that the true value is less than one this is based
     * on the absolute difference, otherwise on the relative difference:
     * <pre>
     *     2*|x[n]-x[n-1]|/|x[n]+x[n-1]| &lt; eps
     * </pre>
     *
     * @param xn  the actual argument x[n]
     * @param xo  the older argument x[n-1]
     * @param eps accuracy to reach
     * @param <T> the type of the elements
     * @return flag indicating if accuracy is reached.
     */
    public static <T extends Numeric<T>> boolean hasReachedAccuracy(final T xn, final T xo, final double eps) {
        final T one = xn.getOne();
        final T two = one.plus(one);
        final T z = xn.plus(xo).div(two).abs();
        T error = xn.minus(xo).abs();
        if (z.gt(one)) {
            error = error.div(z);
        }
        return error.doubleValue() <= eps;
    }

    /**
     * Indicate if the arrays xn and xo have the relative/absolute accuracy epsilon.
     * In case that the true value is less than one this is based
     * on the absolute difference, otherwise on the relative difference:
     * <pre>
     *     2*|x[n]-x[n-1]|/|x[n]+x[n-1]| &lt; eps
     * </pre>
     *
     * @param xn  the actual vector/array x[n]
     * @param xo  the older vector/array x[n-1]
     * @param eps accuracy to reach
     * @param <T> the type of the elements
     * @return flag indicating if accuracy is reached.
     */
    public static <T extends Numeric<T>> boolean hasReachedAccuracy(final T[] xn, final T[] xo, final double eps) {
        boolean ret = true;
        for (int i = 0; ret && i < xn.length; i++) {
            ret = hasReachedAccuracy(xn[i], xo[i], eps);
        }
        return ret;
    }

    /**
     * Indicate if the matrixes xn and xo have the relative/absolute accuracy epsilon.
     * In case that the true value is less than one this is based
     * on the absolute difference, otherwise on the relative difference:
     * <pre>
     *     2*|x[n]-x[n-1]|/|x[n]+x[n-1]| &lt; eps
     * </pre>
     *
     * @param xn  the actual vector/array x[n]
     * @param xo  the older vector/array x[n-1]
     * @param eps accuracy to reach
     * @param <T> the type of the elements
     * @return flag indicating if accuracy is reached.
     */
    public static <T extends Numeric<T>> boolean hasReachedAccuracy(final T[][] xn, final T[][] xo, final double eps) {
        final int n = xn.length;
        boolean ret = true;
        for (int i = 0; ret && i < n; i++) {
            ret = hasReachedAccuracy(xn[i], xo[i], eps);
        }
        return ret;
    }

    /**
     * Indicate if the arrays xn and xo have the relative/absolute accuracy epsilon.
     * In case that the true value is less than one this is based
     * on the absolute difference, otherwise on the relative difference:
     * <pre>
     *     2*|x[n]-x[n-1]|/|x[n]+x[n-1]| &lt; eps
     * </pre>
     *
     * @param xn  the actual vector/array x[n]
     * @param xo  the older vector/array x[n-1]
     * @param eps accuracy to reach
     * @return flag indicating if accuracy is reached.
     */
    public static boolean hasReachedAccuracy(final double[] xn, final double[] xo, final double eps) {
        for (int i = 0; i < xn.length; i++) {
            final double z = abs(xn[i] + xo[i]) / 2;
            double error = abs(xn[i] - xo[i]);
            if (z > 1) {
                error /= z;
            }
            if (error > eps)
                return false;
        }
        return true;
    }

    /**
     * Indicate if the matrixes xn and xo have the relative/absolute accuracy epsilon.
     * In case that the true value is less than one this is based
     * on the absolute difference, otherwise on the relative difference:
     * <pre>
     *     2*|x[n]-x[n-1]|/|x[n]+x[n-1]| &lt; eps
     * </pre>
     *
     * @param xn  the actual vector/array x[n]
     * @param xo  the older vector/array x[n-1]
     * @param eps accuracy to reach
     * @return flag indicating if accuracy is reached.
     */
    public static boolean hasReachedAccuracy(final double[][] xn, final double[][] xo, final double eps) {
        final int n = xn.length, m = xn[0].length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                final double z = abs(xn[i][j] + xo[i][j]) / 2;
                double error = abs(xn[i][j] - xo[i][j]);
                if (z > 1) {
                    error /= z;
                }
                if (error > eps)
                    return false;
            }
        }
        return true;
    }

    /**
     * Indicate if the collections xn and xo have the relative/absolute accuracy
     * epsilon. In case that the true value is less than one this is based
     * on the absolute difference, otherwise on the relative difference:
     * <pre>
     *     2*|x[n]-x[n-1]|/|x[n]+x[n-1]| &lt; eps
     * </pre>
     *
     * @param xn  the actual x[n] data
     * @param xo  the older  x[n-1] data
     * @param eps accuracy to reach
     * @return flag indicating if accuracy is reached.
     */
    public static boolean hasReachedAccuracy(final Collection<? extends Number> xn,
                                             final Collection<? extends Number> xo, final double eps) {
        final double[] x = asArray(xn);
        final double[] y = asArray(xo);
        if (x.length != y.length) {
            return false;
        }
        return hasReachedAccuracy(x, y, eps);
    }

    /**
     * Indicate if an iterative algorithm has RELATIVE converged.
     * <hr/>
     * <b>Note</b>:
     * HasConverged throws an ArithmeticException if more than max calls
     * have been made. Choose hasReacherAccuracy if this is not desired.
     * <hr/>
     *
     * @param xn  the actual argument x[n]
     * @param xo  the older argument x[n-1]
     * @param eps the accuracy to reach
     * @param n   the actual iteration counter
     * @param max the maximal number of iterations
     * @return flag indicating if accuracy is reached.
     */
    public static boolean hasConverged(final double xn, final double xo, final double eps, final int n, final int max) {
        if (hasReachedAccuracy(xn, xo, eps)) {
            return true;
        }
        if (n >= max) {
            final String msg = format(NO_CONVERGENCE, n, xn);
            if (DEBUG)
                getLogger().warn(msg);
            throw new ArithmeticException(msg);
        }
        return false;
    }

    /**
     * Indicate if an iterative algorithm has RELATIVE converged.
     * <b>Note</b>:
     * HasConverged throws an ArithmeticException if more than max calls
     * have been made. Choose hasReacherAccuracy if this is not desired.
     * <hr/>
     *
     * @param xn  the actual vector/array x[n]
     * @param xo  the older vector/array x[n-1]
     * @param eps the accuracy to reach
     * @param n   the actual iteration counter
     * @param max the maximal number of iterations
     * @return flag indicating if accuracy is reached.
     */
    public static boolean hasConverged(final double[] xn, final double[] xo, final double eps, final int n,
                                       final int max) {
        if (hasReachedAccuracy(xn, xo, eps)) {
            return true;
        }
        if (n >= max) {
            final String msg = format(NO_CONVERGENCE, n, norm(xn));
            if (DEBUG)
                getLogger().warn(msg);
            throw new ArithmeticException(msg);
        }
        return false;
    }

    /**
     * Indicate if an iterative algorithm has RELATIVE converged.
     * <b>Note</b>:
     * HasConverged throws an ArithmeticException if more than max calls
     * have been made. Choose hasReacherAccuracy if this is not desired.
     * <hr/>
     *
     * @param xn  the actual x[n] data
     * @param xo  the older  x[n-1] data
     * @param eps the accuracy to reach
     * @param n   the actual iteration counter
     * @param max the maximal number of iterations
     * @return flag indicating if accuracy is reached.
     */
    public static boolean hasConverged(final Collection<? extends Number> xn, final Collection<? extends Number> xo,
                                       final double eps, final int n, final int max) {
        if (hasReachedAccuracy(xn, xo, eps)) {
            return true;
        }
        if (n >= max) {
            final double[] xx = asArray(xn);
            final String msg = format(NO_CONVERGENCE, n, norm(xx));
            if (DEBUG)
                getLogger().warn(msg);
            throw new ArithmeticException(msg);
        }
        return false;
    }

    /**
     * Indicate if an iterative algorithm has ABSOLUTE converged. That is
     * <pre>
     *           |xo - xn| &lt; eps  and n &lt; max
     * </pre>
     * <hr/>
     * <b>Note</b>:
     * HasAbsoluteConverged throws an ArithmeticException if more than max calls
     * have been made. Choose hasReacherAccuracy if this is not desired.
     * <hr/>
     *
     * @param xn  the actual argument x[n]
     * @param xo  the older argument x[n-1]
     * @param eps the accuracy to reach
     * @param n   the actual iteration counter
     * @param max the maximal number of iterations
     * @return flag indicating if accuracy is reached.
     */
    public static boolean hasAbsoluteConverged(final double xn, final double xo, final double eps, final int n,
                                               final int max) {
        if (abs(xn - xo) < eps) {
            return true;
        }
        if (n >= max) {
            final String msg = format(NO_CONVERGENCE, n, xn);
            if (DEBUG)
                getLogger().warn(msg);
            throw new ArithmeticException(msg);
        }
        return false;
    }

    /**
     * Indicate if an iterative algorithm has ABSOLUTE converged. That is
     * <pre>
     *           ||xo - xn|| &lt; eps  and n &lt; max
     * </pre>
     * <hr/>
     * <b>Note</b>:
     * HasAbsoluteConverged throws an ArithmeticException if more than max calls
     * have been made. Choose hasReacherAccuracy if this is not desired.
     * <hr/>
     *
     * @param xn  the actual argument x[n]
     * @param xo  the older argument x[n-1]
     * @param eps the accuracy to reach
     * @param n   the actual iteration counter
     * @param max the maximal number of iterations
     * @return flag indicating if accuracy is reached.
     */
    public static boolean hasAbsoluteConverged(final double[] xn, final double[] xo, final double eps, final int n,
                                               final int max) {
        for (int i = 0; i < xn.length; i++) {
            if (abs(xn[i] - xo[i]) >= eps) {
                return false;
            }
        }
        if (n >= max) {
            final String msg = format(NO_CONVERGENCE, n, norm(xn));
            if (DEBUG)
                getLogger().warn(msg);
            throw new ArithmeticException(msg);
        }
        return true;
    }

    /**
     * Indicate if an iterative algorithm has ABSOLUTE converged. That is
     * <pre>
     *           |xo - xn| &lt; eps  and n &lt; max
     * </pre>
     * <hr/>
     * <b>Note</b>:
     * HasAbsoluteConverged throws an ArithmeticException if more than max calls
     * have been made. Choose hasReacherAccuracy if this is not desired.
     * <hr/>
     *
     * @param xn  the actual arguments x[n]
     * @param xo  the older arguments x[n-1]
     * @param eps the accuracy to reach
     * @param n   the actual iteration counter
     * @param max the maximal number of iterations
     * @return flag indicating if accuracy is reached.
     */
    public static boolean hasAbsoluteConverged(final Collection<? extends Number> xn,
                                               final Collection<? extends Number> xo, final double eps, final int n, final int max) {
        if (xn.size() != xo.size()) {
            return false;
        }
        final double[] x = asArray(xn);
        final double[] y = asArray(xo);
        return hasAbsoluteConverged(x, y, eps, n, max);
    }

    /**
     * Calculate the maximum norm of the vector x.
     *
     * @param x input vector
     * @return maximum norm of x
     */
    private static double norm(final double[] x) {
        final int n = x.length;
        double y = 0;
        for (int i = 0; i < n; i++) {
            if (y < abs(x[i])) {
                y = abs(x[i]);
            }
        }
        return y;
    }

    /**
     * round a number with precision eps.
     *
     * @param x   double number to round
     * @param eps double the precision
     * @return x rounded
     */
    public static double round(final double x, final double eps) {
        final int digits = digits(eps);
        // if (abs(eps)<1) {
        // digits = -(int) log10(abs(eps));
        // }
        final double y = round(x, digits);
        return y;
    }

    private static int digits(final double eps) {
        int digits = 0;
        final double aeps = abs(eps);
        if (aeps <= 1 && aeps >= 1.E-20) {
            for (digits = 1; digits < 20; digits++) {
                if (aeps > IPOWERS[digits])
                    return digits - 1;
            }
        } else {
            digits = -(int) log10(abs(eps));
        }
        return digits;
    }

    /**
     * round a number to digits decimals.
     *
     * @param x      double number to round
     * @param digits long with the number of digits
     * @return double the rounded number
     */
    public static double round(final double x, final int digits) {
        double r, y = x;
        if (digits > 0) {
            if (digits < 21) {
                r = POWERS[digits];
            } else {
                r = pow(10, digits);
            }
            y = x * r;
            y = Math.round(y);
            y /= r;
        }
        return y;
    }
}
 