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

import de.lab4inf.math.Function;
import de.lab4inf.math.Letters;
import de.lab4inf.math.gof.Visitor;
import de.lab4inf.math.util.ContinuedFraction;

import static de.lab4inf.math.functions.Gamma.gamma;
import static de.lab4inf.math.functions.Gamma.lngamma;
import static de.lab4inf.math.util.Accuracy.hasConverged;
import static java.lang.Math.exp;
import static java.lang.Math.log;

/**
 * Implementation of the incomplete gamma function &Gamma;(a,x),
 * via the regularized gamma functions P(a,x) and Q(a,x).
 * <br/>
 * <b>Note</b>: Very often the function P(a,x) or &gamma;(a,x) are required.
 * <p>
 * <pre>
 *
 *  &Gamma;(a,x) = Q(a,x)*&Gamma;(a) = [1 - P(a,x)]*&Gamma;(a)
 *
 *
 *              &infin;
 *             /
 *  &Gamma;(a,x) =  / dt t<sup>(a-1)</sup>*exp(-t)
 *           /
 *          x
 *
 *
 *                     x
 *             1      /
 *  P(a,x) = -----   / dt t<sup>(a-1)</sup>*exp(-t)  = 1 - Q(a,x)
 *          (a-1)!  /
 *                 0
 *                    &infin;
 *             1     /
 *  Q(a,x) = -----  / dt t<sup>(a-1)</sup>*exp(-t)  = 1 - P(a,x)
 *          (a-1)! /
 *                x
 *
 *
 *  &gamma;(a,x) = &Gamma;(a)*P(a,x) = &Gamma;(a) - &Gamma;(a,x)
 *
 * </pre>
 * The integrals are calculated via series expansions or continued fractions
 * and are check to be accurate within order 1.E-13.
 *
 * @author nwulff
 * @version $Id: IncompleteGamma.java,v 1.25 2015/01/29 15:13:03 nwulff Exp $
 * @since 14.02.2009
 */
public class IncompleteGamma extends L4MFunction {
    /**
     * the greek upper gamma character as unicode.
     */
    public static final char GAMMA = Letters.UPPER_GAMMA;
    private static final double DEFAULT_EPSILON = 1.E-14;
    private static final int MAX_ITERATIONS = 1500;

    /**
     * The incomplete gamma function &Gamma;(a,x).
     *
     * @param a the parameter
     * @param x the value
     * @return &Gamma;(a,x)
     */
    public static double incGammaP(final double a, final double x) {
        return gamma(a) * regGammaQ(a, x);
    }

    /**
     * The incomplete gamma function &gamma;(a,x).
     *
     * @param a the parameter
     * @param x the value
     * @return &gamma;(a,x)
     */
    public static double incGammaQ(final double a, final double x) {
        return gamma(a) * regGammaP(a, x);
    }

    /**
     * The regularized gamma function P(a,x) 6.5.1 A&amp;ST.
     *
     * @param a the parameter
     * @param x the value
     * @return the regularized gamma function P(a,x)
     */
    public static double regGammaP(final double a, final double x) {
        return regGammaP(a, x, DEFAULT_EPSILON, MAX_ITERATIONS);
    }

    /**
     * The regularized gamma function Q(a,x) = 1 - P(a,x).
     *
     * @param a the parameter
     * @param x the value
     * @return the regularized gamma function Q(a,x)
     */
    public static double regGammaQ(final double a, final double x) {
        return regGammaQ(a, x, DEFAULT_EPSILON, MAX_ITERATIONS);
    }

    /**
     * Calculate the regularized gamma function P(a,x), with epsilon precision
     * using maximal max iterations. The algorithm uses series expansion 6.5.29
     * and formula 6.5.4 from A&amp;ST.
     *
     * @param a   the a parameter.
     * @param x   the value.
     * @param eps the desired accuracy
     * @param max maximum number of iterations to complete
     * @return the regularized gamma function P(a,x)
     */
    private static double regGammaP(final double a, final double x, final double eps, final int max) {
        double ret = 0;
        if ((a <= 0.0) || (x < 0.0)) {
            throw new IllegalArgumentException(String.format("P(%f,%f)", a, x));
        }
        if (a >= 1 && x > a) {
            ret = 1.0 - regGammaQ(a, x, eps, max);
        } else if (x > 0) {
            // calculate series expansion A&S 6.5.29
            int n = 1;
            final double ea = exp(-x + (a * log(x)) - lngamma(a));
            final double err = eps;
            double an = 1.0 / a, so, sn = an;
            do {
                so = sn;
                an *= x / (a + n);
                sn += an;
            } while (!hasConverged(sn, so, err, ++n, max));
            // do the transformation 6.5.4
            ret = ea * sn;
        }
        return ret;
    }

    /**
     * Calculate the regularized gamma function Q(a,x) = 1 - P(a,x), with
     * epsilon precision using maximal maxIterations.
     * The algorithm uses a continued fraction until convergence is reached.
     *
     * @param a             the a parameter.
     * @param x             the value.
     * @param epsilon       the desired accuracy
     * @param maxIterations maximum number of iterations to complete
     * @return the regularized gamma function Q(a,x)
     */
    private static double regGammaQ(final double a, final double x, final double epsilon, final int maxIterations) {
        double ret = 0;

        if ((a <= 0.0) || (x < 0.0)) {
            throw new IllegalArgumentException(String.format("Q(%f,%f)", a, x));
        }
        if (x < a || a < 1.0) {
            ret = 1.0 - regGammaP(a, x, epsilon, maxIterations);
        } else if (x > 0) {
            // create continued fraction analog to A&S 6.5.31 / 26.4.10 ?
            // this implementation is due to Wolfram research
            // http://functions.wolfram.com/GammaBetaErf/GammaRegularized/10/0003/
            final double ea = exp(-x + (a * log(x)) - lngamma(a));
            final double err = epsilon;
            final ContinuedFraction cf = new RegularizedGammaFraction(a);
            ret = 1.0 / cf.evaluate(x, err, maxIterations);
            ret *= ea;
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        if (x.length != 2) {
            throw new IllegalArgumentException("Incomplete" + GAMMA + "(a,x) needs two arguments");
        }
        return incGammaP(x[0], x[1]);
    }

    /**
     * Internal helper class for the continued fraction.
     */
    static class RegularizedGammaFraction extends ContinuedFraction {
        private final double a;

        public RegularizedGammaFraction(final double a) {
            this.a = a;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.util.ContinuedFraction#getA0(double)
         */
        @Override
        protected double getA0(final double x) {
            return getAn(0, x);
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.util.ContinuedFraction#getAn(int, double)
         */
        @Override
        protected double getAn(final int n, final double x) {
            return (2.0 * n + 1.0) - a + x;
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.util.ContinuedFraction#getBn(int, double)
         */
        @Override
        protected double getBn(final int n, final double x) {
            return n * (a - n);
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

    ;
}
 