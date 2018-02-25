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
import de.lab4inf.math.Integrable;
import de.lab4inf.math.util.Strings;

import static de.lab4inf.math.functions.Gamma.gamma;
import static de.lab4inf.math.util.Accuracy.hasConverged;
import static de.lab4inf.math.util.Accuracy.isInteger;
import static java.lang.Math.abs;
import static java.lang.Math.exp;
import static java.lang.Math.min;
import static java.lang.String.format;

/**
 * The Kummer function M(a,b;x) also known as confluent hypergeometric function
 * <sub>1</sub>F<sub>1</sub>(a,b;x) is a solution of the differential equation:
 * <pre>
 *
 *  x*y'' + (b-x)y' - ay = 0
 *
 * </pre>
 * <p>
 * To be found in A&amp;ST 13.1. The Taylor series expansion is:
 * <p>
 * <pre>
 *              &infin;
 *  M(a,b;x) =  &sum; (a)<sub>k</sub>/(b)<sub>k</sub> x<sup>k</sup>/k!
 *             k=0
 * </pre>
 * <p>
 * <p>
 * <p>
 * <hr/>
 * Relations for some special a,b values:
 * <pre>
 *  M(0,b,x) = 1
 *  M(b,b,x) = exp(x)
 *  M(1,2,2x)= sinh(x)*exp(x)/x
 *  M(a,a+1,-x) = ax<sup>-a</sup>&gamma;(a,x)
 *  M(1/2,3/2,-x<sup>2</sup>) = &radic;(&pi;) erf(x)/(2*x)
 * </pre>
 *
 * @author nwulff
 * @version $Id: KummerFunction.java,v 1.24 2014/11/16 21:47:23 nwulff Exp $
 * @since 14.05.2014
 */
public class KummerFunction extends L4MFunction implements Differentiable, Integrable {
    public static final String KUMMER = format("%sF%s", Strings.toLowerScript(1), Strings.toLowerScript(1));
    private static final int MIN_A_B = 25;
    private static final String B_0 = "b=%.0f none positiv integer for " + KUMMER;
    private static final double PRECISSION = 5.E-14;
    private static final int MAX_ITERATIONS = 200;
    private final boolean abSetted;
    private double a, b, scale = 1;

    /**
     * Public default constructor
     */
    public KummerFunction() {
        abSetted = false;
    }

    /**
     * Constructor with given a and b values.
     *
     * @param a first positive parameter
     * @param b second positive parameter
     */
    public KummerFunction(final double a, final double b) {
        this.a = a;
        this.b = b;
        abSetted = true;
        checkB(b);
    }

    /**
     * Constructor with given a and b values.
     *
     * @param a first positive parameter
     * @param b second positive parameter
     */
    public KummerFunction(final Double a, final Double b) {
        this(a.doubleValue(), b.doubleValue());
    }

    private static void checkB(final double b) {
        if (b <= 0 && isInteger(b))
            throw new IllegalArgumentException(format(B_0, b));
    }

    /**
     * Calculation of the Kummer function M(a,b; x)
     *
     * @param a first parameter
     * @param b second parameter must be positive
     * @param x the real argument
     * @return M(a, b; x)
     */
    public static double kummer(final double a, final double b, final double x) {
        checkB(b);
        double y;
        if (a == 0) {
            y = 1;
        } else if (a == b) {
            y = exp(x);
        } else {
            // check which method might be appropriate
            double r1, r2, rt;
            // values for M(a,b,x)
            r1 = abs(a);
            r2 = abs(b);
            // values for transformed M(b-a,b,-x)
            rt = abs(b - a);
            // if(isInteger(rt)) {
            // // e.g. a = b + n => M(-n,b,-x) is finite series
            // return seriesExpansion(b-a, b, -x)*exp(x);
            // }
            if (rt < r1) {
                // A&S 13.1.27
                return kummer(b - a, b, -x) * exp(x);
            }
            if (!isInteger(rt) && (r1 > MIN_A_B || ((r2 - 3 > MIN_A_B)) && b < 0)) {
                y = recursiveExpansion(a, b, x);
            } else {
                if (b < 1) {
                    y = fractionApprox(a, b, x);
                } else {
                    y = seriesExpansion(a, b, x);
                }
            }
        }
        return y;
    }

    /**
     * Recursive solutions.
     *
     * @param a
     * @param b
     * @param x
     * @return
     */
    private static double recursiveExpansion(final double a, final double b, final double x) {
        int ia = (int) Math.floor(a);
        int ib = (int) Math.floor(b) - 2;
        // int max_ab = max(abs(ia),abs(ib));
        final int minAB = min(abs(ia), abs(ib));
        double y, ra = a - ia, rb = b - ib;
        if (minAB > MIN_A_B) {
            ia = minAB;
            ib = minAB;
            // check for none equal sign i.e. M(a+n,b-n) or M(a-n,b+n)
            if (a * b < 0) {
                ia = minAB;
                ib = -minAB;
            }
            if (a < 0) {
                ia = -ia;
                ib = -ib;
            }
            ra = a - ia;
            rb = b - ib;
            if (a * b < 0)
                return recurrentAB(ia, ib, ra, rb, x);
            return recurrentAB(ia, ra, rb, x);
        }
        if (abs(ib) >= MIN_A_B || abs(ia) >= MIN_A_B) {
            if (abs(ib) > abs(ia))
                y = recurrentB(ib, a, rb, x);
            else
                y = recurrentA(ia, ra, b, x);
            return y;
        }

        System.err.printf("unhandled a=%f b=%f min=%d %n", a, b, minAB);
        throw new IllegalStateException(format("a=%f b=%f", a, b));
        // if(b<1) {
        // y = fractionApprox(a,b,x);
        // } else {
        // y = seriesExpansion(a,b,x);
        // }
        // return y;
    }

    /**
     * Recurrent (+,0) or (-,0) solution using A&amp;ST 13.4.1, for a &gt; 0.
     *
     * @param n
     * @param a
     * @param b
     * @param x
     * @return
     */
    private static double recurrentA(final int n, final double a, final double b, final double x) {
        double yo, ym, yk = -1, ak = a, bk = 2 * a - b + x;
        ym = kummer(a, b, x);
        if (n > 0) {
            // for positive n (+0) recursion
            yo = kummer(a - 1, b, x);
            for (int k = 1; k <= n; k++) {
                yk = (bk * ym + (b - ak) * yo) / ak;
                ak++;
                bk += 2;
                yo = ym;
                ym = yk;
            }
        } else {
            // for negative n (-0) recursion
            if (x > 0)
                LOGGER.warn(format("%s(%.2f,%.2f;%g) unstable (-0) recursion n=%d", KUMMER, a, b, x, n));
            yo = kummer(a + 1, b, x);
            for (int k = 0; k < -n; k++) {
                // yk = (-(2*a-2*k-b+x)*ym+(a-k)*yo)/(b-a+k);
                yk = -(bk * ym - ak * yo) / (b - ak);
                ak--;
                bk -= 2;
                yo = ym;
                ym = yk;
            }
        }
        return yk;
    }

    /**
     * Recurrent (0,+) or (0,-) solution using A&amp;ST 13.4.2.
     *
     * @param n
     * @param a
     * @param b
     * @param x
     * @return
     */
    private static double recurrentB(final int n, final double a, final double b, final double x) {
        double yo, ym, yk = -1, ak, bk = b;
        ym = kummer(a, b, x);
        // for positive n (0+) recursion
        if (n > 0) {
            LOGGER.warn(format("%s(%.2f,%.2f;%g) unstable (0+) recursion n=%d", KUMMER, a, b, x, n));
            yo = kummer(a, b - 1, x);
            for (int k = 1; k <= n; k++) {
                yk = ((bk - 1 + x) * ym - (bk - 1) * yo) * bk / (x * (bk - a));
                bk++;
                yo = ym;
                ym = yk;
            }
        } else {
            // for negative n (0-) recursion
            yo = kummer(a, b + 1, x);
            for (int k = 1; k <= -n; k++) {
                ak = x * (bk - a) / bk;
                bk--;
                yk = ((x + bk) * ym - ak * yo) / bk;
                yo = ym;
                ym = yk;
            }
        }
        return yk;
    }

    /**
     * Recurrent (+,-) or (-+) solution using A&amp;ST 13.4.7, for a &gt; 0.
     *
     * @param n
     * @param a
     * @param b
     * @param x
     * @return
     */
    private static double recurrentAB(final int n, final int m, final double a, final double b, final double x) {
        double yo, ym, yk = -1, dk, ak, bk;
        ym = kummer(a, b, x);
        if (n > 0) {
            // for (+-) dominant stable recursion
            yo = kummer(a - 1, b + 1, x);
            for (int k = 1; k <= n; k++) {
                dk = (k - b + 1) * (k - b) * (a + k) * (k + a + x - 1);

                ak = -x * (2 * k + a - b) * (2 * k - 1 + a - b) * (k + a + x);

                bk = -k * k * k + (-2 * a + 3 * x + b) * k * k;
                bk += (2 * a * b - a * a + 1 - a - b + 5 * x * x + 6 * a * x - 3 * x) * k + x * x * x;
                bk += x * x * (4 * a - b - 1) + 3 * a * x * (a - 1) + a * (a * b - a - b + 1);
                bk *= (k - b);

                yk = -(bk * ym + ak * yo) / dk;
                yo = ym;
                ym = yk;
            }
        } else {
            // for (-+) minimal unstable recursion
            LOGGER.warn(format("%s(%.2f,%.2f;%g) unstable (-+) recursion n=%d", KUMMER, a, b, x, n));
            yo = kummer(a + 1, b - 1, x);
            for (int k = 1; k <= m; k++) {
                dk = x * (b + 2 * k + 1 - a) * (b + 2 * k - a) * (a + x - k);

                ak = (b + k - 1) * (b + k) * (-a + k) * (a + x - 1 - k);

                bk = k * k * k + (3 * x + b - 2 * a) * k * k;
                bk += (3 * x + b - 6 * a * b + a * a - 5 * x * x + a - 2 * a * b - 1) * k + x * x * x;
                bk += x * x * (4 * a - b - 1) + 3 * a * x * (a - 1) + a * (a * b - a - b + 1);
                bk *= (k + b);

                yk = -(bk * ym + ak * yo) / dk;
                yo = ym;
                ym = yk;
            }
        }
        return yk;
    }

    /**
     * Recurrent (+,+) or (--) solution using A&amp;ST 13.4.7, for a &gt; 0.
     *
     * @param n
     * @param a
     * @param b
     * @param x
     * @return
     */
    private static double recurrentAB(final int n, final double a, final double b, final double x) {
        double yo, ym, yk = -1, ak = a, bk = b;
        ym = kummer(a, b, x);
        if (n > 0) {
            LOGGER.warn(format("%s(%.2f,%.2f;%g) unstable (++) recursion n=%d", KUMMER, a, b, x, n));
            yo = kummer(a - 1, b - 1, x);
            // for positive n (++) recursion
            for (int k = 1; k <= n; k++) {
                yk = ((x - (bk - 1)) * ym + (bk - 1) * yo) * bk / (x * ak);
                bk++;
                ak++;
                yo = ym;
                ym = yk;
            }
        } else {
            // for negative n (--) recursion
            yo = kummer(a + 1, b + 1, x);
            ym *= gamma(1 - b);
            yo *= gamma(1 - b + 1);
            bk = b - x - 1;
            for (int k = -1; k >= n; k--) {
                yk = -bk * ym + x * ak * yo;
                bk--;
                ak--;
                yo = ym;
                ym = yk;
            }
            yk /= gamma(1 - b - n);
        }
        return yk;
    }

    /**
     * Using series expansion of A&amp;ST 13.2.1 but without using
     * the gamma function to calculate the Pochhammer symbols.
     *
     * @param a first parameter
     * @param b second positive parameter
     * @param x the real argument
     * @return M(a, b;x)
     */
    protected static double seriesExpansion(final double a, final double b, final double x) {
        int k = 0;
        double d = 1, yo = 0, ym, yk = 1, ak = a, bk = b;
        do {
            ym = yo;
            yo = yk;
            d *= (ak++) / (bk++) * x / (++k);
            yk += d;
        } while (!hasConverged(yk, yo, PRECISSION, k, MAX_ITERATIONS)
                || !hasConverged(yo, ym, PRECISSION, k, MAX_ITERATIONS));
        // System.err.printf("M(%f,%f; %f) k=%d %n",a,b,x,k);
        return yk;
    }

    /**
     * Calculating the Kummer function using a recurrent fraction.
     *
     * @param a first parameter
     * @param b second positive parameter
     * @param x the real argument
     * @return M(a, b;x)
     */
    protected static double fractionApprox(final double a, final double b, final double x) {
        int k = 0;
        double yo = 0, ym, yk = Double.MAX_VALUE;
        double p, q, g, q0 = 0, p0 = 1, g0 = 1;
        do {
            ym = yo;
            yo = yk;
            p = p0 * (a + k) * x;
            g = g0 * (b + k);
            q = (q0 + p0) * (b + k);
            q *= ++k;
            g *= k;
            yk = (q + p) / g;
            q0 = q;
            p0 = p;
            g0 = g;
        } while (!hasConverged(yk, yo, PRECISSION, k, MAX_ITERATIONS)
                || !hasConverged(yo, ym, PRECISSION, k, MAX_ITERATIONS));
        // System.err.printf("M(%f,%f; %f) k=%d %n",a,b,x,k);
        return yk;
    }

    @Override
    public String toString() {
        return format("%s(%.2f,%.2f;x)", KUMMER, a, b);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Differentiable#getDerivative()
     */
    @Override
    public Function getDerivative() {
        final KummerFunction derivative = new KummerFunction(a + 1, b + 1);
        derivative.scale = scale * a / b;
        return derivative;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Integrable#getAntiderivative()
     */
    @Override
    public Function getAntiderivative() {
        final KummerFunction antiderivative = new KummerFunction(a - 1, b - 1);
        antiderivative.scale = scale * b / a;
        return antiderivative;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.L4MFunction#f(double[])
     */
    @Override
    public double f(final double... x) {
        if (abSetted) {
            return scale * kummer(a, b, x[0]);
        }
        if (x.length != 3) {
            final String msg = KUMMER + "(a,b;x) needs three arguments";
            logger.warn(msg);
            throw new IllegalArgumentException(msg);
        }
        return scale * kummer(x[0], x[1], x[2]);
    }
}
 