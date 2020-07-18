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

import static de.lab4inf.math.util.GCD.gcd;
import static java.lang.Math.abs;

/**
 * The least common multiple LCM as natural function.
 *
 * @author nwulff
 * @version $Id: LCM.java,v 1.2 2012/08/22 11:53:43 nwulff Exp $
 * @since 15.10.2011
 */
public class LCM extends L4MObject implements NFunction {
    private static final String LCM_NEEDS_ARGUMENTS = "lcm needs arguments";

    /**
     * Calculate the least common multiple.
     *
     * @param a first argument
     * @param b second argument
     * @return lcm(a, b)
     */
    public static long lcm(final long a, final long b) {
        if (a == 0 && b == 0) {
            return 0;
        }
        return abs(a * b) / gcd(a, b);
    }

    /**
     * Calculate the least common multiple of the arguments.
     *
     * @param a the arguments
     * @return lcm(a_0, ..., a_n)
     */
    public static long lcm(final long... a) {
        long g = 1;
        int n = a.length;
        if (n > 4) {
            long[] b = new long[n - 1];
            System.arraycopy(a, 1, b, 0, n - 1);
            g = lcm(a[0], lcm(b));
        } else {
            switch (n) {
                case 4:
                    g = lcm(a[0], lcm(a[1], lcm(a[2], a[3])));
                    break;
                case 3:
                    g = lcm(a[0], lcm(a[1], a[2]));
                    break;
                case 2:
                    g = lcm(a[0], a[1]);
                    break;
                case 1:
                    g = abs(a[0]);
                    break;
                default:
                    throw new IllegalArgumentException(LCM_NEEDS_ARGUMENTS);
            }
        }
        return g;
    }

    /* (non-Javadoc)
     * @see de.lab4inf.math.NFunction#f(long[])
     */
    @Override
    public long f(final long... x) {
        return lcm(x);
    }

}
 