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
import de.lab4inf.math.util.Aitken;

import static java.lang.Math.abs;

/**
 * RootFinder using the regula falsi method. It uses an approximate
 * first derivative as the Newton method using the secant and exchanges
 * the left right borders as the bisection method. So this algorithm
 * will always converge if the guess f(a)*f(b)is less then zero for a
 * continuous function f and is faster than the bisection method.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: RegulaFalsiRootFinder.java,v 1.10 2014/02/11 20:10:22 nwulff Exp $
 * @since 15.01.2009
 */
public class RegulaFalsiRootFinder extends AbstractRootFinder {
    /**
     * Find a root using the regula falsi method.
     *
     * @param fct function to find the root for
     * @param a   left border of the enclosed root
     * @param b   right border of the enclosed root
     * @param eps the accuracy to be reached
     * @return the root
     */
    public static double regulafalsi(final Function fct, final double a,
                                     final double b, final double eps) {
        int n = 0;
        @SuppressWarnings("rawtypes")
        Aitken aitken = new Aitken();
        double xa = a, xb = b, xc, dx;
        double ya = fct.f(xa), yb = fct.f(xb), yc;
        do {
            dx = xb - xa;
            checkEnclosure(xa, xb, ya, yb);
            xc = xa - ya * dx / (yb - ya);
            xc = aitken.next(xc);
            yc = fct.f(xc);
            if (abs(yc) < 10 * EPS) {
                break;
            }
            if (yc * yb < 0) {
                xa = xc;
                ya = yc;
            } else {
                xb = xa;
                yb = ya;
                xa = xc;
                ya = yc;
            }
        } while (!convergence(xa, xb, yc, ++n, eps));
        return xc;
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.roots.AbstractRootFinder#checkGuess(double[])
     */
    @Override
    protected boolean checkGuess(final double... guess) {
        return guess.length >= 2;
    }

    /*
     * (non-Javadoc)
     * @see de.lab4inf.math.roots.AbstractRootFinder#root(de.lab4inf.math.Function, double, double, double)
     */
    @Override
    protected double findroot(final Function f, final double... guess) {
        return regulafalsi(f, guess[0], guess[1], getEpsilon());
    }

}
 