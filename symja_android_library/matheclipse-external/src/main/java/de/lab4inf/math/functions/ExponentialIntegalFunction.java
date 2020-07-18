/*
 * Project: Lab4Math
 *
 * Copyright (c) 2006-2010,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for computer sciences (Lab4Inf).
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

import static de.lab4inf.math.Constants.EULER;
import static de.lab4inf.math.util.Accuracy.hasConverged;
import static java.lang.Math.exp;
import static java.lang.Math.log;

/**
 * The exponential integral function Ei(x), see  A&amp;ST 5.1.1.
 * <p>
 * <pre>
 *            x
 *           /
 *  Ei(x) = / dt exp(t)/t     for x>0
 *         /
 *       -&infin;
 * </pre>
 *
 * @author nwulff
 * @version $Id: ExponentialIntegalFunction.java,v 1.9 2015/01/29 15:13:03 nwulff Exp $
 * @since 12.11.2010
 */

public class ExponentialIntegalFunction extends L4MFunction {
    private static final boolean DEBUG = false;
    private static final String FMT = "Ei(%.3g)=%.4g needs %d";
    private static final String FMT1 = "E1(%.3g)=%.4g needs %d";
    private static final double TURN = 40;
    private static final int MAX = 250;
    private static final double EPS = 1.E-14;

    /**
     * Calculate the exponential integral function.
     *
     * @param x argument x &gt; 0
     * @return ei(x)
     */
    public static double ei(final double x) {
        if (x <= 0) {
            throw new IllegalArgumentException(String.format("x=%.2f <=0", x));
        }
        if (x > TURN) {
            return -e1Asym(-x);
        }
        return EULER + log(x) - ein(-x);
    }

    /**
     * Calculate the Ein function x.
     *
     * @param x the argument
     * @return ein(x)
     */
    public static double ein(final double x) {
        int k = 1;
        double xk = x;
        double tmp, ak, ein = 0, fac = 1;
        do {
            tmp = ein;
            ak = xk / (k * fac);
            if ((k & 1) == 1) {
                ein += ak;
            } else {
                ein -= ak;
            }
            xk *= x;
            fac *= ++k;
        } while (!hasConverged(ein, tmp, EPS, k, MAX));
        if (DEBUG) {
            LOGGER.info(String.format(FMT, x, ein, k));
        }
        return ein;
    }

    /**
     * Asymptotic (formally divergent) series of the the E1 function for large x.
     *
     * @param x the argument
     * @return e1(x)
     */
    private static double e1Asym(final double x) {
        int k = 1;
        double xk = -x;
        double tmp, ak, e1 = 1, fac = 1;
        do {
            tmp = e1;
            ak = fac / xk;
            e1 += ak;
            xk *= x;
            fac *= ++k;
        } while (!hasConverged(e1, tmp, EPS, k, MAX));
        e1 *= exp(-x) / x;
        if (DEBUG) {
            LOGGER.info(String.format(FMT1, x, e1, k));
        }
        return e1;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        return ei(x[0]);
    }

}
 