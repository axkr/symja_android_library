/*
 * Copyright 2022 Sander Verdonschot <sander.verdonschot at gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.mangara.diophantine.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A classic sieve of Eratosthenes.
 */
public class Primes {

    private static long currentUpperbound = 10000;
    private static int primesUpto = 100;
    private static final ArrayList<Integer> primes = new ArrayList<>(Arrays.asList(
            2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97
    ));

    /**
     * Returns a list of all prime factors of n.
     * <p>
     * Factors may appear multiple times. For example, the factors of 24 are [2, 2, 2, 3].
     *
     * @param n the number to factor, must be positive
     * @return
     */
    public static List<Long> getPrimeFactors(long n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be posititve");
        }
        if (n == 1) {
            return Collections.<Long>emptyList();
        }

        checkUpperBound(n);
        List<Long> factors = new ArrayList<>();

        for (Integer p : primes) {
            if ((long) p * p > n) {
                break;
            }

            while (n % p == 0) {
                factors.add((long) p);
                n /= p;
            }

            if (n == 1) {
                break;
            }
        }

        if (n > 1) {
            factors.add(n);
        }

        return factors;
    }
    
    public static List<Long> getDistinctPrimeFactors(long n) {
        return getPrimeFactors(n).stream().distinct().collect(Collectors.toList());
    }

    private static void checkUpperBound(long n) {
        if (n > currentUpperbound) {
            if (n / 2 >= currentUpperbound) {
                initializeSmallPrimes(n);
            } else if (currentUpperbound >= Long.MAX_VALUE / 2) {
                initializeSmallPrimes(Long.MAX_VALUE);
            } else {
                initializeSmallPrimes(2 * currentUpperbound);
            }
        }
    }

    private static void initializeSmallPrimes(long upperbound) {
        // Run the sieve of Eratosthenes
        long maxPrimes = (long) Math.sqrt(upperbound);
        int maxPrimesI = (int) maxPrimes;

        if (maxPrimes != maxPrimesI) {
            throw new IllegalArgumentException("Upperbound too large.");
        }

        boolean[] prime = new boolean[maxPrimesI + 1]; // prime[i] => i might still be prime

        Arrays.fill(prime, true);

        // We already know all primes up to primesUpto. Use these to cross out non-primes in the following part.
        for (Integer p : primes) {
            // Multiples of p below p * p have been crossed out already
            int start = Math.max(primesUpto + 1, p * p);

            // Skip to the next multiple of p
            if (start % p != 0) {
                start += p - (start % p);
            }

            // Check if we're done sieving
            if (start > prime.length) {
                break;
            }

            // Mark all further multiples of p as non-prime
            for (int i = start; i < prime.length; i += p) {
                prime[i] = false;
            }
        }

        // Continue sieving if necessary, but only with odd numbers
        int firstPossiblePrime = primesUpto + 1;

        if (firstPossiblePrime % 2 == 0) {
            firstPossiblePrime++;
        }

        int lastValue = firstPossiblePrime;

        for (int p = firstPossiblePrime; p < prime.length; p += 2) {
            lastValue = p;

            if (prime[p]) {
                // Add p to our list of primes
                primes.add(p);

                // Multiples of p below p * p have been crossed out already
                int start = p * p;

                // Check if we're done sieving (or overflowed)
                if (start > prime.length || start < p) {
                    break;
                }

                // Mark all further multiples of p as non-prime
                for (int i = start; i < prime.length; i += p) {
                    prime[i] = false;
                }
            }
        }

        // Add all remaining primes to our list
        for (int p = lastValue + 2; p < prime.length; p += 2) {
            if (prime[p]) {
                // Add p to our list of primes
                primes.add(p);
            }
        }

        primesUpto = maxPrimesI;
        currentUpperbound = upperbound;
    }
}
