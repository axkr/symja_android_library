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

import de.lab4inf.math.util.Accuracy;

import static java.lang.Math.PI;
import static java.lang.Math.atan;
import static java.lang.Math.cos;
import static java.lang.Math.sqrt;
import static java.lang.Math.tan;

/**
 * Implementation of the function F(&phi;\&alpha;),
 * the incomplete elliptic integral of the first kind, see A&amp;ST 17.2.6.
 * <pre>
 *          &phi;
 *          &#8992;          1
 * F(&phi;\&alpha;) = &#9134; dt ----------------
 *          &#8993;    &radic;(1-sin<sup>2</sup>&alpha; sin<sup>2</sup>t)
 *          0
 *
 * </pre>
 * other definitions use variable k or m instead of &alpha; with
 * m = k<sup>2</sup>=sin<sup>2</sup>&alpha; , x = sin &phi; and the alternative
 * definitions
 * <p>
 * <pre>
 *          x
 *          &#8992;          1
 * F(x|m) = &#9134; du ----------------
 *          &#8993;    &radic;(1-m u<sup>2</sup>)&radic;(1-u<sup>2</sup>)
 *          0
 *
 *          x
 *          &#8992;          1
 * F(x,k) = &#9134; du ----------------
 *          &#8993;    &radic;(1-k<sup>2</sup>u<sup>2</sup>)&radic;(1-u<sup>2</sup>)
 *          0
 *
 * </pre>
 * <p>
 * <p>
 * <hr/>
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: IncompleteFirstEllipticIntegral.java,v 1.18 2015/01/28 14:46:12 nwulff Exp $
 * @since 20.05.2006
 */
public class IncompleteFirstEllipticIntegral extends L4MFunction {
    private static final double PIH = PI / 2.0;
    private static final double ONE = 1.0 - Accuracy.DEPS * 10;

    /**
     * Calculate the incomplete elliptic integral F(&phi;\&alpha;) of the first kind.
     *
     * @param phi   the phase angle
     * @param alpha the modular angle
     * @return F(&phi;\&alpha;)
     */
    public static double icfeint(final double phi, final double alpha) {
        double p, pp, a, b, c, t;
        if (phi < 0) {
            return -icfeint(-phi, alpha);
        }

        a = 1.0;
        p = phi;
        c = cos(alpha);
        t = tan(p);
        pp = p;

        while (c < ONE) {
            b = 1 + c;
            a = a * b;
            t = b * t / (1 - c * t * t);
            c = 2 * sqrt(c) / b;
            p = atan(t);
            if (p <= pp) {
                final int k = (int) (pp / PIH + 0.5);
                p += k * PI;
            }
            pp = p;
        }
        p = p / a;
        return p;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        return icfeint(x[0], x[1]);
    }

}
 