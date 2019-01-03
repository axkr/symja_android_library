/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2014,  Prof. Dr. Nikolaus Wulff
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
package de.lab4inf.math.functions;

import de.lab4inf.math.Differentiable;
import de.lab4inf.math.Function;

import static de.lab4inf.math.util.BinomialCoefficient.binomial;

/**
 * The Laguerre Polynomials L<sub>n</sub>(x) and L<sub>n</sub><sup>m</sup>(x).
 *
 * @author nwulff
 * @version $Id: Laguerre.java,v 1.4 2014/06/17 09:38:22 nwulff Exp $
 * @since 14.05.2014
 */
public class Laguerre extends L4MFunction implements Differentiable {
    private final int m;
    private final int n;
    private boolean signed = false;

    /**
     * Constructor for L_n(x).
     *
     * @param n the degree
     */
    public Laguerre(final int n) {
        this(n, 0);
    }

    /**
     * Constructor for L(n,m;x)
     *
     * @param n the degree
     * @param m the value of the generalized Laguerre polynomial.
     */
    public Laguerre(final int n, final int m) {
        this(n, m, false);
    }

    /**
     * Internal constructor for L(n,m;x) with optional sing used for the
     * derivative.
     *
     * @param n    the degree
     * @param m    the value of the generalized Laguerre polynomial.
     * @param sign
     */
    private Laguerre(final int n, final int m, final boolean sign) {
        this.n = n;
        this.m = m;
        this.signed = sign;
    }

    public static double laguerre(final int n, final double x) {
        double l1 = 1 - x, l0 = 1, ln = 0;
        if (n == 0) {
            ln = l0;
        } else if (n == 1) {
            ln = l1;
        } else {
            for (int k = 2; k <= n; k++) {
                ln = (2.0 * k - 1.0 - x) * l1 - (k - 1.0) * l0;
                ln /= k;
                l0 = l1;
                l1 = ln;
            }
        }
        return ln;
    }

    public static double laguerre(final int n, final int m, final double x) {
        if (m == 0)
            return laguerre(n, x);
        if (n == 0)
            return 1;
        return laguerreSeries(n, m, x);
    }

    public static double laguerreRecursive(final int n, final int m, final double x) {
        double lnm = 0;
        if (m == 0)
            return laguerre(n, x);

        for (int k = 0; k <= n; k++) {
            lnm += laguerreRecursive(k, m - 1, x);
        }
        return lnm;
    }

    public static double laguerreSeries(final int n, final int m, final double x) {
        double fk = 1, xk = -x, lk = binomial(n + m, n);
        if (m == 0)
            return laguerre(n, x);
        for (int k = 1; k <= n; k++) {
            fk *= k;
            lk += xk * binomial(n + m, n - k) / fk;
            xk *= -x;
        }
        return lk;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Differentiable#getDerivative()
     */
    @Override
    public Function getDerivative() {
        return new Laguerre(n - 1, m + 1, true);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.L4MFunction#f(double[])
     */
    @Override
    public double f(final double... x) {
        if (x.length != 1) {
            String msg = "Laguerre(n,m;x) needs one argument";
            logger.warn(msg);
            throw new IllegalArgumentException(msg);
        }
        double ret = laguerre(n, m, x[0]);
        if (signed)
            ret = -ret;
        return ret;
    }
}
 