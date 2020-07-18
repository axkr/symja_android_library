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

package de.lab4inf.math.util;

import de.lab4inf.math.Function;
import de.lab4inf.math.L4MObject;
import de.lab4inf.math.gof.Visitor;

import static java.lang.Math.PI;
import static java.lang.Math.cos;

/**
 * Expansion of a function in terms of Chebyshev polynomials.
 * <pre>
 *           n
 *   f(x) = sum a_k * T_k(x)
 *          k=0
 * </pre>
 * Where the polynomials T_k(x) are defined recursive:
 * <pre>
 *
 *   T_0(x)     := 1
 *   T_1(x)     := x
 *   T_{n}(x) := 2*x*T_{n-1}(x) - T_{n-2}(x) ;   n &gt; 1
 *
 * </pre>
 * <hr/>
 * Given the coefficients a_k the summation can be done with
 * help of the Clenshaw algorithm coded within the method <b>cheby</b>.
 * <hr/>
 * To calculate the coefficients a_k use the method <b>coeff</b>.
 *
 * @author nwulff
 * @version $Id: ChebyshevExpansion.java,v 1.9 2014/11/16 21:47:23 nwulff Exp $
 * @since 14.09.2009
 */
public class ChebyshevExpansion extends L4MObject implements Function {
    private final double[] a;

    /**
     * Constructor for the given coefficients.
     *
     * @param a the coefficients
     */
    public ChebyshevExpansion(final double[] a) {
        this.a = a.clone();
    }

    /**
     * Constructor for the given function to order n within the interval [-1,1].
     *
     * @param n   the order
     * @param fct the function to approximate
     */
    public ChebyshevExpansion(final int n, final Function fct) {
        this(coeff(n, fct));
    }

    /**
     * Constructor for the given function to order n within the interval [a,b].
     *
     * @param n   the order
     * @param a   the left border
     * @param b   the right border
     * @param fct the function to approximate
     */
    public ChebyshevExpansion(final int n, final double a, final double b, final Function fct) {
        this(coeff(n, a, b, fct));
    }

    /**
     * Calculate the Chebyshev series expansion for the given coefficients
     * using the Clenshaw algorithm.
     *
     * @param a the coefficients
     * @param x the argument
     * @return f(x) = sum a_k T_k(x)
     */
    public static double cheby(final double x, final double[] a) {
        int n = a.length;
        double b, bp = 0, bpp = 0;
        for (n--; n > 0; bpp = bp, bp = b, n--) {
            b = 2 * x * bp - bpp + a[n];
        }
        b = x * bp - bpp + a[0];
        return b;
    }

    /**
     * Calculate the Chebyshev coefficients for the best approximation of
     * f(x) = sum_k a_k T_k(x) of order n over the interval [-1,1].
     *
     * @param n   the maximal order
     * @param fct function to approximate
     * @return the a_k array
     */
    public static double[] coeff(final int n, final Function fct) {
        double t2, t1, t0, zk;
        final double[] a = new double[n];
        final double[] x = new double[n];
        final double[] y = new double[n];
        for (int k = 1; k <= n; k++) {
            zk = PI * (k - 0.5) / n;
            x[k - 1] = cos(zk);
            y[k - 1] = fct.f(x[k - 1]);
        }

        for (int k = 0; k < n; k++) {
            t0 = 2.0 / n;
            t1 = 2 * x[k] / n;
            a[0] += y[k] / n;
            a[1] += y[k] * t1;
            for (int j = 0; j < n - 2; t0 = t1, t1 = t2, j++) {
                t2 = 2 * x[k] * t1 - t0;
                a[j + 2] += y[k] * t2;
            }
        }
        return a;
    }

    /**
     * Calculate the Chebyshev coefficients for the best approximation of
     * f(x) = sum_k a_k T_k(x) of order n over the interval [a,b].
     *
     * @param n   the maximal order
     * @param a   left interval border
     * @param b   right interval border
     * @param fct function to approximate
     * @return the a_k array
     */
    public static double[] coeff(final int n, final double a, final double b, final Function fct) {
        return coeff(n, new ChebyshevTrafo(a, b, fct));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        return cheby(x[0], a);
    }

    /**
     * Access to the internal coefficient array.
     *
     * @return coefficients
     */
    double[] getCoeff() {
        return a;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.gof.Visitable#accept(de.lab4inf.math.gof.Visitor)
     */
    @Override
    public void accept(final Visitor<Function> visitor) {
        visitor.visit(this);
    }

    /**
     * Internal function wrapper to shift a function from the [a,b] interval
     * to the unit [-1,1] interval for the Chebyshev coefficients calculation.
     */
    private static class ChebyshevTrafo implements Function {
        private final Function fct;
        private final double a, b;

        public ChebyshevTrafo(final double a, final double b, final Function f) {
            fct = f;
            this.a = 2.0 / (b - a);
            this.b = (a + b) / (a - b);
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Function#f(double[])
         */
        @Override
        public double f(final double... x) {
            final double y = (x[0] - b) / a;
            return fct.f(y);
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.gof.Visitable#accept(de.lab4inf.math.gof.Visitor)
         */
        @Override
        public void accept(final Visitor<Function> visitor) {
            visitor.visit(this);
        }
    }

}
 