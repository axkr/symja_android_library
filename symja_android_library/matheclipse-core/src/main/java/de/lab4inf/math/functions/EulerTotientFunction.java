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
package de.lab4inf.math.functions;

import de.lab4inf.math.L4MObject;
import de.lab4inf.math.Letters;
import de.lab4inf.math.NFunction;
import de.lab4inf.math.util.PrimeNumbers;

import static de.lab4inf.math.util.GCD.gcd;

/**
 * Eulers totient function &phi;(n) defined for natural integers n&gt;0 as the
 * sum of all integers d less equal n that are coprime to n.
 *
 * @author nwulff
 * @version $Id: EulerTotientFunction.java,v 1.5 2014/11/16 21:47:23 nwulff Exp $
 * @since 15.10.2011
 */
public class EulerTotientFunction extends L4MObject implements NFunction {
    /**
     * the greek &phi; character as unicode.
     */
    public static final char PHI = Letters.PHI;
    private static final String FUNCTION_NEEDS_ONE_ARGUMENT = "Euler " + PHI + " function needs one argument";
    private static PrimeNumbers sieve = new PrimeNumbers();

    public static long phi(final long n) {
        long phi = 0;
        final long m4 = n % 4;
        if (n < 1) {
            throw new IllegalArgumentException("n=" + n);
        }
        if (n == 1) {
            phi = 1;
        } else if (m4 == 0) {
            phi = phi(n >> 1) << 1;
        } else if (m4 == 2) {
            phi = phi(n >> 1);
        } else if (sieve.isPrime(n)) {
            phi = n - 1;
        } else {
            for (long k = 1; k < n; k++) {
                if (gcd(n, k) == 1) {
                    phi++;
                }
            }
        }
        return phi;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.NFunction#f(long[])
     */
    @Override
    public long f(final long... x) {
        if (x.length != 1) {
            throw new IllegalArgumentException(FUNCTION_NEEDS_ONE_ARGUMENT);
        }
        return phi(x[0]);
    }
}
 