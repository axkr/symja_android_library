/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2011,  Prof. Dr. Nikolaus Wulff
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

import java.math.BigDecimal;

/**
 * Utility class to calculate the Catalan numbers.
 * <pre>
 *   C(n+1) = (4n+2)*C(n)/(n+2)
 * </pre>
 * <br/>
 * <p>
 * Note: This simple implementation can only calculate the first 35 numbers,
 * as C(35)= 3116285494907301262 the calculus will fail for higher arguments.
 * <br/><hr/>
 *
 * @author nwulff
 * @version $Id: CatalanNumbers.java,v 1.6 2014/11/16 21:47:23 nwulff Exp $
 * @since 03.10.2011
 */
public final class CatalanNumbers {
    private static final int MAX = 36;
    private static final long[] CN = new long[MAX];

    /**
     * Hidden constructor for utility class.
     */
    private CatalanNumbers() {
    }

    /**
     * Calculate the n-th Catalan number.
     *
     * @param n the argument
     * @return C(n)
     */
    public static long catalan(final int n) {
        if (n >= MAX) {
            throw new IllegalArgumentException("n too large");
        }
        if (n < 1) {
            return 1L;
        } else if (CN[n] > 0) {
            return CN[n];
        }
        long cn = 0;
        if (n <= 33) {
            cn = ((4L * n - 2L) * catalan(n - 1)) / (n + 1L);
        } else {
            cn = cn(n).longValue();
        }
        CN[n] = cn;
        return cn;
    }

    /**
     * Calculate catalan numbers for large n.
     *
     * @param n
     * @return Rational catalan number
     */
    private static BigDecimal cn(final int n) {
        if (n < 1) {
            return BigDecimal.ONE;
        } else if (CN[n] > 0) {
            return new BigDecimal(CN[n]);
        }
        BigDecimal rcn = cn(n - 1);
        rcn = rcn.multiply(BigDecimal.valueOf(4L * n - 2L)).divide(BigDecimal.valueOf(n + 1L));
        final long cn = rcn.longValue();
        CN[n] = cn;
        return rcn;
    }

}
 