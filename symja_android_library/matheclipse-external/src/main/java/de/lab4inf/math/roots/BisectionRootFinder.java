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

import static java.lang.Math.abs;
import static java.lang.Math.max;

/**
 * Root finding by simple bisection.
 * The algorithm will always converge if the guess f(a)*f(b)
 * is less then zero for a continuous function f, but it is slow.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: BisectionRootFinder.java,v 1.10 2014/02/11 20:10:22 nwulff Exp $
 * @since 15.01.2009
 */
public class BisectionRootFinder extends AbstractRootFinder {
    /**
     * Find a root using a simple bisection method.
     *
     * @param fct function to find the root for
     * @param a   left border of the enclosed root
     * @param b   right border of the enclosed root
     * @param eps the accuracy to be reached
     * @return the root
     */
    public static double bisection(final Function fct, final double a,
                                   final double b, final double eps) {
        int n = 0;
        double xa = a, xb = b, xc;
        double ya = fct.f(xa), yb = fct.f(xb), yc;
        checkEnclosure(xa, xb, ya, yb);
        do {
            //checkEnclosure(xa, xb, ya, yb);
            xc = (xa + xb) / 2;
            yc = fct.f(xc);
            if (10 * max(abs(ya), abs(b)) <= abs(yc)) {
                throw new ArithmeticException("function values growing " + yc);
            }
            if (Math.abs(yc) < EPS) {
                break;
            }
            if (yc * yb < 0) {
                xa = xc;
                ya = yc;
            } else {
                xb = xc;
                yb = yc;
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
        return bisection(f, guess[0], guess[1], getEpsilon());
    }
}
 