/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2015,  Prof. Dr. Nikolaus Wulff
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

import static de.lab4inf.math.functions.CarlsonIntegral.rd;
import static de.lab4inf.math.functions.CarlsonIntegral.rf;
import static java.lang.Math.sin;

/**
 * Implementation of the function E(&phi;\&alpha;),
 * the incomplete elliptic integral of the second kind, see A&amp;ST 17.2.8.
 * <pre>
 *          &phi;
 *          &#8992;
 * E(&phi;\&alpha;) = &#9134; dt  &radic;(1-sin<sup>2</sup>&alpha; sin<sup>2</sup>t)
 *          &#8993;
 *          0
 *
 * </pre>
 * other definitions use variable k or m instead of &alpha; with
 * m = k<sup>2</sup>=sin<sup>2</sup>&alpha; , x = sin &phi; and the alternative
 * definitions
 * <p>
 * <pre>
 *          x
 *          &#8992;    &radic;(1-m u<sup>2</sup>)
 * E(x|m) = &#9134; du ---------
 *          &#8993;    &radic;(1-u<sup>2</sup>)
 *          0
 *
 *          x
 *          &#8992;    &radic;(1-k<sup>2</sup>u<sup>2</sup>)
 * E(x,k) = &#9134; du ---------
 *          &#8993;    &radic;(1-u<sup>2</sup>)
 *          0
 *
 * </pre>
 * <p>
 * <p>
 * <hr/>
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: IncompleteSecondEllipticIntegral.java,v 1.2 2015/01/28 19:38:42 nwulff Exp $
 * @since 28.01.2015
 */
public class IncompleteSecondEllipticIntegral extends L4MFunction {
    /**
     * Calculate the incomplete elliptic integral E(&phi;\&alpha;) of the second kind.
     *
     * @param phi   the phase angle
     * @param alpha the modular angle
     * @return E(&phi;\&alpha;)
     */
    public static double icseint(final double phi, final double alpha) {
        double x, y, z, e = 0;
        if (phi < 0)
            return -icseint(-phi, alpha);
        else if (phi > 0) {
            // we use only the implementation due to Carlson at present.
            double c = 1. / sin(phi);
            double k2 = sin(alpha);
            c *= c;
            k2 *= k2;
            x = c - 1;
            y = c - k2;
            z = c;
            e = rf(x, y, z) - k2 / 3 * rd(x, y, z);
        }
        return e;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.L4MFunction#f(double[])
     */
    @Override
    public double f(double... x) {
        return icseint(x[0], x[1]);
    }
}
 