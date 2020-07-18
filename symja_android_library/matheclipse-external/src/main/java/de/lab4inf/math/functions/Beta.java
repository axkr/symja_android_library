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

import de.lab4inf.math.Letters;

import static de.lab4inf.math.functions.Gamma.lngamma;
import static de.lab4inf.math.util.Accuracy.isInteger;
import static java.lang.Math.exp;

/**
 * Approximation of the Beta function &beta;(x,y) calculated via the log Gamma function.
 * <pre>
 *            1
 *           /
 * &beta;(x,y) = /dt t<sup>(x-1)</sup>*(1-t)<sup>(y-1)</sup>  with x,y&gt;0
 *         /
 *        0
 *
 * &beta;(x,y) = &Gamma;(x)*&Gamma;(y)/&Gamma;(x+y)
 *
 * </pre>
 * <p>
 * The accuracy is driven by the accuracy of the gamma implementation.
 *
 * @author Prof. Dr. Nikolaus Wulff
 * @version $Id: Beta.java,v 1.18 2015/01/29 15:13:03 nwulff Exp $
 * @since 10.01.2009
 */
public class Beta extends L4MFunction {
    /**
     * the greek beta character as unicode.
     */
    public static final char BETA = Letters.BETA;

    /**
     * Calculates the beta function.
     * <pre>
     *  beta(x,y) = gamma(x)*gamma(y)/gamma(x+y)
     * </pre>
     *
     * @param x first argument x &gt;0
     * @param y second argument y &gt; 0
     * @return beta(x, y)
     */
    public static double beta(final double x, final double y) {
        double ret = 0;
        if (x <= 0) {
            throw new IllegalArgumentException(BETA + "-fct x not positiv");
        }
        if (y <= 0) {
            throw new IllegalArgumentException(BETA + "-fct y not positiv");
        }
        final int ix = (int) x;
        final int iy = (int) y;
        if (isInteger(x) && isInteger(y)) {
            if (ix > iy) {
                ret = intBeta(ix, iy);
            } else {
                ret = intBeta(iy, ix);
            }
        } else if (isInteger(x)) {
            ret = intBeta(y, ix);
        } else if (isInteger(y)) {
            ret = intBeta(x, iy);
        } else {
            ret = exp(lngamma(x) + lngamma(y) - lngamma(x + y));
        }
        return ret;
    }

    /**
     * Calculates the beta function for at least one integer argument.
     *
     * @param x first argument x &gt;0
     * @param y second argument y &gt; 0
     * @return beta(x, y)
     */
    private static double intBeta(final double x, final int y) {
        double beta = 1;
        for (int k = 1; k < y; k++) {
            beta *= k / (x + k);
        }
        beta /= x;
        return beta;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        if (x.length != 2) {
            throw new IllegalArgumentException(BETA + "(x,y) needs two arguments");
        }
        return beta(x[0], x[1]);
    }

}
 