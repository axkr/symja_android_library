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

package de.lab4inf.math.util;

import java.util.ArrayList;
import java.util.Iterator;

import de.lab4inf.math.Function;
import de.lab4inf.math.L4MObject;
import de.lab4inf.math.Letters;
import de.lab4inf.math.PrimeSieve;
import de.lab4inf.math.gof.Visitor;

import static de.lab4inf.math.util.Accuracy.hasConverged;
import static java.lang.Math.log;
import static java.lang.String.format;

/**
 * Simple utility class for prime numbers. It provides a simple prime number
 * test and an iterator over all prime numbers less than a given maximal value.
 * <pre>
 * The cache size of type "int" is large enough to judge all primes
 * x &lt; Integer.MAX_VALUE without further calculation.
 * Long numbers x&gt; 2^46 &gt; 7.E13 will need time consuming update steps.
 * </pre>
 *
 * @author nwulff
 * @version $Id: PrimeNumbers.java,v 1.50 2014/11/16 21:47:23 nwulff Exp $
 * @since 26.06.2010
 */
public final class PrimeNumbers extends L4MObject implements PrimeSieve {
    public static final char PI = Letters.PI;
    /**
     * the cache size for primes of type int.
     */
    public static final int INT_CACHED = 4800; // tuned for Integer.MAX_VALUE
    /**
     * the cache size for primes of type long.
     */
    public static final int LONG_CACHED = 569000; // tuned for 2**46 >7E13
    static final long MAX_INT = Integer.MAX_VALUE;
    /**
     * The maximal long prime we cache.
     */
    static final long MAX_LONG = 1L << 46;
    /**
     * Relative accuracy for the Li(x) approximation
     */
    static final double REPS = 5.E-5;
    /**
     * Maximal iteration depth for Li(x).
     */
    static final int MAX = 150;
    /**
     * Number of primes for the quick prime check.
     */
    static final int QPRIMES = 10;
    /**
     * cached integer primes.
     */
    static final int[] IPRIMES = new int[INT_CACHED];
    /**
     * cached long primes.
     */
    static final long[] LPRIMES = new long[LONG_CACHED];
    private static final String NEGATIV_EXPONENT = "negativ exponent ";
    private static final String PRIME_CHECK_OK = "p[%d]=%d prime check ok up to %d >= %d";
    private static final String PRIME_CHECK_FAILED = "p[%d]=%d prime check insufficient %d < %d";
    /**
     * true value of the quick prime check.
     */
    private static final int TRUE = 1;
    /**
     * false value of the quick prime check.
     */
    private static final int FALSE = -1;
    /**
     * unknown value of the quick prime check.
     */
    private static final int UNKNOWN = 0;
    /**
     * conversion factor form natural to binary logarithm.
     */
    private static final double LOG2 = log(2);
    /**
     * number of actual cached integers primes.
     */
    static int iPrimes = 0;
    /**
     * the maximal cached integers prime.
     */
    static int maxIPrime;
    /**
     * number of actual cached long primes.
     */
    static int lPrimes = 0;
    /**
     * the maximal cached long prime.
     */
    static long maxLPrime;

    /**
     * Default constructor.
     */
    public PrimeNumbers() {
        if (iPrimes <= 0) {
            intialize();
        }
    }

    /**
     * Internal initialization of the primes array.
     */
    private void intialize() {
        updatePrimes(2);
        updatePrimes(3);
        updatePrimes(5);
        updatePrimes(7);
        updatePrimes(11);
        updatePrimes(13);
        updatePrimes(17);
        updatePrimes(19);
        updatePrimes(23);
        updatePrimes(29); // QPPRIMES == 10
        int p = IPRIMES[iPrimes - 1];
        while (iPrimes < INT_CACHED) {
            p = nextPrime(p);
        }
    }

    /**
     * Optional initialization of the long prime number cache.
     */
    private void intializeLong() {
        long p = IPRIMES[iPrimes - 1];
        while (lPrimes < LONG_CACHED) {
            p = nextPrime(p);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.PrimeSieve#getIterator(long)
     */
    @Override
    public Iterator<Integer> getIterator(final int maximum) {
        return new IntIterator(maximum);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.PrimeSieve#getIterator()
     */
    @Override
    public Iterator<Integer> getIterator() {
        return new IntIterator();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.PrimeSieve#getLongIterator()
     */
    @Override
    public Iterator<Long> getLongIterator() {
        intializeLong();
        return new LongIterator(getMaxPrimeCached() >> 2);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.PrimeSieve#factors(long)
     */
    @Override
    public int[] factors(final int x) {
        final ArrayList<Integer> factors = new ArrayList<Integer>();
        int v = x, p = 2;
        if (x != 1 && !isPrime(x)) {
            while (v >= p && !isPrime(v)) {
                if (v % p == 0) {
                    factors.add(p);
                    v /= p;
                } else {
                    p = nextPrime(p);
                }
            }
            if (v > 1) {
                factors.add(v);
            }
        } else {
            factors.add(x);
        }
        final int[] result = new int[factors.size()];
        for (int i = 0; i < factors.size(); result[i] = factors.get(i), i++)
            ;
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.PrimeSieve#factors(long)
     */
    @Override
    public long[] factors(final long x) {
        final ArrayList<Long> factors = new ArrayList<Long>();
        long v = x, p = 2;
        if (x != 1 && !isPrime(x)) {
            while (v >= p && !isPrime(v)) {
                if (v % p == 0) {
                    factors.add(p);
                    v /= p;
                } else {
                    p = nextPrime(p);
                }
            }
            if (v > 1) {
                factors.add(v);
            }
        } else {
            factors.add(x);
        }
        final long[] result = new long[factors.size()];
        for (int i = 0; i < factors.size(); result[i] = factors.get(i), i++)
            ;
        return result;
    }

    /**
     * Divisor function: sum of all divisors of n.
     * <pre>
     *
     *  sigma(n) = &sum; d
     *            d|n
     * </pre>
     *
     * @param n number to check
     * @return sum of all divisors
     */
    public long sigma(final int n) {
        long sigma = n;
        for (long f = 1; f < n; f++) {
            if (n % f == 0) {
                sigma += f;
            }
        }
        return sigma;
    }

    /**
     * kth-Divisor function: sum of all divisors of n to the power of k.
     * <pre>
     *
     *  sigma(n) = &sum; d**k
     *            d|n
     * </pre>
     *
     * @param n number to check
     * @param k number the exponent
     * @return sum of all divisors to the power of k
     */
    public long sigma(final int n, final int k) {
        long sigma = pow(n, k);
        for (int f = 1; f < n; f++) {
            if (n % f == 0) {
                sigma += pow(f, k);
            }
        }
        return sigma;
    }

    /**
     * Calculate the yth-power of x.
     *
     * @param x the argument
     * @param y the exponent
     * @return x**y
     */
    public long pow(final int x, final int y) {
        long pow = 1;
        if (y < 0)
            throw new IllegalArgumentException(NEGATIV_EXPONENT + y);
        if (y == 0) {
            pow = 1;
        } else if (y == 1) {
            pow = x;
        } else {
            long xk = x, expo = y;
            while (expo > 0) {
                if ((expo & 1) == 1) {
                    pow *= xk;
                }
                xk *= xk;
                expo >>= 1;
            }
        }
        return pow;
    }

    /**
     * Calculate the yth-power of x modulo m.
     *
     * @param x the argument
     * @param y the exponent
     * @param m the modulus
     * @return x**y mod m
     */
    public long pow(final int x, final int y, final int m) {
        long pow = 1;
        if (y < 0)
            throw new IllegalArgumentException(NEGATIV_EXPONENT + y);
        if (y == 0) {
            pow = 1;
        } else if (y == 1) {
            pow = x % m;
        } else {
            long xk = x % m, expo = y;
            while (expo > 0) {
                if ((expo & 1) == 1) {
                    pow *= xk;
                    pow %= m;
                }
                xk *= xk;
                xk %= m;
                expo >>= 1;
            }
        }
        return pow;
    }

    /**
     * Utility method to present the prime factors of x as string.
     *
     * @param x number to factorize
     * @return string representation
     */
    public String strFactors(final long x) {
        long[] f;
        if (x < Integer.MAX_VALUE) {
            final int[] g = factors((int) x);
            f = new long[g.length];
            for (int i = 0; i < f.length; f[i] = g[i], i++)
                ;
        } else {
            f = factors(x);
        }
        final StringBuffer b = new StringBuffer(format("%d=%d", x, f[0]));
        for (int i = 1; i < f.length; i++) {
            b.append("*");
            b.append(f[i]);
        }
        return b.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.PrimeSieve#nextPrime(long)
     */
    @Override
    public int nextPrime(final int x) {
        int p = x;
        if (p <= 1) { // if (p <= 1 || !isPrime(p)) {
            throw new IllegalArgumentException("not a prime: " + p);
        }
        int s = 0, e = iPrimes - 1, i;
        if (x == 2)
            return 3;
        if (x % 2 == 0)
            p--;
        int q = IPRIMES[e];
        if (p < q) {
            // binary search for the right index
            do {
                i = (s + e) >>> 1; // (s+e)/2 without sign overflow
                q = IPRIMES[i];
                if (q <= p) {
                    s = i;
                } else {
                    e = i;
                }
            } while ((e - s) > 1);
            if (q <= x)
                i++;
            return IPRIMES[i];
        }
        for (q = p + 2; !isPrime(q); q += 2)
            ;
        return q;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.PrimeSieve#nextPrime(long)
     */
    @Override
    public long nextPrime(final long x) {
        final long p = x;
        if (p < maxIPrime) {
            return nextPrime((int) x);
        }
        int s = 0, e = lPrimes - 1, i;
        long q = LPRIMES[e];
        if (p < q) {
            // binary search for the right index
            do {
                i = (s + e) >>> 1; // (s+e)/2 without sign overflow
                q = LPRIMES[i];
                if (q <= p) {
                    s = i;
                } else {
                    e = i;
                }
            } while ((e - s) > 1);
            if (q <= x)
                i++;
            return LPRIMES[i];
        }
        for (q = p + 2; !isPrime(q); q += 2)
            ;
        if (maxLPrime < q) {
            updatePrime(q);
        }
        return q;
    }

    /**
     * Quick prime check for the first primes.
     *
     * @param x number to check
     * @return 1 if prime, -1 if not 0 if unknown
     */
    private int quickPrimeCheck(final long x) {
        int ret = UNKNOWN;
        if (x == 2 || x == 3 || x == 5 || x == 7 || x == 11 || x == 13 || x == 17 || x == 19 || x == 23 || x == 29) {
            ret = TRUE;
        } else if (x == 1 || x % 2 == 0 || x % 3 == 0 || x % 5 == 0 || x % 7 == 0 || x % 11 == 0 || x % 13 == 0 || x % 17 == 0 || x % 19 == 0 || x % 23 == 0 || x % 29 == 0) {
            ret = FALSE;
        }
        return ret;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.PrimeSieve#isPrime(long)
     */
    @Override
    public boolean isPrime(final long x) {
        boolean prime = true;
        if (x < 0) {
            return isPrime(-x);
        }
        if (x <= Integer.MAX_VALUE) {
            return isPrime((int) x);
        }
        if (lPrimes <= 1) {
            intializeLong();
        }
        // do some quick test for the first primes
        final int qpc = quickPrimeCheck(x);
        if (qpc == FALSE) {
            prime = false;
        } else if (qpc == UNKNOWN) {
            prime = checkPrime(x);
        }
        return prime;
    }

    /**
     * Internal prime check and optional update.
     *
     * @param x number to check
     * @return boolean prime indicator
     */
    private boolean checkPrime(final long x) {
        long p, q;
        final long mp = (long) Math.sqrt(x) + 1;
        boolean prime = true;
        int i;
        for (i = QPRIMES, p = IPRIMES[i - 1]; prime && p <= mp && i < iPrimes; prime = (x % p) != 0, p = IPRIMES[i], i++)
            ;
        for (i = 0; prime && p <= mp && i < lPrimes; prime = (x % p) != 0, p = LPRIMES[i], i++)
            ;
        if (prime && mp > maxLPrime) {
            logger.warn(format("time consuming prime check for n=" + x));
            // the brute force, to be changed to better Atkin sieve?
            for (q = p + 2; prime && q < mp; prime = (x % q != 0), q += 2)
                ;
        }
        if (prime && x > maxLPrime) {
            updatePrime(x);
        }
        return prime;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.lab4inf.math.PrimeSieve#isPrime(long)
     */
    @Override
    public boolean isPrime(final int x) {
        boolean prime = true;
        if (x < 0) {
            return isPrime(-x);
        }
        // do some quick test for the first primes
        final int qpc = quickPrimeCheck(x);
        if (qpc == FALSE) {
            prime = false;
        } else if (qpc == UNKNOWN) {
            prime = checkPrime(x);
        }
        return prime;
    }

    /**
     * Internal prime check and optional update.
     *
     * @param x number to check
     * @return boolean prime indicator
     */
    private boolean checkPrime(final int x) {
        boolean prime = true;
        int i, p;
        final int mp = (int) Math.sqrt(x) + 1;
        for (i = QPRIMES, p = IPRIMES[i - 1]; prime && p <= mp && i < iPrimes; prime = (x % p) != 0, p = IPRIMES[i], i++)
            ;
        if (prime && x > maxIPrime) {
            updatePrimes(x);
        }
        return prime;
    }

    /**
     * Check if n is a power k of some other natural a.
     *
     * @param n value to check
     * @return true if n = a**k
     */
    public boolean isPower(final long n) {
        int k = 2;
        final int max = (int) (log(n) / LOG2 + 1);
        long v;
        do {
            v = (long) Math.pow(n, 1.0 / k);
            v = (long) Math.pow(v, k);
            if (n == v)
                return true;
            k++;
        } while (k < max);
        return false;
    }

    /**
     * The number of calculated primes.
     *
     * @return number of primes calculated
     */
    public int getNumCached() {
        return iPrimes + lPrimes;
    }

    /**
     * The maximal calculated prime number in cache.
     *
     * @return maximal cached prime number
     */
    public long getMaxPrimeCached() {
        if (maxLPrime > maxIPrime)
            return maxLPrime;
        return maxIPrime;
    }

    /**
     * Internal method to add a new found prime to the cache if size fits.
     *
     * @param x
     */
    private void updatePrimes(final int x) {
        if (iPrimes < IPRIMES.length) {
            IPRIMES[iPrimes++] = x;
            maxIPrime = x;
            if (iPrimes == IPRIMES.length) {
                String msg;
                long check = x;
                check *= x;
                if (check <= Integer.MAX_VALUE) {
                    msg = format(PRIME_CHECK_FAILED, iPrimes, maxIPrime, check, MAX_INT);
                    logger.error(msg);
                    throw new IllegalArgumentException(msg);
                } else {
                    msg = format(PRIME_CHECK_OK, iPrimes, maxIPrime, check, MAX_INT);
                    logger.info(msg);
                }
                // put this value also as seed into the long cache
                updatePrime(x);
            }
        }
    }

    /**
     * Internal method to add a new found prime to the cache if size fits.
     *
     * @param x
     */
    private void updatePrime(final long x) {
        if (lPrimes < LPRIMES.length) {
            LPRIMES[lPrimes++] = x;
            maxLPrime = x;
            if (lPrimes == LPRIMES.length) {
                String msg;
                final long check = x * x;
                if (check <= MAX_LONG) {
                    msg = format(PRIME_CHECK_FAILED, lPrimes, maxLPrime, check, MAX_LONG);
                    logger.error(msg);
                    throw new IllegalArgumentException(msg);
                } else {
                    msg = format(PRIME_CHECK_OK, lPrimes, maxLPrime, check, MAX_LONG);
                    logger.info(msg);
                }
            }
        }
    }

    /**
     * Calculate the number of primes less than x. Note: f the argument is
     * larger than the maximal cached prime number an approximation via li(x) is
     * returned.
     *
     * @param x the argument
     * @return pi(x) the prime counting function value
     */
    public double pi(final double x) {
        if (x > maxIPrime)
            intializeLong();
        if (x > getMaxPrimeCached()) {
            final double y = li(x);
            final String msg = format("approx %s(%.1g)=%g", PI, x, y);
            logger.info(msg);
            return y;
        } else if (x < 2) {
            return 0;
        }
        // do a binary search to find pi(n-1)<x<=pi(n)
        int e = iPrimes, s = 0, m, n = e;
        long p;
        if (x < maxIPrime) {
            do {
                m = n;
                n = (s + e) >>> 1; // (s+e)/2 without sign overflow
                p = IPRIMES[n];
                if (p <= x) {
                    s = n;
                } else {
                    e = n;
                }
            } while (m != n);
            n++;
        } else {
            e = lPrimes;
            n = e;
            do {
                m = n;
                n = (s + e) >>> 1; // (s+e)/2 without sign overflow
                p = LPRIMES[n];
                if (p <= x) {
                    s = n;
                } else {
                    e = n;
                }
            } while (m != n);
            n += iPrimes;
        }
        return n;
    }

    /**
     * Calculate logarithmic integral function Li(x) = li(x)-li(2), which is a
     * good approximation of pi(x) for large x.
     *
     * @param x the argument
     * @return Li(x) the prime counting function value
     */
    private double li(final double x) {
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
        return li;
    }

    /**
     * Factory method to create an instance of the prime counting function
     * pi(x).
     *
     * @return pi(x) function
     */
    public Pi createCountingFunction() {
        return new Pi();
    }

    /**
     * The prime counting function pi(x), calculating the number of primes less
     * a given number.
     */
    public class Pi implements Function {
        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.Function#f(double[])
         */
        @Override
        public double f(final double... x) {
            return pi(x[0]);
        }

        /*
         * (non-Javadoc)
         *
         * @see de.lab4inf.math.gof.Visitable#accept(de.lab4inf.math.gof.Visitor)
         */
        @Override
        public void accept(final Visitor<Function> visitor) {
            visitor.visit(this);
        }
    }

    /**
     * Internal iterator over all prime numbers.
     */
    private final class IntIterator implements Iterator<Integer> {
        private final int maximalPrime;
        private int index = 0;
        private int prime = IPRIMES[0];
        private int nextPrime = IPRIMES[1];

        /**
         * Default constructor using the cached primes.
         */
        IntIterator() {
            this(IPRIMES[INT_CACHED - 1]);
        }

        /**
         * Constructor with maximal number to iterate to.
         *
         * @param max number of iteration
         */
        IntIterator(final int max) {
            maximalPrime = max;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            final int k = index + 1;
            if (k < iPrimes) {
                nextPrime = IPRIMES[k];
            } else {
                nextPrime = nextPrime(prime);
            }
            return nextPrime < maximalPrime;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.Iterator#next()
         */
        @Override
        public Integer next() {
            if (index < iPrimes) {
                prime = IPRIMES[index++];
            } else {
                prime = nextPrime;
                nextPrime = nextPrime(prime);
            }
            return prime;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() {
            // nothing to do
        }
    }

    /**
     * Internal iterator over all prime numbers.
     */
    private final class LongIterator implements Iterator<Long> {
        private final long maximalPrime;
        private int index = 0;
        private boolean useInt = true;
        private long prime = IPRIMES[0];
        private long nextPrime = IPRIMES[1];

        /**
         * Constructor with maximal number to iterate to.
         *
         * @param max number of iteration
         */
        LongIterator(final long max) {
            if (max > MAX_LONG) {
                throw new IllegalArgumentException("max " + max);
            }
            maximalPrime = max;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.Iterator#hasNext()
         */
        @Override
        public boolean hasNext() {
            int k = index + 1;
            if (k < iPrimes && useInt) {
                nextPrime = IPRIMES[k];
            } else {
                if (useInt) {
                    useInt = false;
                    k = 0;
                }
                if (k < lPrimes) {
                    nextPrime = LPRIMES[k];
                } else {
                    nextPrime = nextPrime(prime);
                }
            }
            return nextPrime < maximalPrime;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.Iterator#next()
         */
        @Override
        public Long next() {
            if (useInt && index < iPrimes) {
                prime = IPRIMES[index++];
            } else {
                if (index < lPrimes) {
                    prime = LPRIMES[index++];
                } else {
                    prime = nextPrime;
                    nextPrime = nextPrime(prime);
                }
            }
            return prime;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() {
            // nothing to do
        }
    }
}
 