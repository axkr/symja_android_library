/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
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

package de.lab4inf.math.integration;

import de.lab4inf.math.Function;
import de.lab4inf.math.L4MObject;
import de.lab4inf.math.util.Aitken;

import static de.lab4inf.math.util.Accuracy.hasReachedAccuracy;
import static java.lang.String.format;

/**
 * Numerical integration of indefinite exponential falling integrals.
 * These have the form:
 * <pre>
 *
 *   +infinity
 *      /
 *     / f(x)*exp(-x) dx
 *    /
 *   0
 *
 * </pre>
 * <p>
 * or with a further power
 * <p>
 * <pre>
 *
 *   +infinity
 *      /
 *     / x**a * f(x)*exp(-x) dx
 *    /
 *   0
 *
 * </pre>
 *
 * @author nwulff
 * @version $Id: LaguerreIntegrator.java,v 1.7 2011/03/11 07:05:26 nwulff Exp $
 * @since 07.09.2009
 */
public final class LaguerreIntegrator extends L4MObject {
    private static final String FMT = "Bad convergence! Limiting value: %f";
    /**
     * default integration precision.
     */
    private static final double EPS = 1.E-8;
    /**
     * minimal number of interval splits.
     */
    private static final int NMIN = 6;
    /**
     * maximal number of interval splits.
     */
    private static final int NMAX = 128;
    /**
     * throw an exception if no convergence.
     */
    private static boolean throwing = false;
    /**
     * should Aitken acceleration be used.
     */
    private static boolean usingAitken = true;

    /**
     * Hidden constructor.
     */
    private LaguerreIntegrator() {
    }

    /**
     * Indicate if the Aitken method is enabled.
     *
     * @return the usingAitken
     */
    public static boolean isUsingAitken() {
        return usingAitken;
    }

    /**
     * Set the Aitken acceleration flag.
     *
     * @param usingAitken the usingAitken to set
     */
    public static void setUsingAitken(final boolean usingAitken) {
        LaguerreIntegrator.usingAitken = usingAitken;
    }

    /**
     * Indicate if no convergence will throw an exception.
     *
     * @return the throwing flag
     */
    public static boolean isThrowing() {
        return throwing;
    }

    /**
     * Set the throwing flag. If true an exception
     * is thrown if no convergence is reached
     * otherwise only a warning will be logged.
     *
     * @param throwing the throwing to set
     */
    public static void setThrowing(final boolean throwing) {
        LaguerreIntegrator.throwing = throwing;
    }

    /**
     * Integrate the given function with default precision.
     *
     * @param fct to integrate
     * @return integral from zero to infinity
     */
    public static double integrate(final Function fct) {
        return integrate(EPS, fct);
    }

    /**
     * Integrate the given function within the given precision.
     *
     * @param eps precision to reach
     * @param fct to integrate
     * @return integral from zero to infinity
     */
    public static double integrate(final double eps, final Function fct) {
        return integrate(eps, fct, 0);
    }

    /**
     * Integrate the given function with default precision for the given
     * exponent.
     *
     * @param a   the exponent to use
     * @param fct to integrate
     * @return integral from zero to infinity
     */
    public static double integrate(final Function fct, final double a) {
        return integrate(EPS, fct, a);
    }

    /**
     * Integrate the given function within the given precision for the given
     * exponent.
     *
     * @param eps precision to reach
     * @param fct to integrate
     * @param a   the exponent to use
     * @return integral from zero to infinity
     */
    @SuppressWarnings("rawtypes")
    public static double integrate(final double eps, final Function fct,
                                   final double a) {
        final Aitken aitken = new Aitken();
        final int max = 128;
        double[] x, w;
        double z, yo, yn = 0;
        int k, n = 2;
        do {
            n *= 2;
            if (a == 0) {
                w = GaussLaguerre.getWeights(n);
                x = GaussLaguerre.getAbscissas(n);
            } else {
                w = GaussLaguerre.getWeights(a, n);
                x = GaussLaguerre.getAbscissas(a, n);
            }
            yo = yn;
            for (z = 0, k = 0; k < n; z += w[k] * fct.f(x[k]), k++) ;
            yn = z;
            if (usingAitken) {
                yn = aitken.next(z);
            }
        } while ((!hasReachedAccuracy(yn, yo, eps) || n < NMIN) && n < NMAX);
        if (n > max) {
            String msg = format(FMT, yn);
            getLogger().warn(msg);
            if (throwing) {
                throw new ArithmeticException(msg);
            }
        }
        return yn;
    }
}
 