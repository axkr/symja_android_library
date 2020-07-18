/*
 * Project: Lab4Math
 *
 * Copyright (c) 2006-2011,  Prof. Dr. Nikolaus Wulff
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

package de.lab4inf.math.util;

import de.lab4inf.math.L4MObject;
import de.lab4inf.math.NFunction;

import static java.lang.Math.round;

/**
 * Utility class to calculate Binomial coefficients n over k.
 *
 * @author nwulff
 * @version $Id: BinomialCoefficient.java,v 1.3 2012/02/02 15:23:51 nwulff Exp $
 * @since 13.03.2011
 */

public final class BinomialCoefficient extends L4MObject implements NFunction {
    private static final String BINOMINAL_NEEDS_TWO_ARGUMENTS = "binominal needs two arguments";

    /**
     * Calculate the binomial coefficient n over k for positive n,k.
     *
     * @param n first argument
     * @param k second argument
     * @return n over k
     */
    public static long binomial(final int n, final int k) {
        return round(dbinomial(n, k));
    }

    /**
     * Calculate the binomial coefficient n over k for positive n,k
     * using double precision, thus enlarging the available range.
     *
     * @param n first argument
     * @param k second argument
     * @return n over k
     */
    public static double dbinomial(final int n, final int k) {
        int j, kk = k;
        if (n < 0 || n < k || k < 0) {
            return 0;
        }
        if (k == 0 || n == k) {
            return 1;
        }
        if (k > n / 2) {
            kk = n - k; // Use symmetry
        }
        double nk, binomi = 1;
        for (j = 1, nk = n - kk; j <= kk; j++) {
            binomi *= (nk + j) / j;
        }
        return binomi;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.NFunction#f(long[])
     */
    @Override
    public long f(final long... x) {
        if (x.length != 2) {
            throw new IllegalArgumentException(BINOMINAL_NEEDS_TWO_ARGUMENTS);
        }
        return binomial((int) x[0], (int) x[1]);
    }
}
 