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

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Root finder using secant method.
 * <p>
 * This algorithm is faster than the bisection method but might
 * fail if the calculated secant will fall out of the bounding interval.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: SecantRootFinder.java,v 1.9 2014/02/11 20:25:04 nwulff Exp $
 * @since 15.01.2009
 */
public class SecantRootFinder extends AbstractRootFinder {
    /**
     * Check that the  approximate secant root stays within the initial interval.
     *
     * @param a the left border
     * @param x the actual value to check
     * @param b the right border
     */
    private static void checkBracket(final double a, final double x,
                                     final double b) {
        if (x < min(a, b) || x > max(a, b)) {
            String msg = String.format("secant %f leaves [%f,%f]", x, a, b);
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Find a root using the secant method.
     *
     * @param fct function to find the root for
     * @param a   left border of the enclosed root
     * @param b   right border of the enclosed root
     * @param eps the accuracy to be reached
     * @return the root
     */
    public static double secant(final Function fct, final double a,
                                final double b, final double eps) {
        int n = 0;
        double xa = a, xb = b, xc, dx;
        double ya = fct.f(xa), yb = fct.f(xb), yc;
        checkEnclosure(xa, xb, ya, yb);
        dx = xb - xa;
        do {
            /**
             *  Sub optimal as rounding errors will increase:
             *  xc = (xa*yb - ya*xb)/(yb-ya);
             *  DON'T DO SO!
             */
            xc = xa - ya * dx / (yb - ya);
            yc = fct.f(xc);
            xb = xa;
            xa = xc;
            yb = ya;
            ya = yc;
            dx = xb - xa;
            checkBracket(a, xa, b);
        } while (!convergence(xb, xa, yc, ++n, eps));
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
        return secant(f, guess[0], guess[1], getEpsilon());
    }
}
 