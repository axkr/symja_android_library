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

import static de.lab4inf.math.functions.CarlsonIntegral.rf;
import static de.lab4inf.math.functions.CarlsonIntegral.rj;
import static java.lang.Math.sin;

/**
 * Implementation of the function &Pi;(n; &phi;\&alpha;),
 * the incomplete elliptic integral of the third kind, see A&amp;ST 17.2.14.
 * <pre>
 *            &phi;
 *            &#8992;
 * &Pi;(n;&phi;\&alpha;) = |dt (1-n sin<sup>2</sup>t)<sup>-1</sup>  (1-sin<sup>2</sup>&alpha; sin<sup>2</sup>t)<sup>-1/2</sup>
 *            &#8993;
 *            0
 *
 * </pre>
 * other definitions (A&amp;ST 17.2.15) use variable k or m instead of &alpha; with
 * m = k<sup>2</sup>=sin<sup>2</sup>&alpha; , x = sin &phi; and the alternative
 * definition
 * <p>
 * <pre>
 *            x
 *            &#8992;
 * &Pi;(n;x|m) = |du (1-n u<sup>2</sup>)<sup>-1</sup>[(1-m u<sup>2</sup>)(1-u<sup>2</sup>)]<sup>-1/2</sup>
 *            &#8993;
 *            0
 *
 * </pre>
 * <p>
 * <p>
 * <hr/>
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: IncompleteThirdEllipticIntegral.java,v 1.4 2015/01/29 20:45:00 nwulff Exp $
 * @since 29.01.2015
 */
public class IncompleteThirdEllipticIntegral extends L4MFunction {
    /**
     * Calculate the incomplete elliptic integral &Pi;(n;&phi;\&alpha;) of the 3<sup>rd</sup> kind.
     *
     * @param n     elliptic characteristic
     * @param phi   the phase angle
     * @param alpha the modular angle
     * @return &Pi;(n; &phi;\&alpha;)
     */
    public static double icteint(final double n, final double phi, final double alpha) {
        double x, y, z, t = 0;
        if (phi < 0)
            return -icteint(n, -phi, alpha);
        else if (phi > 0) {
            // we use only the implementation due to Carlson at present.
            double c = 1. / sin(phi);
            double k2 = sin(alpha);
            c *= c;
            k2 *= k2;
            x = c - 1;
            y = c - k2;
            z = c;
            // Carlson has opposite sign for n in his diffinition of &Pi;(...) (61)
            // using +n instead of -n compared to A&ST
            t = rf(x, y, z) + n / 3 * rj(x, y, z, z - n);

            // if (n!=0) {
            // t += n/3*rj(x, y, z, z-n);
            // }
        }
        return t;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.functions.L4MFunction#f(double[])
     */
    @Override
    public double f(double... x) {
        return icteint(x[0], x[1], x[2]);
    }
}
 