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

package de.lab4inf.math.util;

//import static de.lab4inf.math.Constants.DEBUG;

import de.lab4inf.math.Function;
import de.lab4inf.math.L4MObject;
import de.lab4inf.math.gof.Visitor;

import static de.lab4inf.math.util.Accuracy.hasConverged;
import static java.lang.Math.abs;

/**
 * Generic implementation for a continued fraction (CF) algorithm.
 * Only the actual a[n] and b[n] values have to be provided by subclasses.
 * <pre>
 *
 *  CF = a0 + b1/(a1 + b2/(a2 + b3/(a3 + b4/(a4 + ...
 *
 *            b1  b2  b3
 *  CF - a0 = --- --- --- ...
 *            a1+ a2+ a3+
 * </pre>
 *
 * @author nwulff
 * @version $Id: ContinuedFraction.java,v 1.19 2014/06/26 11:25:35 nwulff Exp $
 * @since 14.02.2009
 */
public abstract class ContinuedFraction extends L4MObject implements Function {
    /**
     * reference to the FMTINFO attribute.
     */
    protected static final String FMTINFO = "ite(%3d)=%f";
    /**
     * reference to the NO_CONVERGENCE attribute.
     */
    protected static final String NO_CONVERGENCE = "Continued fraction diverges";
    protected static final double DEFAULT_EPS = Accuracy.FEPS;
    protected static final int MAX_ITERATIONS = Integer.MAX_VALUE - 4;
    protected static final double TOO_BIG = 1.E12;

    /* (non-Javadoc)
     * @see de.lab4inf.math.gof.Visitable#accept(de.lab4inf.math.gof.Visitor)
     */
    @Override
    public void accept(final Visitor<Function> visitor) {
        visitor.visit(this);
    }

    /**
     * Evaluate the value of the CF at point x[0].
     *
     * @param x argument(s) only first is used
     * @return CF.evaluate(x[0])
     */
    public double f(final double... x) {
        return evaluate(x[0]);
    }

    /**
     * Get the first inital a[0] coefficient, which is often different
     * to the rest.
     *
     * @param x the argument.
     * @return a[0]
     */
    protected abstract double getA0(double x);

    /**
     * Get the actual a[n] coefficient, which might depend on argument x.
     *
     * @param n the index
     * @param x the argument
     * @return a[n]
     */
    protected abstract double getAn(int n, double x);

    /**
     * Get the actual b[n] coefficient, which might depend on argument x.
     *
     * @param n the index
     * @param x the argument
     * @return b[n]
     */
    protected abstract double getBn(int n, double x);

    /**
     * Evaluate the limiting CF at argument x.
     *
     * @param x the argument
     * @return the limiting CF.
     */
    public double evaluate(final double x) {
        return evaluate(x, DEFAULT_EPS, MAX_ITERATIONS);
    }

    /**
     * Evaluate the limiting CF at argument x with precision eps.
     *
     * @param x   the argument
     * @param eps the precision
     * @return the limiting CF.
     */
    public double evaluate(final double x, final double eps) {
        return evaluate(x, eps, MAX_ITERATIONS);
    }

    /**
     * Evaluate the limiting CF at argument x within default precision
     * using not more than maxIterations.
     *
     * @param x          the argument
     * @param iterations maximal number of iterations
     * @return the limiting CF
     */
    public double evaluate(final double x, final int iterations) {
        return evaluate(x, DEFAULT_EPS, iterations);
    }

    /**
     * Evaluate the limiting CF at argument x within epsilon precision
     * using not more than maxIterations.
     *
     * @param x             the argument
     * @param epsilon       precision to reach
     * @param maxIterations to do
     * @return the CF within epsilon precision
     */
    public double evaluate(final double x, final double epsilon,
                           final int maxIterations) {
        int n = 1;
        double a, b, a0 = 1, b0 = 0, b1 = 1, a1 = getA0(x), a2, b2;
        double xo, xn = a1 / b1;

        do {
            xo = xn;
            a = getAn(n, x);
            b = getBn(n, x);
            a2 = a * a1 + b * a0;
            b2 = a * b1 + b * b0;
            if (abs(a2) > TOO_BIG || abs(b2) > TOO_BIG) {
                a1 /= TOO_BIG;
                a2 /= TOO_BIG;
                b1 /= TOO_BIG;
                b2 /= TOO_BIG;
            }
            xn = a2 / b2;
            a0 = a1;
            a1 = a2;
            b0 = b1;
            b1 = b2;
            // if(DEBUG && logger.isInfoEnabled()) {
            // logger.info(format(FMTINFO,n,xn));
            // }
        } while (!hasConverged(xn, xo, epsilon, ++n, maxIterations));
        // if(DEBUG && logger.isInfoEnabled()) {
        // logger.info(format(FMTINFO,n,xn));
        // }
        return xn;
    }
}
 