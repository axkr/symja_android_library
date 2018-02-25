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

/**
 * Complete elliptic integral of the third kind &Pi;(n,m), see A&ST 17.7.2.
 * <pre>
 *          &pi;/2
 *          &#8992;
 * &Pi;(n,m) = |dt (1-n sin<sup>2</sup>t)<sup>-1</sup> (1-m sin<sup>2</sup>t)<sup>-1/2</sup>
 *          &#8993;
 *          0
 * </pre>
 * other definitions use variables k or &alpha; instead of m, with
 * m = k<sup>2</sup>=sin<sup>2</sup>&alpha;.
 * &Pi;(n,m) is related to the third incomplete elliptic integral &Pi;(n;&phi;\&alpha;) via:
 * <pre>
 * s
 * &Pi;(n,m) = &Pi;(n;1|m) = &Pi;(n;1,k) = &Pi;(n;&pi;/2\&alpha;)
 *
 * </pre>
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: CompleteThirdEllipticIntegral.java,v 1.5 2015/01/29 20:46:01 nwulff Exp $
 * @see <a href="IncompleteThirdEllipticIntegral.html">IncompleteThirdEllipticIntegral</a>
 * @since 29.01.2015
 */
public class CompleteThirdEllipticIntegral extends L4MFunction {

    /**
     * Calculate the third elliptic integral using Carlson's integrals.
     *
     * @param n the real argument |n| less one
     * @param m the real argument |m| less one
     * @return &Pi;(n,m)
     */
    public static double cteint(final double n, final double m) {
        double t = 0;
        // Carlson has a different defintion as A&ST, thus the sign of n
        // has to be changed in formulae (61)
        t = rf(0, 1 - m, 1) + n / 3 * rj(0, 1 - m, 1, 1 - n);
        return t;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        return cteint(x[0], x[1]);
    }

}
 