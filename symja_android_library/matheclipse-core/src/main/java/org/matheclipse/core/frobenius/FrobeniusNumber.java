package org.matheclipse.core.frobenius;
/*
 * 2016-09-04: Copied and modified under Lesser GPL license from
 * <a href="http://redberry.cc/">Redberry: symbolic tensor computations</a> with
 * permission from the original authors Stanislav Poslavsky and Dmitry Bolotin.
 * 
 * Following is the original header:
 * 
 * Redberry: symbolic tensor computations.
 *
 * Copyright (c) 2010-2015:
 *   Stanislav Poslavsky   <stvlpos@mail.ru>
 *   Bolotin Dmitriy       <bolotin.dmitriy@gmail.com>
 *
 * This file is part of Redberry.
 *
 * Redberry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Redberry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Redberry. If not, see <http://www.gnu.org/licenses/>.
 */

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;

/**
 * @author Dmitry Bolotin
 * @author Stanislav Poslavsky
 */
public class FrobeniusNumber {

    private static final BigInteger MINUS_ONE = BigInteger.valueOf(-1);

    /**
     * Returns Frobenius number
     * <p/>
     * <br>The implementation is taken from
     * <br>
     * http://dansesacrale.wordpress.com/2010/09/20/frobenius-numbers-round-robin-algorithm/
     * </br>
     * </br>
     *
     * @param array "coins denominations"
     * @return Frobenius number
     */
    public static BigInteger frobeniusNumber(int[] array) {
        BigInteger[] _array = new BigInteger[array.length];
        for (int i = array.length - 1; i >= 0; --i)
            _array[i] = BigInteger.valueOf(array[i]);
        return frobeniusNumber(_array);
    }

    /**
     * Returns Frobenius number
     * <p/>
     * <br>The implementation is taken from
     * <br>
     * http://dansesacrale.wordpress.com/2010/09/20/frobenius-numbers-round-robin-algorithm/
     * </br>
     * </br>
     *
     * @param array "coins denominations"
     * @return Frobenius number
     */
    public static BigInteger frobeniusNumber(long[] array) {
        BigInteger[] _array = new BigInteger[array.length];
        for (int i = array.length - 1; i >= 0; --i)
            _array[i] = BigInteger.valueOf(array[i]);
        return frobeniusNumber(_array);
    }

    private static int ARRAY_SIZE_THRESHOLD_INT = 100;
    private static BigInteger ARRAY_SIZE_THRESHOLD = BigInteger.valueOf(ARRAY_SIZE_THRESHOLD_INT);


    /**
     * Returns Frobenius number
     * <p/>
     * <br>The implementation is taken from
     * <br>
     * http://dansesacrale.wordpress.com/2010/09/20/frobenius-numbers-round-robin-algorithm/
     * </br>
     * </br>
     *
     * @param array "coins denominations"
     * @return Frobenius number
     */
    public static BigInteger frobeniusNumber(BigInteger[] array) {
        if (array[0].compareTo(ARRAY_SIZE_THRESHOLD) > 0)
            return frobeniusNumberIntegerArray(array);
        else
            return frobeniusNumberIntegerArray(array);
    }

    public static BigInteger frobeniusNumberBigIntArray(BigInteger[] array) {
        HashMap<BigInteger, BigInteger> ns = new HashMap<>(ARRAY_SIZE_THRESHOLD_INT);
        ns.put(ZERO, ZERO);
        for (int i = 1; i < array.length; i++) {
            BigInteger d = gcd(array[0], array[i]);
            for (BigInteger r = ZERO; r.compareTo(d) < 0; r = r.add(ONE)) {
                BigInteger n = MINUS_ONE;
                if (r.compareTo(ZERO) == 0)
                    n = ZERO;
                else {
                    BigInteger q = r;
                    while (q.compareTo(array[0]) < 0) {
                        if (ns.containsKey(q) && (ns.get(q).compareTo(n) < 0 || n.equals(MINUS_ONE)))
                            n = ns.get(q);
                        q = q.add(d);
                    }
                }
                if (!n.equals(MINUS_ONE))
                    for (int j = 0, size = intValue(array[0].divide(d)); j < size; j++) {
                        n = n.add(array[i]);
                        BigInteger p = n.remainder(array[0]);
                        if (ns.containsKey(p) && (ns.get(p).compareTo(n) < 0 || n.equals(MINUS_ONE)))
                            n = ns.get(p);
                        ns.put(p, n);
                    }
            }
        }
        BigInteger max = MINUS_ONE;
        for (BigInteger c : ns.values())
            if (c.compareTo(max) > 0)
                max = c;
        return max.subtract(array[0]);

    }

    private static BigInteger frobeniusNumberIntegerArray(BigInteger[] array) {
        int array0 = intValue(array[0]);
        BigInteger[] ns = new BigInteger[array0];
        Arrays.fill(ns, MINUS_ONE);
        ns[0] = ZERO;
        for (int i = 1; i < array.length; i++) {
            int d = intValue(gcd(array[0], array[i]));
            for (int r = 0; r < d; r++) {
                BigInteger n = MINUS_ONE;
                if (r == 0)
                    n = ZERO;
                else {
                    int q = r;
                    while (q < array0) {
                        if (!ns[q].equals(MINUS_ONE) && (ns[q].compareTo(n) < 0 || n.equals(MINUS_ONE)))
                            n = ns[q];
                        q += d;
                    }
                }
                if (!n.equals(MINUS_ONE))
                    for (int j = 0, size = array0 / d; j < size; j++) {
                        n = n.add(array[i]);
                        int p = intValue(n.remainder(array[0]));
                        if (!ns[p].equals(MINUS_ONE) && (ns[p].compareTo(n) < 0 || n.equals(MINUS_ONE)))
                            n = ns[p];
                        ns[p] = n;
                    }
            }
        }
        BigInteger max = ZERO;
        for (int i = 0; i < array0; i++)
            if (ns[i].equals(MINUS_ONE) || ns[i].compareTo(max) > 0)
                max = ns[i];
        if (max.equals(MINUS_ONE))
            return MINUS_ONE;
        return max.subtract(array[0]);
    }


    private static BigInteger gcd(BigInteger a, BigInteger b) {
        return a.gcd(b);
    }

    private static final BigInteger MAX_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);

    private static int intValue(BigInteger integer) {
        if (integer.compareTo(MAX_VALUE) > 0)
            throw new UnsupportedOperationException("Integer overflow.");
        return integer.intValue();
    }
}
