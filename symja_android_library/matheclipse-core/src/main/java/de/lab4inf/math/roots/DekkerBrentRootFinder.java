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
import de.lab4inf.math.util.Accuracy;

import static java.lang.Math.abs;
import static java.lang.Math.min;

/**
 * Root finder using the Dekker-Brent-method.
 * <pre>
 * An algorithm with guaranteed convergence for finding a zero of a function
 * R.P.Brent (1971), The Computer Journal, Vol. 14 4, pp.422-425
 * </pre>
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: DekkerBrentRootFinder.java,v 1.14 2014/02/11 20:25:04 nwulff Exp $
 * @since 16.01.2009
 */
public class DekkerBrentRootFinder extends AbstractRootFinder {
    /**
     * Find a root using the Dekker-Brent method.
     *
     * @param fct function to find the root for
     * @param a0  left border of the enclosed root
     * @param b0  right border of the enclosed root
     * @param t   the accuracy to be reached
     * @return the root
     */
    public static double dekkerbrent(final Function fct, final double a0,
                                     final double b0, final double t) {
        int n = 0;
        double m, tol, p, q, r, s, a = a0, b = b0, c = b0, d = b0 - a0, e = d, fa = fct.f(a), fb = fct.f(b), fc = fb;
        checkEnclosure(a, b, fa, fb);
        do {
            // adjust left and right borders of the interval
            if (fb * fc > 0) {
                c = a;
                fc = fa;
                e = b - a;
                d = e;
            }

            if (abs(fc) < abs(fb)) {
                a = b;
                b = c;
                c = a;
                fa = fb;
                fb = fc;
                fc = fa;
            }
            // adjust the tolerance criteria
            tol = 2 * EPS * abs(b) + t;
            m = (c - b) / 2;
            // see if a bisection is forced
            if (abs(e) < tol || abs(fa) <= abs(fb)) {
                d = m;
                e = m;
            } else {
                // try inverse quadratic interpolation
                s = fb / fa;
                if (Accuracy.isSimilar(a, c)) { // linear interpolation
                    p = 2 * m * s;
                    q = 1 - s;
                } else { // inverse quadratic interpolation
                    q = fa / fc;
                    r = fb / fc;
                    p = s * (2 * m * q * (q - r) - (b - a) * (r - 1));
                    q = (q - 1) * (r - 1) * (s - 1);
                }
                // check if in bounds
                if (p > 0) {
                    q = -q;
                } else {
                    p = -p;
                }
                s = e;
                e = d;
                if (2 * p < min(3 * m * q - abs(tol * q), abs(s * q))) {
                    d = p / q;
                } else {
                    d = m;
                    e = m;
                }
            }
            a = b;
            fa = fb;
            //            if (abs(d)<tol) {
            //                d = tol*signum(m);
            //            }
            b += d;
            fb = fct.f(b);
        } while (!convergence(b, a, fb, ++n, tol));
        return b;
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
        return dekkerbrent(f, guess[0], guess[1], getEpsilon());
    }
}
 