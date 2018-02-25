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

package de.lab4inf.math.functions;

import de.lab4inf.math.Differentiable;
import de.lab4inf.math.Function;

import static de.lab4inf.math.functions.Beta.beta;
import static de.lab4inf.math.util.Accuracy.hasConverged;
import static de.lab4inf.math.util.Accuracy.isInteger;
import static de.lab4inf.math.util.Accuracy.isSimilar;
import static de.lab4inf.math.util.BinomialCoefficient.binomial;
import static java.lang.Math.pow;

/**
 * The regularized incomplete Beta function I(x;a,b) with 0 &le; x &le; 1, see A&amp;ST 26.5.1.
 * <pre>
 *                       x
 *                      /
 *  I<sub>x</sub>(a,b) = 1/&beta;(a,b) / dt t<sup>(a-1)</sup>*(1-t)<sup>(b-1)</sup>
 *                    /
 *                   0
 * </pre>
 * with the properties:
 * <pre>
 *  I(0;a,b) = 0,  I(1;a,b) = 1, I(x;a,b) = 1 - I(1-x;b,a)
 * </pre>
 *
 * @author nwulff
 * @version $Id: IncompleteBeta.java,v 1.30 2014/11/16 21:47:23 nwulff Exp $
 * @since 23.03.2009
 */
public class IncompleteBeta extends L4MFunction implements Differentiable {
    private static final double PRECISSION = 1.E-13;
    private static final int MAX_ITERATIONS = 200;
    private final boolean abSetted;
    private double a, b;

    /**
     * Default constructor.
     */
    public IncompleteBeta() {
        abSetted = false;
    }

    /**
     * Constructor with given a and b values.
     *
     * @param a first positive parameter
     * @param b second positive parameter
     */
    public IncompleteBeta(final double a, final double b) {
        this.a = a;
        this.b = b;
        this.abSetted = true;
    }

    /**
     * Constructor with given a and b values.
     *
     * @param a first positive parameter
     * @param b second positive parameter
     */
    public IncompleteBeta(final Double a, final Double b) {
        this(a.doubleValue(), b.doubleValue());
    }

    /**
     * Internal parameter check for a,b and x.
     *
     * @param x the real argument 0 &le; x &le; 1
     * @param a first positive parameter
     * @param b second positive parameter
     * @throws IllegalArgumentException if parameters are invalid
     */
    private static void checkParameters(final double x, final double a, final double b) {
        if (a < 0) {
            throw new IllegalArgumentException("a not positiv: " + a);
        }
        if (b < 0) {
            throw new IllegalArgumentException("b not positiv: " + b);
        }
        if (x < 0 || x > 1) {
            throw new IllegalArgumentException("x range wrong: " + x);
        }

    }

    /**
     * Calculate the incomplete Beta function.
     *
     * @param x the real argument 0 &le; x &le; 1
     * @param a first positive parameter
     * @param b second positive parameter
     * @return I(x;a, b)
     */
    public static double incBeta(final double x, final double a, final double b) {
        double ret = 0;
        checkParameters(x, a, b);
        if (isSimilar(x, 0)) {
            return 0;
        } else if (isSimilar(x, 1)) {
            return 1;
        }
        final int ia = (int) a;
        final int ib = (int) b;
        if (isInteger(a) || isInteger(b)) {
            if (isInteger(a)) {
                ret = 1 - intBeta(x, ia, b);
            } else {
                ret = intBeta(1 - x, ib, a);
            }
        } else {
            if (x < 0.5) {
                ret = seriesBeta(x, a, b);
            } else {
                ret = 1 - seriesBeta(1 - x, b, a);
            }
        }
        return ret;
    }

    /**
     * Using series expansion for integer a coefficient. A&amp;ST 26.5.6
     *
     * @param x the real argument 0 &le; x &le; 1
     * @param a first positive integer parameter
     * @param b second positive parameter
     * @return I(x;a, b)
     */
    protected static double intBeta(final double x, final int a, final double b) {
        final int ib = (int) b;
        if (isInteger(b) && a > ib) {
            return 1 - intBeta(1 - x, ib, a);
        }
        double ret = 0, eb, y, zk = 1;
        final double z = 1 - x;
        eb = pow(z, b) / beta(a, b);
        for (int k = 0; k < a; k++) {
            y = binomial(a - 1, k) * zk / (b + k);
            zk *= z;
            if ((k & 1) == 1) { // odd
                ret -= y;
            } else { // even
                ret += y;
            }
        }
        ret *= eb;
        return ret;
    }

    /**
     * Using series expansion for real coefficients. A&amp;ST26.5.4
     *
     * @param x the real argument 0 &le; x &le; 1
     * @param a first positive parameter
     * @param b second positive parameter
     * @return I(x;a, b)
     */
    protected static double seriesBeta(final double x, final double a, final double b) {
        int n = 0;
        double xo, xn = 1, xk = x;
        final double eb = pow(x, a) * pow(1 - x, b) / (a * beta(a, b));
        do {
            xo = xn;
            xn += xk * beta(a + 1, n + 1) / beta(a + b, n + 1);
            xk *= x;
        } while (!hasConverged(xn, xo, PRECISSION, ++n, MAX_ITERATIONS));
        // logger.info("needed " + n);
        return xn * eb;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        if (abSetted) {
            return incBeta(x[0], a, b);
        }
        if (x.length != 3) {
            final String msg = "IncompleteBeta(x;a,b) needs three arguments";
            logger.warn(msg);
            throw new IllegalArgumentException(msg);
        }
        return incBeta(x[0], x[1], x[2]);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Differentiable#getDerivative()
     */
    @Override
    public Function getDerivative() {
        if (abSetted) {
            return new IncBetaKernel(a, b);
        }
        throw new IllegalStateException("a,b parameters not set");
    }

    /**
     * Internal class with the kernel as derivative.
     */
    private static class IncBetaKernel extends L4MFunction {
        private final double am, bm;
        private final double reg;

        IncBetaKernel(final double a, final double b) {
            am = a - 1;
            bm = b - 1;
            reg = Beta.beta(a, b);
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Function#f(double[])
         */
        @Override
        public double f(final double... x) {
            final double t = x[0];
            double y = 1 / reg;
            if (am != 0 && t > 0)
                y *= pow(t, am);
            if (bm != 0)
                y *= pow(1 - t, bm);
            // y = pow(t,am)*pow(1-t,bm)/reg;
            return y;
        }
    }
}
 