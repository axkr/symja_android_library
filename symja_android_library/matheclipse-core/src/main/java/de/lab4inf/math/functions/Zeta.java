/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2010,  Prof. Dr. Nikolaus Wulff
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

import java.util.Iterator;

import de.lab4inf.math.CFunction;
import de.lab4inf.math.Complex;
import de.lab4inf.math.Letters;
import de.lab4inf.math.sets.ComplexNumber;
import de.lab4inf.math.util.PrimeNumbers;

import static de.lab4inf.math.functions.Power.pow;
import static de.lab4inf.math.functions.Sine.sin;
import static de.lab4inf.math.sets.ComplexNumber.ONE;
import static de.lab4inf.math.sets.ComplexNumber.ZERO;
import static de.lab4inf.math.util.Accuracy.hasConverged;
import static de.lab4inf.math.util.Accuracy.isEqual;
import static de.lab4inf.math.util.Accuracy.isInteger;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Math.PI;
import static java.lang.Math.abs;

/**
 * Basic implementation of Riemann's Zeta function &zeta;(z) for real and
 * complex arguments z. The implementation is based on
 * <pre>
 * "Numerical evaluation of the Riemann Zeta-function" </br>
 *  by X. Gourdon and P. Sebah, Number, constants and computation (2003).
 * </pre>
 * <p>
 * Using an alternating series the accuracy is of order 1.E-14 for real
 * arguments.
 * <p>
 * In the complex domain on the critical strip z=1/2 + j*y
 * the error is of order:
 * <pre>
 *   1.E-14 for values |y|&lt;30.
 *   1.E-12 for values |y|&lt;40.
 *   1.E-9  for values |y|&lt;50.
 *   1.E-8  for values |y|&lt;60.
 *   1.E-6  for values |y|&lt;70.
 * </pre>
 *
 * @author nwulff
 * @version $Id: Zeta.java,v 1.24 2014/11/18 23:41:21 nwulff Exp $
 * @since 08.09.2010
 */
public class Zeta extends L4MFunction implements CFunction {
    /**
     * the greek &zeta; character as unicode.
     */
    public static final char ZETA = Letters.ZETA;
    protected static final double EPS = 5.E-16;
    /**
     * singular message.
     */
    private static final String SINGULAR = ZETA + "(1.0) is singular";
    /**
     * reference to the ARG attribute.
     */
    private static final String ARG = "(%6.3f)=%+f ";
    private static final char PM = '\u00B1';
    /**
     * reference to the FMT attribute.
     */
    private static final String FMT = ZETA + ARG + PM + " %.1g n:%d";
    /**
     * reference to the AFMT attribute.
     */
    private static final String AFMT = ZETA + ARG;
    /**
     * reference to the PFMT attribute.
     */
    private static final String PFMT = ZETA + ARG + PM + " %.1g p:%.0f";
    private static final int MAX = Integer.MAX_VALUE - 3;
    private static PrimeNumbers primes;

    /**
     * Calculate the real zeta(x) function.
     *
     * @param x the real argument
     * @return zeta(x)
     */
    public static double zeta(final double x) {
        return zeta(0, x);
    }

    /**
     * Calculate the complex zeta(z) function.
     *
     * @param z the complex argument
     * @return zeta(z)
     */
    public static Complex zeta(final Complex z) {
        return zeta(0, z);
    }

    /**
     * Calculate the complex zeta(z).
     *
     * @param method to use 0=alternating, 1=product 2=summation formula
     * @param z      the complex argument
     * @return zeta(z)
     */
    private static Complex zeta(final int method, final Complex z) {
        Complex zeta = ZERO;
        if (z.isReal()) {
            return new ComplexNumber(zeta(z.real()));
        }
        if (z.real() < 0) {
            final Complex mz = ONE.minus(z);
            // use reflection formula A&ST 23.2.6
            Complex v = sin(z.multiply(PI / 2));
            if (v.abs().doubleValue() > EPS) {
                v = v.multiply(pow(2 * PI, z).div(PI).multiply(Gamma.gamma(mz)));
                zeta = zeta(method, mz).multiply(v);
            }
            return zeta;
        }
        return zetaSumAlternating(z);
    }

    /**
     * Calculate the real zeta(x).  For negative x reflection formula
     * A&amp;ST 23.2.6 is used.
     *
     * @param method to use 0=alternating, 1=product 2=summation formula
     * @param x      the real argument
     * @return zeta(x)
     */
    static double zeta(final int method, final double x) {
        double zeta = 0;
        if (isEqual(x, 1.0)) {
            getLogger().info(SINGULAR);
            return POSITIVE_INFINITY;
        } else if (isEqual(x, 0.0)) {
            zeta = -0.5;
        } else if (x < 0) {
            // use reflection formula A&ST 23.2.6
            double v = sin(PI * x / 2);
            if (abs(v) > EPS) {
                v *= pow(2 * PI, x) / PI * Gamma.gamma(1 - x);
                zeta = zeta(method, 1 - x) * v;
            }
            return zeta;
        } else {
            switch (method) {
                case 2:
                    zeta = zetaSum(x);
                    break;
                case 1:
                    zeta = zetaProd(x);
                    break;
                default:
                    zeta = zetaSumAlternating(x);
            }
        }
        return zeta;
    }

    /**
     * Zeta function based on an approximation of the alternating
     * zeta function. For the coefficient ek we use the recursion
     * <pre>
     *  e(1) = 2**n - 1
     *  e(k+1) = e(k) - (n over k)
     * </pre>
     * and (n over k) can be calculated directly within the k-loop
     *
     * @param s the value has be positive!
     * @return zeta(s)
     */
    private static double zetaSumAlternating(final double s) {
        final int n = 22; // for 16 digits precision
        final double np = n + 1, v = (1 - pow(2, 1 - s));
        int k, j = n + 1;
        double zeta, pk, pj;
        final double tn = 1 << n;
        double ek = tn - 1, bk = 1, sk = 0, sj = 0;
        for (k = 1; k <= n; k++, j++) {
            pk = pow(k, -s);
            pj = pow(j, -s);
            // as long as n is even pk and pj have equal sign change
            if ((k & 1) == 0) {
                sk -= pk;
                sj -= pj * ek;
            } else {
                sk += pk;
                sj += pj * ek;
            }
            // bk = binomial(n,k);
            bk *= (np - k) / k;
            // ek = ek(n, k);
            ek -= bk;
        }
        zeta = (sk + sj / tn) / v;
        if (getLogger().isInfoEnabled()) {
            final String msg = String.format(AFMT, s, zeta);
            getLogger().info(msg);
        }
        return zeta;
    }

    /**
     * Zeta function based on an approximation of the alternating
     * zeta function. For the coefficient ek we use the recursion
     * <pre>
     *  e(1) = 2**n - 1
     *  e(k+1) = e(k) - (n over k)
     * </pre>
     * and (n over k) can be calculated directly within the k-loop
     *
     * @param s the real part has to be positive!
     * @return zeta(s)
     */
    private static Complex zetaSumAlternating(final Complex s) {
        final int n = 30; // for 16 digits precision
        Complex zeta, pk, pj, sk = ZERO, sj = ZERO;
        final Complex v = ONE.minus(pow(2, ONE.minus(s)));
        final Complex ms = s.multiply(-1);
        int k, j = n + 1;
        final double np = n + 1, tn = 1 << n;
        double ek = tn - 1, bk = 1;
        for (k = 1; k <= n; k++, j++) {
            pk = pow(k, ms);
            pj = pow(j, ms);
            // as long as n is even pk and pj have equal sign change
            if ((k & 1) == 0) {
                sk = sk.minus(pk);
                sj = sj.minus(pj.multiply(ek));
            } else {
                sk = sk.plus(pk);
                sj = sj.plus(pj.multiply(ek));
            }
            // bk = binomial(n,k);
            bk *= (np - k) / k;
            // ek = ek(n, k);
            ek -= bk;
        }
        zeta = sk.plus(sj.div(tn)).div(v);
        return zeta;
    }

    /**
     * Zeta function based on the prime product. A&amp;ST 23.2.2.</br>
     * Note: This implementation is not very efficient, converges slowly and
     * is limited by the number of available prime numbers.
     *
     * @param s the value
     * @return zeta(s)
     */
    private static double zetaProd(final double s) {
        int n = 2, max;
        double delta, p, zold, zeta = 0;
        if (null == primes) {
            primes = new PrimeNumbers();
        }
        final Iterator<Long> ite = primes.getLongIterator();
        if (isInteger(s)) {
            final int k = (int) s;
            if ((k % 2) == 0 && k >= 2) {
                return zetaEven(k);
            }
        }
        max = primes.getNumCached();
        do {
            p = ite.next();
            zold = zeta;
            zeta += Math.log1p(-pow(p, -s));
        } while (!hasConverged(zeta, zold, EPS, ++n, max));
        zeta = Math.exp(-zeta);
        if (getLogger().isInfoEnabled()) {
            delta = zeta - Math.exp(-zold);
            final String msg = String.format(PFMT, s, zeta, delta, p);
            getLogger().info(msg);
        }
        return zeta;
    }

    /**
     * Recursive Zeta function for positive even integer argument.
     *
     * @param m the argument of the form m = 2*n for n>=1.
     * @return zeta(m)
     */
    private static double zetaEven(final int m) {
        final int n = m / 2;
        double sum = 0;
        if (m == 2) {
            return PI * PI / 6;
        }
        for (int k = 2; k < m; k += 2) {
            sum += zetaEven(k) * zetaEven(m - k);
        }
        return sum / (n + 0.5);
    }

    /**
     * Zeta function based on the definition the sum of powers. <br/>
     * Note: This implementation is not very efficient and converges slowly.
     *
     * @param s the value
     * @return zeta(s)
     */
    private static double zetaSum(final double s) {
        int n = 2;
        final int max = MAX;
        double delta, zold, zeta = 1;
        if (isInteger(s)) {
            final int k = (int) s;
            if ((k % 2) == 0 && k >= 2) {
                return zetaEven(k);
            }
        }
        do {
            zold = zeta;
            delta = pow(n, -s);
            zeta += delta;
        } while (!hasConverged(zeta, zold, EPS, ++n, max));

        final String msg = String.format(FMT, s, zeta, delta, n);
        getLogger().info(msg);
        return zeta;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        if (null == x || x.length != 1) {
            throw new IllegalArgumentException("one real expected");
        }
        return zeta(x[0]);
    }

    /*
    * (non-Javadoc)
    *
    * @see de.lab4inf.math.CFunction#f(de.lab4inf.math.Complex[])
    */
    @Override
    public Complex f(final Complex... z) {
        if (null == z || z.length != 1) {
            throw new IllegalArgumentException("one complex expected");
        }
        return zeta(0, z[0]);
    }
}
 