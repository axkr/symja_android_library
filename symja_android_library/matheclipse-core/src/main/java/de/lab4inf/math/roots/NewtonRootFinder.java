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

import de.lab4inf.math.Differentiable;
import de.lab4inf.math.Function;
import de.lab4inf.math.differentiation.Differentiator;
import de.lab4inf.math.gof.Visitor;

import static java.lang.Math.abs;
import static java.lang.String.format;

/**
 * Root finder using the Newton method. The algorithm
 * includes the damped method to avoid to big step width
 * and identifies multiple roots and uses in this case
 * a modified Newton method.
 * <p>
 * Partially adapter from:
 * <pre>
 * "Formelsammlung zur Numerischen Mathematik", G.Engeln-Muellges et. al.
 * </pre>
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: NewtonRootFinder.java,v 1.18 2014/06/26 11:25:35 nwulff Exp $
 * @since 15.01.2009
 */
public class NewtonRootFinder extends AbstractRootFinder {
    /**
     * Newton is best with analytical derivatives message.
     */
    private static final String NO_DERIVATIVE_NOT_RECOMMENDED =
            "using Newton without a derivative is not recommended";

    public NewtonRootFinder() {
    }

    /**
     * Find a root using the (damped) Newton method. In case
     * of a multiple root switches to the modified Newton method.
     *
     * @param fct differentiable function to find the root for
     * @param x0  the initial starting value
     * @param eps the accuracy to be reached
     * @return the root
     */
    public static double newton(final Differentiable fct, final double x0,
                                final double eps) {
        Function dF = fct.getDerivative();
        return newton(fct, dF, x0, eps);
    }

    /**
     * Find a root using the (damped) Newton method. In case
     * of a multiple root switches to the modified Newton method.
     *
     * @param fct function to find the root of
     * @param dF  first derivative of the function
     * @param x0  the initial starting value
     * @param eps the accuracy to be reached
     * @return the root
     */
    public static double newton(final Function fct, final Function dF,
                                final double x0, final double eps) {
        int n = 0;
        double dx, xn = x0, xm, f, fn, df;
        f = fct.f(xn);
        do {
            xm = xn;
            df = dF.f(xm);
            // check for multiple root
            if (abs(df) < TINY_DERIVATIVE) {
                getLogger().info(format("found multiple root at x=%g", xm));
                return modnewton(fct, dF, xn, eps);
            }
            dx = f / df;
            xn = xm - dx;
            fn = fct.f(xn);
            // simple damping factor adjustment to avoid overshoot down hill.
            // Formula adapted from G.E-Muellges: 6.5
            while (abs(fn) > abs(f) && abs(dx) > eps) {
                dx /= 2;
                xn = xm - dx;
                fn = fct.f(xn);
            }
            f = fn;
        } while (!convergence(xn, xm, f, ++n, eps));
        getLogger().info(format(ITERATIONS_FOR_ROOT, n, xn));
        return xn;
    }

    /**
     * Modified Newton method for multiple roots.
     *
     * @param fct the function to find the root of
     * @param dF  the first derivative of the function
     * @param x0  the initial guess as starting value
     * @param eps the accuracy to reach
     * @return the root
     */
    public static double modnewton(final Function fct, final Function dF,
                                   final double x0, final double eps) {
        Function d2F;
        if (dF instanceof Differentiable) {
            d2F = ((Differentiable) dF).getDerivative();
        } else {
            getLogger().warn(NO_DERIVATIVE_NOT_RECOMMENDED);
            d2F = new Differentiator(dF);
        }
        return modnewton(fct, dF, d2F, x0, eps);
    }

    /**
     * Modified Newton method for multiple roots.
     *
     * @param fct the function to find the root of
     * @param dF  the first derivative of the function
     * @param d2F the second derivative of the function
     * @param x0  the initial guess as starting value
     * @param eps the accuracy to reach
     * @return the root
     */
    public static double modnewton(final Function fct, final Function dF,
                                   final Function d2F, final double x0, final double eps) {
        int n = 0;
        double dx, xn = x0, xm, f, fn, df, d2f, s = 1, a = 1;
        f = fct.f(xn);
        do {
            xm = xn;
            df = dF.f(xm);
            if (abs(df) < 10 * EPS) {
                if (abs(f) < s * EPS) {
                    break;
                }
                throw new ArithmeticException("derivative is zero");
            }
            d2f = d2F.f(xm);
            // calculate the multiplicity of the root
            // Formula adapted from G.E-Muellges: 2.20
            if (abs(df) < TINY_DERIVATIVE) {
                a = 1. / (1 - f * d2f / (df * df));
                getLogger().info("approximate root multiplicity: " + a);
                if (abs(a) < TINY_DERIVATIVE) {
                    getLogger().warn("multiplicity tends to zero: " + a);
                } else {
                    s = Math.pow(10, a);
                }
            }
            dx = a * f / df;
            xn = xm - dx;
            fn = fct.f(xn);
            // simple damping factor adjustment to avoid overshoot down hill.
            // Formula adapted from G.E-Muellges: 6.5
            while (abs(fn) > abs(f) && abs(dx) > eps) {
                dx /= 2;
                xn = xm - dx;
                fn = fct.f(xn);
            }
            f = fct.f(xn);
        } while (!convergence(xn, xm, f, ++n, eps));
        getLogger().info(format(ITERATIONS_FOR_ROOT, n, xn));
        return xn;
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.roots.AbstractRootFinder#checkGuess(double[])
     */
    @Override
    protected boolean checkGuess(final double... guess) {
        return guess.length >= 1;
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.roots.AbstractRootFinder#root(de.lab4inf.math.Function, double, double, double)
     */
    @Override
    protected double findroot(final Function f, final double... guess) {
        Differentiable d = null;
        if (f instanceof Differentiable) {
            d = (Differentiable) f;
        } else {
            getLogger().warn(NO_DERIVATIVE_NOT_RECOMMENDED);
            d = new FunctionWrapper(f);
        }
        double x0 = guess[0];
        if (guess.length >= 2) {
            x0 = BisectionRootFinder.bisection(f, x0, guess[1], 0.005);
        }
        return newton(d, x0, getEpsilon());
    }

    private static class FunctionWrapper implements Differentiable {
        final Function delegate;
        final Differentiator diff;

        FunctionWrapper(final Function toWrap) {
            delegate = toWrap;
            diff = new Differentiator(delegate);
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.Differentiable#getDerivative()
         */
        public Function getDerivative() {
            return diff;
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.Function#f(double[])
         */
        public double f(final double... x) {
            return delegate.f(x);
        }

        /* (non-Javadoc)
         * @see de.lab4inf.math.gof.Visitable#accept(de.lab4inf.math.gof.Visitor)
         */
        @Override
        public void accept(final Visitor<Function> visitor) {
            visitor.visit(this);
        }
    }

}
 