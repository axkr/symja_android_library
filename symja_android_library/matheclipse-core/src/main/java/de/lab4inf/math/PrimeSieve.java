/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2010,  Prof. Dr. Nikolaus Wulff
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

package de.lab4inf.math;

import java.util.Iterator;

/**
 * Prime number service providing a prime number iterator over the
 * primes and a prime number indicator function.
 * The methods for integers are much faster to calculate and should
 * be used for integers |p| &lt; 2**31 &lt; 2.147E9.
 * <p>
 * You can resolve a PrimeSieve implementation via the L4MLoader:
 * <pre>
 *
 *   PrimeSieve sieve = L4MLoader.load(PrimeSieve.class);
 *
 * </pre>
 *
 * @author nwulff
 * @version $Id: PrimeSieve.java,v 1.11 2014/11/16 21:47:23 nwulff Exp $
 * @since 26.06.2010
 */
@Service
public interface PrimeSieve {
    /**
     * Get an iterator over all primes with default maximum prime number.
     *
     * @return prime number iterator
     */
    Iterator<Integer> getIterator();

    /**
     * Get an iterator over all primes less then maximum.
     *
     * @param maximum maximal prime number to iterate
     * @return prime number iterator
     */
    Iterator<Integer> getIterator(final int maximum);

    /**
     * Get an iterator over all primes with long prime numbers.
     *
     * @return prime number iterator
     */
    Iterator<Long> getLongIterator();

    /**
     * Test if the given number x is a prime number.
     *
     * @param x integer to test
     * @return true if x is prime
     */
    boolean isPrime(final int x);

    /**
     * Test if the given number x is a prime number.
     *
     * @param x integer to test
     * @return true if x is prime
     */
    boolean isPrime(final long x);

    /**
     * Get the next prime number following the given number x.
     * This method is useful for loops over all primes and
     * will be used by the internal iterator implementation.
     *
     * @param x the current number
     * @return the next prime after x
     */
    int nextPrime(final int x);

    /**
     * Get the next prime number following the given number x.
     * This method is useful for loops over all primes and
     * will be used by the internal iterator implementation.
     *
     * @param x the current number
     * @return the next prime after x
     */
    long nextPrime(final long x);

    /**
     * Factor x into prime numbers.
     *
     * @param x natural to factor
     * @return prime numbers factorization
     */
    int[] factors(final int x);

    /**
     * Factor x into prime numbers.
     *
     * @param x natural to factor
     * @return prime numbers factorization
     */
    long[] factors(final long x);
}
 