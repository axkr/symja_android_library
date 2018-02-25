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
import static de.lab4inf.math.util.Accuracy.hasReachedAccuracy;
import static java.lang.Math.log;
import static java.lang.Math.sqrt;

/**
 * The logarithmic integral function, see A&amp;ST 5.1.3.
 * <pre>
 *           x
 *          /
 * li(x) = / dt /ln(t)     for x>0
 *        /
 *       0
 * </pre>
 *
 * @author nwulff
 * @version $Id: LogarithmicIntegalFunction.java,v 1.7 2014/11/16 21:47:23 nwulff Exp $
 * @since 12.11.2010
 */

public class LogarithmicIntegalFunction extends L4MFunction {
    private static final boolean DEBUG = false;
    private static final String FMT = "li(%.3g)=%.4g needs %d";
    private static final int MAX = 50;
    private static final double EPS = 1.E-14;
    private static final double REPS = 1.E-5;
    private static final double TURN = 5E5;

    public static double li(final double x) {
        if (x <= 1) {
            throw new IllegalArgumentException(String.format("x=%.2f < 1", x));
        }
        if (x < TURN) {
            return liRamanujan(x);
            // return liSmall(x);
        }
        return liLarge(x);
    }

    /**
     * Calculate logarithmic integral function for large x.
     *
     * @param x the argument
     * @return li(x) the prime counting function value
     */
    private static double liLarge(final double x) {
        int k = 1;
        final double lnx = log(x);
        double lnxk = lnx;
        double tmp, li = 1, fac = 1;
        do {
            tmp = li;
            li += fac / lnxk;
            lnxk *= lnx;
            fac *= ++k;
        } while (!hasConverged(li, tmp, REPS, k, MAX));
        li *= x / lnx;
        if (DEBUG) {
            LOGGER.info(String.format(FMT, x, li, k));
        }
        return li;
    }

    /**
     * Using the alternating series of Ramanujan.
     *
     * @param x the argument
     * @return li(x) the prime counting function value
     */
    private static double liRamanujan(final double x) {
        int n = 1;
        double li;
        final double lnx = log(x);
        double ln = lnx;
        final double sqx = sqrt(x), eps = EPS / sqx;
        double sum = 0;
        double tmp, d, ak = 1, fac = 1, p2 = 1;
        for (n = 1; n < MAX; n++) {
            tmp = sum;
            fac *= n;
            d = (ln * ak) / (p2 * fac);
            if ((n & 1) == 1) {
                sum += d;
            } else {
                sum -= d;
                ak += 1. / (n + 1);
            }
            if (hasReachedAccuracy(sum, tmp, eps)) {
                break;
            }
            ln *= lnx;
            p2 *= 2;
        }
        li = EULER + log(lnx) + sqx * sum;
        if (DEBUG) {
            LOGGER.info(String.format(FMT, x, li, n));
        }
        return li;
    }

    /**
     * Calculate logarithmic integral function for small x.
     * @param x the argument
     * @return li(x) the prime counting function value
     */
     /*
      * protected static double liSmall(final double x) { int k = 1; double lnx = log(x),lnxk=lnx; double tmp, li =EULER
      * + log(lnx), fac = 1; do { tmp = li; li += lnxk/(k*fac); lnxk *=lnx; fac *= ++k; }
      * while(!hasReachedAccuracy(li,tmp,EPS) && k<MAX); if(DEBUG) { LOGGER.info(String.format(FMT,x,li,k)); } return li;
      * }
      */

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.Function#f(double[])
     */
    @Override
    public double f(final double... x) {
        return li(x[0]);
    }
}
 