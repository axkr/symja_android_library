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

import de.lab4inf.math.L4MObject;
import de.lab4inf.math.NFunction;

import static java.lang.Math.abs;

/**
 * The greatest common divisor GDC as natural function.
 *
 * @author nwulff
 * @version $Id: GCD.java,v 1.3 2012/08/22 09:47:59 nwulff Exp $
 * @since 15.10.2011
 */
public class GCD extends L4MObject implements NFunction {
    private static final String GCD_NEEDS_ARGUMENTS = "gcd needs arguments";

    /**
     * Calculate the greatest common divisor.
     *
     * @param a first argument
     * @param b second argument
     * @return gcd(a, b)
     */
    public static long gcd(final long a, final long b) {
        long r, m = abs(a), n = abs(b);
        if (m < n) {
            r = m;
            m = n;
            n = r;
        }
        if (n == 0) return m;
        do {
            r = m % n;
            m = n;
            n = r;
        } while (r > 0);
        return m;
    }

    /**
     * Recursive calculate the greatest common divisor for multiple arguments.
     *
     * @param a argument array
     * @return gcd(a_0, a_1, ..., a_n)
     */
    public static long gcd(final long... a) {
        long g = 1;
        int n = a.length;
        if (n > 4) {
            long[] b = new long[n - 1];
            System.arraycopy(a, 1, b, 0, n - 1);
            g = gcd(a[0], gcd(b));
        } else {
            switch (n) {
                case 4:
                    g = gcd(a[0], gcd(a[1], gcd(a[2], a[3])));
                    break;
                case 3:
                    g = gcd(a[0], gcd(a[1], a[2]));
                    break;
                case 2:
                    g = gcd(a[0], a[1]);
                    break;
                case 1:
                    g = abs(a[0]);
                    break;
                default:
                    throw new IllegalArgumentException(GCD_NEEDS_ARGUMENTS);
            }
        }
        return g;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.NFunction#f(long[])
     */
    @Override
    public long f(final long... x) {
        return gcd(x);
    }


}
 