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

import java.util.HashMap;

import static de.lab4inf.math.util.BinomialCoefficient.binomial;

/**
 * Utility class to calculate unsigned Stirling numbers of the first
 * and second kind.
 * Note: A&amp;ST 24.1.3 uses signed Stirling numbers.
 * <p>
 * <pre>
 * 1.st:  s(n,k) = s(n-1,k-1) + (n-1)*s(n-1,k)
 * 2.nd:  S(n,k) = S(n-1,k-1) + k*S(n-1,k)
 * </pre>
 *
 * @author nwulff
 * @version $Id: StirlingNumbers.java,v 1.4 2014/11/16 21:47:23 nwulff Exp $
 * @since 25.03.2011
 */

public final class StirlingNumbers {
    private static HashMap<Object, Long> first = new HashMap<Object, Long>();
    private static HashMap<Object, Long> second = new HashMap<Object, Long>();

    /**
     * Hidden constructor for a utility class without instances.
     */
    private StirlingNumbers() {
    }

    /**
     * Calculate an unique key from the tuple (n,k).
     *
     * @param s the String representation.
     * @param n 1.st argument
     * @param k 2.nd argument
     * @return the unique key
     */
    private static Object pk(final String s, final int n, final int k) {
        return String.format("%s(%d,%d)", s, n, k);
    }

    /**
     * Calculate the Stirling number s(n,k) of the first kind.
     *
     * @param n first argument
     * @param k second argument
     * @return s(n, k)
     */
    public static long stirling1st(final int n, final int k) {
        long ret = 0;
        if (n == 0 && k == 0) {
            ret = 1;
        } else if (n <= 0 || k <= 0) {
            ret = 0;
        } else if (n < k) {
            ret = 0;
        } else {
            final Object key = pk("s", n, k);
            if (!first.containsKey(key)) {
                for (int p = k - 1; p < n; p++) {
                    ret += stirling1st(n - 1, p) * binomial(p, k - 1);
                }
                first.put(key, ret);
            } else {
                ret = first.get(key);
            }
        }
        return ret;
    }

    /**
     * Calculate the Stirling number S(n,k) of the second kind.
     *
     * @param n first argument
     * @param k second argument
     * @return S(n, k)
     */

    public static long stirling2nd(final int n, final int k) {
        long ret = 0;
        if (n == 0 && k == 0) {
            ret = 1;
        } else if (n <= 0 || k <= 0) {
            ret = 0;
        } else {
            final Object key = pk("S", n, k);
            if (!second.containsKey(key)) {
                ret = stirling2nd(n - 1, k - 1) + k * stirling2nd(n - 1, k);
                second.put(key, ret);
            } else {
                ret = second.get(key);
            }
        }
        return ret;
    }
}
 