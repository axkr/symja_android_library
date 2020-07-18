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
package de.lab4inf.math.functions;

import de.lab4inf.math.Differentiable;
import de.lab4inf.math.Function;

import static de.lab4inf.math.util.Strings.toLowerScript;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 * Function wrapper implementation for the integer Bessel functions J<sub>n</sub>(x)
 * of integer order n with one real argument x.
 * <pre>
 *             1
 *            /
 *   J<sub>n</sub>(x) = / dt  cos(x*sin(&pi;*t)- n*&pi;*t)
 *          /
 *         0
 * </pre>
 * The recursive implementation is based on Hankel's expansion
 * <pre>
 *  J[0/1](x) = P[0/1](x)*cos(T(x)-Q[0/1](x)*sin(T(x))
 * </pre>
 * and uses a forward/backward recursion to calculate the remaining j<sub>n</sub>(x).
 * <p>
 * The implementation of j0 and j1 is tested to be accurate to 5.E-9.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: Bessel.java,v 1.28 2015/01/29 08:42:53 nwulff Exp $
 * @since 19.09.2006
 */
public class Bessel extends L4MFunction implements Differentiable {
    private static final double MMAX = 80.0;
    private static final double SCALE = 1.E10;
    /**
     * p coefficients for j0 for |x|<8.
     */
    private static final double[] RP0 = {57568490574.0, -13362590354.0, 651619640.7, -11214424.18, 77392.33017,
            -184.9052456};
    /**
     * q coefficients for j01 for |x|<8.
     */
    private static final double[] RQ0 = {57568490411.0, 1029532985.0, 9494680.718, 59272.64853, 267.8532712, 1.0};
    /**
     * p coefficients for j01 for 8<|x|.
     */
    private static final double[] FP0 = {1., -.1098628627e-2, .2734510407e-4, -.2073370639e-5, .2093887211e-6};
    /**
     * q coefficients for j0 for 8<|x|.
     */
    private static final double[] FQ0 = {-.1562499995e-1, .143048865e-3, -.6911147651e-5, .7621095161e-6,
            -.934945152e-7};
    /**
     * p coefficients for j1 for |x|<8.
     */
    private static final double[] RP1 = {72362614232.0, -7895059235.0, 242396853.1, -2972611.439, 15704.48260,
            -30.16036606};
    /**
     * q coefficients for j1 for |x|<8.
     */
    private static final double[] RQ1 = {144725228442.0, 2300535178.0, 18583304.74, 99447.43394, 376.9991397, 1.0};
    /**
     * p coefficients for j1 for 8<|x|.
     */
    private static final double[] FP1 = {1, .183105e-2, -.35163964e-4, .2457520174e-5, -.240337019e-6};
    /**
     * q coefficients for j1 for 8<|x|.
     */
    private static final double[] FQ1 = {.04687499995, -.200269087e-3, .8449199096e-5, -.88228987e-6, .105787412e-6};
    /**
     * Pi/2 .
     */
    private static final double PIHALF = PI / 2;
    /**
     * j0 angle shift.
     */
    private static final double J0_SHIFT = PI * ((2 * FP0.length + 1.) / 4 - 2.5);
    /**
     * j1 angle shift.
     */
    private static final double J1_SHIFT = PI * ((2 * FP1.length + 1.) / 4 - 2);
    /**
     * order of the Bessel function.
     */
    protected final int n;

    /**
     * Constructor for the Bessel function of order n.
     *
     * @param n the order for j_n(x)
     */
    public Bessel(final int n) {
        this.n = n;
    }

    /**
     * Constructor for the Bessel function of order n.
     *
     * @param n the order for j_n(x)
     */
    public Bessel(final Integer n) {
        this(n.intValue());
    }

    /**
     * Common polynomial expansion for helper function of J0 and J1.
     *
     * @param x the argument
     * @param c the polynom coefficients.
     * @return
     */
    private static double p(final double x, final double[] c) {
        double y = 0, z2 = x * x;
        for (int i = c.length - 1; i > 0; i--) {
            y = (y + c[i]) * z2;
        }
        y += c[0];
        return y;
    }

    /**
     * Calculate the zero Bessel function J0(x).
     *
     * @param x the real argument.
     * @return J0(x)
     */
    public static double j0(final double x) {
        double t, z, j = 1, p = 0, q = 0;
        if (x < 0) {
            return j0(-x);
        }
        if (x == 0)
            return 1;
        if (x <= 8.0) {
            j = p(x, RP0) / p(x, RQ0);
        } else {
            z = 8 / x;
            t = x - J0_SHIFT;
            p = p(z, FP0);
            q = z * p(z, FQ0);
            j = (p * cos(t) - q * sin(t)) / sqrt(PIHALF * x);
        }
        return j;
    }

    /**
     * Calculate the first Bessel function J1(x).
     *
     * @param x the real argument.
     * @return J1(x)
     */
    public static double j1(final double x) {
        double t, z, j = 0, p = 0, q = 0;
        if (x < 0) {
            return -j1(-x);
        }
        if (x <= 8.0) {
            j = x * p(x, RP1) / p(x, RQ1);
        } else {
            z = 8 / x;
            t = x - J1_SHIFT;
            p = p(z, FP1);
            q = z * p(z, FQ1);
            j = (p * cos(t) - q * sin(t)) / sqrt(PIHALF * x);
        }
        return j;
    }

    /**
     * Calculate the Bessel function Jn(x) of order n.
     *
     * @param n the order
     * @param x the real argument.
     * @return Jn(x)
     */
    public static double jn(final int n, final double x) {
        int sign = 1;
        double jn = 0;
        // check if |n| is odd
        if ((n & 1) == 1) {
            sign = -1;
        }
        if (n < 0) {
            return sign * jn(-n, x);
        }
        if (x < 0) {
            return sign * jn(n, -x);
        }
        if (n == 0) {
            jn = j0(x);
        } else if (n == 1) {
            jn = j1(x);
        } else {
            if (x > n) {
                jn = jnhigh(n, x);
            } else {
                jn = jnlow(n, x);
            }
        }
        return jn;

    }

    /**
     * Calculation for x<n via downwards recurrence relation 9.1.27 A&S.
     *
     * @param n order of bessel function
     * @param x the real argument x>n
     * @return j(n, x)
     */
    private static double jnlow(final int n, final double x) {
        boolean add = false;
        double js = 1, jn = 0, jm = 0, jp = 0, sum = 0, twobyx = 2. / x;
        if (x == 0) {
            return 0;
        }
        int m = 2 * ((n + (int) sqrt(MMAX * n)) / 2);
        for (int j = m; j > 0; j--) {
            jm = j * twobyx * js - jp;
            jp = js;
            js = jm;
            if (abs(js) > SCALE) {
                js /= SCALE;
                jp /= SCALE;
                jn /= SCALE;
                sum /= SCALE;
            }
            if (add) {
                sum += js;
            }
            add = !add;
            if (j == n) {
                jn = jp;
            }
        }
        sum = 2 * sum - js;
        jn /= sum;
        return jn;

    }

    /**
     * Calculation for x>n via upwards recurrence relation 9.1.27 A&S.
     *
     * @param n order of bessel function
     * @param x the real argument x>n
     * @return j(n, x)
     */
    private static double jnhigh(final int n, final double x) {
        double twobyx = 2. / x, jp, jn = j1(x), jm = j0(x);
        for (int j = 1; j < n; j++) {
            jp = j * twobyx * jn - jm;
            jm = jn;
            jn = jp;
        }
        return jn;
    }

    /**
     * Calculate the derivative of function Bessel
     * function J(n, x).
     * <pre>
     * J'(n,x) = J(n-1,x) - n*J(n,x)/x
     * </pre>
     *
     * @param n the order
     * @param x the argument
     * @return the derivative J'(n,x)
     */
    public static double dJn(final int n, final double... x) {
        double z = x[0];
        if (n == 0) {
            return -j1(z);
        }
        return jn(n - 1, z) - n * jn(n, z) / z;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        return jn(n, x[0]);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Differentiable#getDerivative()
     */
    public Function getDerivative() {
        return new BesselDerivative();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.L4MObject#toString()
     */
    @Override
    public String toString() {
        return String.format("j%s", toLowerScript(n));
    }

    /**
     * The derivative of a Bessel function.
     * <pre>
     * J'(n,x) = J(n-1,x) - n*J(n,x)/x
     * </pre>
     */
    class BesselDerivative extends L4MFunction {
        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Function#f(double[])
         */
        @Override
        public double f(final double... x) {
            return dJn(n, x[0]);
        }
    }
}
 