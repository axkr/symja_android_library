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

import static de.lab4inf.math.util.Accuracy.hasReachedAccuracy;
import static java.lang.Math.PI;
import static java.lang.Math.cos;

/**
 * Utility class to calculate the Gauss-Legendre weights and abscissas.
 * Used for integrals of the form:
 * <pre>
 *        /+1         /+1
 *       / f(x) dx = / w(x)*f(x) dx
 *      /-1         /-1
 * </pre>
 * That is the weight function w(x) equals one.
 *
 * @author nwulff
 * @version $Id: GaussLegendre.java,v 1.7 2014/06/25 12:44:22 nwulff Exp $
 * @since 03.09.2009
 */
public final class GaussLegendre extends L4MObject {
    private static final double PRECISSION = 5.E-15;
    private static HashMap<Integer, XW> xwLegendre = new HashMap<Integer, XW>();

    /**
     * Hidden constructor.
     */
    private GaussLegendre() {
    }

    /**
     * Calculate the n zeros and weight factors of the Gauss-Legendre
     * polynomials with degree n.
     *
     * @param x array with the zeros
     * @param w array with the weight factors
     */
    static void calcCoefficients(final double[] x, final double[] w) {
        int i, j, n = x.length, m = (n + 1) / 2;
        double zn, zo, dP, p0, p1, p2;
        // logger.info(format("Legendre degree: %d",n));
        for (i = 1; i <= m; i++) {
            // initial guess of the i-th root of polynom p_n
            zn = cos(PI * (i - 0.25) / (n + 0.5));
            do {
                p0 = 1.0;
                p1 = 0.0;
                // recursive construction of the Legendre polynomials
                for (j = 1; j <= n; j++) {
                    p2 = p1;
                    p1 = p0;
                    p0 = ((2.0 * j - 1.0) * zn * p1 - (j - 1.0) * p2) / j;
                }
                // derivative of the Legendre polynom
                dP = n * (zn * p0 - p1) / (zn * zn - 1.0);
                zo = zn;
                // newtown iteration z_{j+1} = z_j - f(z_j)/f'(z_j)
                zn = zo - p0 / dP;
            } while (!hasReachedAccuracy(zn, zo, PRECISSION));
            // logger.info(format("z[%2d]=%.15f",i,zn));
            x[i - 1] = -zn;
            x[n - i] = zn;
            w[i - 1] = 2.0 / ((1.0 - zn * zn) * dP * dP);
            w[n - i] = w[i - 1];
        }
    }

    /**
     * Get the abscissas/zeros of the Legendre polynomial of degree n.
     *
     * @param n degree of polynomial
     * @return array of roots
     */
    public static double[] getAbscissas(final int n) {
        XW xw = getXW(n);
        return xw.x.clone();
    }

    /**
     * Get the abscissas/zeros of the Legendre polynomial of degree n
     * scaled to the interval [a,b].
     *
     * @param a left border
     * @param b right border
     * @param n degree of polynomial
     * @return array of roots
     */
    public static double[] getAbscissas(final double a, final double b,
                                        final int n) {
        double[] x = getAbscissas(n);
        // scale the roots if the interval [a,b] is not [-1,1]
        if (a != -1 || b != 1) {
            double xm = (a + b) / 2;
            double dx = (b - a) / 2;
            for (int k = 0; k < n; k++) {
                x[k] = dx * x[k] + xm;
            }
        }
        return x;
    }

    /**
     * Get the weight coefficients for the Gauss-Legendre integration.
     *
     * @param n degree of polynomial
     * @return weight array of size n
     */
    public static double[] getWeights(final int n) {
        XW xw = getXW(n);
        return xw.w.clone();
    }

    /**
     * Get the weight coefficients for the Gauss-Legendre integration.
     *
     * @param n degree of polynomial
     * @return weight array of size n
     */
    private static XW getXW(final int n) {
        XW xw = xwLegendre.get(n);
        if (xw == null) {
            xw = new XW();
            xw.x = new double[n];
            xw.w = new double[n];
            calcCoefficients(xw.x, xw.w);
            xwLegendre.put(n, xw);
        }
        return xw;
    }

    /**
     * Internal class to hold the abscissas and weights.
     */
    protected static class XW {
        double[] x, w;
    }

}
 