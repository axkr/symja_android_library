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

import de.lab4inf.math.Letters;
import de.lab4inf.math.NFunction;
import de.lab4inf.math.util.PrimeNumbers;

/**
 * The number-theoretic M&ouml;bius &mu;(n)-function, defined for all natural
 * integers n>0.
 * <pre>
 *              1 if n is square free with an even number of prime factors
 *  mu(n) = {  -1 if n is square free with an odd number of prime factors
 *              0 if n is not square free
 *
 * </pre>
 *
 * @author nwulff
 * @version $Id: MoebiusFunction.java,v 1.5 2014/06/17 09:38:22 nwulff Exp $
 * @since 16.10.2011
 */
public class MoebiusFunction implements NFunction {
    /**
     * the greek &mu; character as unicode.
     */
    public static final char MU = Letters.MU;
    private static final String FUNCTION_NEEDS_ONE_ARGUMENT = "Moebius " + MU + "-function needs one argument";
    private static PrimeNumbers sieve = new PrimeNumbers();

    /**
     * Calculate the Moebius-function value for given n.
     *
     * @param n the argument
     * @return mu(n)
     */
    public static long mu(final long n) {
        int ret = 1;
        if (n < 1) {
            throw new IllegalArgumentException("n=" + n);
        }
        long p, q;
        if (1 == n) {
            ret = 1; // by definition
        } else if (sieve.isPrime(n)) {
            ret = -1; // a prime is always odd
        } else {
            long[] primes = sieve.factors(n);
            p = primes[0];
            // search for powers of prime factors
            for (int i = 1; i < primes.length; i++) {
                q = primes[i];
                // find squares
                if (p == q) {
                    ret = 0;
                    break;
                }
                p = q;
            }
            if (ret != 0) {
                // discriminate odd and even number of primes
                if ((primes.length & 1) == 0) {
                    ret = 1; // even
                } else {
                    ret = -1; // odd
                }
            }
        }
        return ret;
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
        return mu(x[0]);
    }
}
 