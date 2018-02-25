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

package de.lab4inf.math.integration;

import java.util.HashMap;

import de.lab4inf.math.L4MObject;

import static de.lab4inf.math.functions.Gamma.lngamma;
import static de.lab4inf.math.util.Accuracy.hasReachedAccuracy;
import static java.lang.Math.exp;
import static java.lang.String.format;

/**
 * Utility class to calculate the Gauss-Laguerre weights and abscissas,
 * which are the zeros of the Laguerre polynomials. Used for integrals of the
 * form:
 * <pre>
 *     +infinity                 +infinity
 *        /                         /
 *       / f(x)*x**v*exp(-x) dx  = / f(x)*w(x) dx  ~ sum_k w_k f(x_k)
 *      /0                        /0
 * </pre>
 * <p>
 * or the special case v=zero.
 * <p>
 * <pre>
 *     +infinity
 *        /
 *       / f(x) * exp(-x) dx
 *      /0
 * </pre>
 * <p>
 * That is the weight function is w(x) = exp(v*ln(x)-x).
 *
 * @author nwulff
 * @version $Id: GaussLaguerre.java,v 1.6 2010/04/25 10:08:29 nwulff Exp $
 * @since 07.09.2009
 */
public final class GaussLaguerre extends L4MObject {
    private static final String XWFMT = "xw[%d,%f]";
    private static final double PRECISSION = 5.E-15;
    private static HashMap<String, XW> xwLaguerre = new HashMap<String, XW>();

    /**
     * Hidden constructor.
     */
    private GaussLaguerre() {
    }

    /**
     * Calculate the n zeros and weight factors of the Gauss-Laguerre polynomials.
     *
     * @param v the exponent for x**v
     * @param x array with the zeros
     * @param w array with the weight factors
     */
    static void calcCoefficients(final double v, final double[] x,
                                 final double[] w) {
        int i, j, n = x.length;
        double ai, zn = 0, zo, dP, p0, p1, p2;
        getLogger().info(format("Laguerre degree: %d v=%.3g", n, v));
        for (i = 1; i <= n; i++) {
            // initial guess of the i-th root of polynom p_n
            if (i == 1) {
                zn = (1 + v) * (3 + 0.92 * v) / (1 + 2.4 * n + 1.8 * v);
            } else if (i == 2) {
                zn += (15 + 6.25 * v) / (1 + 0.9 * v + 2.5 * n);
            } else {
                ai = i - 2;
                zn += ((1 + 2.55 * ai) / (1.9 * ai) + 1.26 * ai * v / (1 + 3.5 * ai)) * (zn - x[i - 3])
                        / (1 + 0.3 * v);
            }
            do {
                p0 = 1.0;
                p1 = 0.0;
                // recursive construction of the Laguerre polynomials
                for (j = 1; j <= n; j++) {
                    p2 = p1;
                    p1 = p0;
                    p0 = ((2 * j - 1 + v - zn) * p1 - (j - 1 + v) * p2) / j;
                }
                // derivative of the Laguerre polynom
                dP = (n * p0 - (n + v) * p1) / zn;
                zo = zn;
                // newtown iteration z_{j+1} = z_j - f(z_j)/f'(z_j)
                zn = zo - p0 / dP;
            } while (!hasReachedAccuracy(zn, zo, PRECISSION));
            getLogger().info(format("z[%2d]=%.15f", i, zn));
            x[i - 1] = zn;
            w[i - 1] = -exp(lngamma(v + n) - lngamma(n)) / (n * p1 * dP);
        }
    }

    /**
     * Calculate the n zeros and weight factors of the Gauss-Laguerre polynomials
     * for the special case v=0.
     *
     * @param x array with the zeros
     * @param w array with the weight factors
     */
    static void calcCoefficients(final double[] x, final double[] w) {
        calcCoefficients(0, x, w);
    }

    /**
     * Get the abscissas/zeros of the Laguerre polynomial of degree n for
     * an exponent v=0.
     *
     * @param n degree of polynomial
     * @return array of roots
     */
    public static double[] getAbscissas(final int n) {
        String name = format(XWFMT, n, 0.0);
        XW xw = xwLaguerre.get(name);
        if (xw == null) {
            xw = new XW();
            xw.x = new double[n];
            xw.w = new double[n];
            calcCoefficients(xw.x, xw.w);
            xwLaguerre.put(name, xw);
        }
        return xw.x.clone();
    }

    /**
     * Get the weight coefficients for the Gauss-Laguerre integration
     * for the exponent v=0.
     *
     * @param n degree of polynomial
     * @return weight array of size n
     */
    public static double[] getWeights(final int n) {
        String name = format(XWFMT, n, 0.0);
        XW xw = xwLaguerre.get(name);
        if (xw == null) {
            xw = new XW();
            xw.x = new double[n];
            xw.w = new double[n];
            calcCoefficients(xw.x, xw.w);
            xwLaguerre.put(name, xw);
        }
        return xw.w.clone();
    }

    /**
     * Get the abscissas/zeros of the Legendre polynomial of degree n with
     * exponent v.
     *
     * @param v exponent
     * @param n degree of polynomial
     * @return array of roots
     */
    public static double[] getAbscissas(final double v, final int n) {
        String name = format(XWFMT, n, v);
        XW xw = xwLaguerre.get(name);
        if (xw == null) {
            xw = new XW();
            xw.x = new double[n];
            xw.w = new double[n];
            // if there are too many fetched results from different
            // v values, clear the cache
            if (xwLaguerre.size() > 50) {
                xwLaguerre.clear();
            }
            calcCoefficients(v, xw.x, xw.w);
            xwLaguerre.put(name, xw);
        }
        return xw.x.clone();
    }

    /**
     * Get the weight coefficients for the Gauss-Laguerre integration for an
     * exponent v not zero.
     *
     * @param v exponent
     * @param n degree of polynomial
     * @return weight array of size n
     */
    public static double[] getWeights(final double v, final int n) {
        String name = format(XWFMT, n, v);
        XW xw = xwLaguerre.get(name);
        if (xw == null) {
            xw = new XW();
            xw.x = new double[n];
            xw.w = new double[n];
            calcCoefficients(v, xw.x, xw.w);
            xwLaguerre.put(name, xw);
        }
        return xw.w.clone();
    }

    /**
     * Internal class to hold the abscissas and weights.
     */
    protected static class XW {
        double[] x, w;
    }

}
 