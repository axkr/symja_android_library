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
package de.lab4inf.math.differentiation;

import de.lab4inf.math.Function;
import de.lab4inf.math.L4MObject;

import static de.lab4inf.math.lapack.LinearAlgebra.mult;
import static de.lab4inf.math.lapack.LinearAlgebra.sub;
import static de.lab4inf.math.util.Accuracy.hasReachedAccuracy;
import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.String.format;

/**
 * Calculate the hessian matrix of a real valued function using approximate
 * partial derivatives. <br/>
 * <p>
 * This implementation is based on formula 25.3.23 and 25.3.26 from
 * "Pocketbook of Mathematical Functions", Abramowitz and Stegun,  (1984).
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: HessianApproximator.java,v 1.10 2010/09/09 17:52:35 nwulff Exp $
 * @since 13.10.2007
 */
public class HessianApproximator extends L4MObject implements Hessian {
    /**
     * maximal number of interval splitting.
     */
    public static final int NMAX = 16;
    /**
     * Default accuracy of 1E-8.
     */
    public static final double EPS_DEFAULT = 1.E-6;
    /**
     * Default step width of 5E-4.
     */
    public static final double H_START = 0.05;
    private final Function function;
    private double eps = EPS_DEFAULT;

    /**
     * Constructor for the hessian matrix of the given function.
     *
     * @param fct function to wrap
     */
    public HessianApproximator(final Function fct) {
        function = fct;
    }

    /**
     * Calculate the hessian matrix with given step width.
     *
     * @param h the step width
     * @param x the current parameter values
     * @return the hessian matrix
     */
    public double[][] calcHessian(final double h, final double... x) {
        final int n = x.length;
        double[][] hess = new double[n][n];
        double xi, xj, ddF, fpp, fmm, fpm, fmp, hi, hj, h4;
        for (int i = 0; i < n; i++) {
            xi = x[i];
            hi = max(1, abs(xi)) * h;
            ddF = function.f(x);
            x[i] = xi + hi;
            fpp = function.f(x);
            x[i] = xi - hi;
            fmm = function.f(x);
            x[i] = xi;
            ddF = (fpp - 2 * ddF + fmm) / (hi * hi);
            checkNaN(i, i, ddF);
            hess[i][i] = ddF;
            for (int j = 0; j < i; j++) {
                xj = x[j];
                hj = max(1, abs(xj)) * h;
                h4 = 4 * hi * hj;
                x[i] = xi + hi;
                x[j] = xj + hj;
                fpp = function.f(x);

                x[i] = xi + hi;
                x[j] = xj - hj;
                fpm = function.f(x);

                x[i] = xi - hi;
                x[j] = xj + hj;
                fmp = function.f(x);

                x[i] = xi - hi;
                x[j] = xj - hj;
                fmm = function.f(x);

                ddF = (fpp + fmm - fpm - fmp) / h4;
                checkNaN(i, j, ddF);

                hess[i][j] = ddF;
                hess[j][i] = ddF;
                x[j] = xj;
                x[i] = xi;
            }
        }
        return hess;
    }

    /**
     * Check that the actual step width is valid, otherwise half it.
     *
     * @param x the initial parameter values
     * @return the number of step width increments
     */
    private int calcInitialStepWidth(final double... x) {
        int n = 0;
        double h = H_START;
        boolean hOk = true;
        // check if H value is within range of function
        do {
            double hi, xi, f, fac = 1;
            int l = x.length;
            hOk = true;
            try {
                for (int i = 0; i < l; i++) {
                    xi = x[i];
                    hi = max(1, abs(xi)) * h;
                    x[i] += hi / fac;
                    f = function.f(x);
                    if (Double.isNaN(f)) {
                        x[i] = xi;
                        n++;
                        hOk = false;
                        break;
                    }
                    x[i] = xi - hi / fac;
                    f = function.f(x);
                    if (Double.isNaN(f)) {
                        x[i] = xi;
                        n++;
                        hOk = false;
                        break;
                    }
                    x[i] = xi;
                }
            } catch (IllegalArgumentException error) {
                fac /= 2;
                n++;
                hOk = false;
            }
        } while (!hOk && n < NMAX);
        return n;
    }

    /**
     * Check if x value is a number.
     *
     * @param i 1.st index
     * @param j 2.nd index
     * @param x the value to check
     */
    private void checkNaN(final int i, final int j, final double x) {
        if (Double.isNaN(x)) {
            String msg = format("NaN Hess[%d][%d]", i, j);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * The accuracy to use.
     *
     * @return precision of the gradient approximation.
     */
    public double getEps() {
        return eps;
    }

    /**
     * Set the accuracy of the gradient.
     *
     * @param eps precision of approximation
     */
    public void setEps(final double eps) {
        this.eps = eps;
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.differentiation.Hessian#hessian(double[])
     */
    public double[][] hessian(final double... x) {
        double[][] hess, hessOld, matH, matO;
        int n = calcInitialStepWidth(x);
        double h = H_START / Math.pow(2, n);

        if (abs(H_START - h) >= H_START / 2) {
            getLogger().warn("h start adjusted to " + h);
        }
        matH = calcHessian(h, x);
        hess = matH;
        do {
            h /= 2;
            matO = matH;
            hessOld = hess;
            matH = calcHessian(h, x);
            // correct for the error O(h**2)
            hess = mult(sub(mult(matH, 4), matO), 1. / 3.);
        } while (!hasReachedAccuracy(hess, hessOld, eps) && (++n < NMAX));
        if (n >= NMAX) {
            String msg = String.format(
                    "Hessian no convergence after %d iterations", n);
            getLogger().warn(msg);
        }
        return hess;
    }
}
 