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

package de.lab4inf.math.roots;

import de.lab4inf.math.Function;
import de.lab4inf.math.L4MObject;
import de.lab4inf.math.RootFinder;
import de.lab4inf.math.util.Accuracy;

import static de.lab4inf.math.util.Accuracy.hasReachedAccuracy;
import static de.lab4inf.math.util.Accuracy.round;
import static java.lang.String.format;

/**
 * Abstract base class for a root finder.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: AbstractRootFinder.java,v 1.17 2014/11/18 23:41:21 nwulff Exp $
 * @since 15.01.2009
 */
public abstract class AbstractRootFinder extends L4MObject implements RootFinder {
    protected static final String WRONG_ARGS = "%s needs [left,right] borders";
    protected static final String WRONG_RANGE = "wrong range <%f=[%f|%f]=%f>";
    protected static final String NOT_A_NUMBER = "not a number f(%g) after %d iterations";
    protected static final String NO_CONVERGENCE = "no convergence f(%g)=%6g after %d iterations";
    protected static final String ITERATIONS_FOR_ROOT = "needed %d iterations for root: %f";
    protected static final int MAX_ITERATIONS = 1000;
    protected static final double TINY_DERIVATIVE = 1.E-3;
    protected static final double EPS = Accuracy.DEPS;
    private double eps = EPS;

    /**
     * Signal if convergence has been reached, that is |xn-xo|<error or
     * |f|< DEPS (minimal possible accuracy). The relative accuracy equals
     * the absolute accuracy as long as |xn|<1 otherwise the relative
     * accuracy is taken.
     *
     * @param xn  the actual approximation
     * @param xo  the last approximation
     * @param f   the current function value f(xn)
     * @param n   the iteration counter
     * @param eps the accuracy to reach
     * @return boolean true if converged
     */
    protected static boolean convergence(final double xn, final double xo, final double f, final int n, final double eps) {
        boolean converged = false;
        String msg;
        if (Double.isInfinite(f) || Double.isNaN(f)) {
            msg = format(NOT_A_NUMBER, xn, n);
            getLogger().error(msg);
            throw new ArithmeticException(msg);
        }
        if (n >= MAX_ITERATIONS) {
            msg = format(NO_CONVERGENCE, xn, f, n);
            getLogger().error(msg);
            throw new ArithmeticException(msg);
        }
        converged = hasReachedAccuracy(xn, xo, eps);// || abs(f) < EPS;
        if (converged && getLogger().isInfoEnabled()) {
            msg = format(ITERATIONS_FOR_ROOT, n, round(xn, eps));
            getLogger().info(msg);
        }
        return converged;
    }

    /**
     * Check if the condition f(a)*f(b)<0 is fulfilled. Otherwise
     * there cannot be any real root in the interval [a,b].
     *
     * @param a  the argument of f(a)
     * @param b  the argument of f(b)
     * @param fa the value f(a)
     * @param fb the value f(b)
     */
    protected static void checkEnclosure(final double a, final double b, final double fa, final double fb) {
        if ((fa < 0 && fb < 0) || (fa > 0 && fb > 0)) {
            throw new IllegalArgumentException(format(WRONG_RANGE, fa, a, b, fb));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.roots.RootFinder#getEpsilon()
     */
    @Override
    public double getEpsilon() {
        return eps;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.roots.RootFinder#setEpsilon(double)
     */
    @Override
    public void setEpsilon(final double precision) {
        this.eps = precision;
    }

    protected String getRootFinderName() {
        final String name = getClass().getSimpleName();
        return name;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.roots.RootFinder#root(de.lab4inf.math.Function, double[])
     */
    @Override
    public double root(final Function f, final double... guess) {
        if (guess == null || !checkGuess(guess)) {
            final String msg = format(WRONG_ARGS, getRootFinderName());
            getLogger().info(msg);
            throw new IllegalArgumentException(msg);
        }
        if (guess.length > 2) {
            setEpsilon(guess[2]);
        }
        final double x = findroot(f, guess);
        // logger.info(String.format("found approximate root: %+f ",x));
        if (Math.abs(f.f(x)) > Accuracy.FEPS) {
            final String msg = String.format("found fake pole %.2e at %.5g", f.f(x), x);
            logger.info(msg);
            throw new ArithmeticException(msg);
        }
        return x;
    }

    /**
     * Quick check if the guess values are sufficient, either as
     * left, right borders or as x0 starting point.
     *
     * @param guess the initial values
     * @return boolean flag
     */
    protected abstract boolean checkGuess(final double... guess);

    /**
     * Calculate an approximate root f(x)=0 in the interval [a,b] within
     * the given precision.
     *
     * @param f     the function to choose
     * @param guess starting point or left-right border
     * @return x with f(x)=0
     */
    protected abstract double findroot(final Function f, double... guess);
}
 