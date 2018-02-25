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

package de.lab4inf.math.fitting;

import static java.lang.Math.pow;
import static java.lang.String.format;

/**
 * Generic fit for a rational approximation by two polynomials.
 * <pre>
 *         p(x)    sum p_j*x**j     p_0 + p_1*x + ... p_n x**n
 *  f(x) = ---- = --------------- = ---------------------------
 *         q(x)    sum q_k*x**k      1  + q_1*x + ... q_m x**m
 * </pre>
 * The lowest factor q_0 of the divider polynomial is forced to be one
 * and excluded from the fit parameters.
 *
 * @author nwulff
 * @version $Id: RationalFitter.java,v 1.8 2010/12/19 21:57:54 nwulff Exp $
 * @since 30.08.2009
 */
public class RationalFitter extends GenericFitter {
    private int p, q;

    /**
     * @param n the degree of the numerator polynomial
     * @param m the degree of the divider polynomial
     */
    public RationalFitter(final int n, final int m) {
        super(n + m + 1);
        this.p = n;
        this.q = m;
        setEps(1.E-5);
        setUsePenalty(false);
        setApproximate(false);
        setNewton(false);
    }

    /**
     * Calculate the numerator polynomial.
     *
     * @param x the argument
     * @return p(x)
     */
    public final double p(final double x) {
        double xi = 1, ret = a[0];
        for (int i = 1; i <= p; i++) {
            xi *= x;
            ret += a[i] * xi;
        }
        return ret;
    }

    /**
     * Calculate the divider polynomial.
     *
     * @param x the argument
     * @return q(x)
     */
    public final double q(final double x) {
        double xi = 1, ret = 1;
        for (int i = 1; i <= q; i++) {
            xi *= x;
            ret += a[p + i] * xi;
        }
        return ret;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#dFct(int, double)
     */
    @Override
    protected double dFct(final int k, final double x) {
        // dr/dp_j = x**k/q(x)
        // dr/dq_j = -r*x**(k-p)/q(x)
        double pk, qx = q(x);
        if (k <= p) {
            switch (k) {
                case 0:
                    pk = 1;
                    break;
                case 1:
                    pk = x;
                    break;
                default:
                    pk = pow(x, k);
            }
        } else {
            pk = -pow(x, k - p) * p(x) / qx;
        }
        return pk / qx;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#ddFct(int, int, double)
     */
    @Override
    protected double ddFct(final int k, final int j, final double x) {
        // d2r/dp_jdp_k = 0 k & j <=p
        // d2r/dp_jdq_k = -x**(j+k-p)/(q*q) k | j>p
        // d2r/dq_jdq_k = 2r*x**(j+k-2p)/(q*q) k & j>p
        if ((k <= p && j <= p)) {
            return 0;
        }
        double v = -1, qx = q(x), qx2 = qx * qx;
        int kpj = k + j - p;
        if (k > p && j > p) {
            kpj -= p;
            v = 2 * p(x) / qx;
        }
        return v * pow(x, kpj) / qx2;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#fct(double)
     */
    @Override
    public double fct(final double x) {
        double r;
        double numerator = p(x);
        double divider = q(x);
        if (divider == 0) {
            if (numerator != 0) {
                getLogger().error(format("q(%f)=0!", x));
                return Math.signum(numerator) * Double.MAX_VALUE;
            }
            return 0;
        }
        r = numerator / divider;
        return r;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.fitting.GenericFitter#initParameters(double[], double[])
     */
    @Override
    protected void initParameters(final double[] x, final double[] y) {
        // we have no clue what to do so simply try this,
        // there might be better ways to do...
        a[0] = 1;
        a[p] = 1;
        a[p + q] = 1;
    }
}
 