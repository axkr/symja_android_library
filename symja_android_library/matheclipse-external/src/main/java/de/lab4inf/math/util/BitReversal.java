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

import java.util.Hashtable;

/**
 * Utility class to create bit reversal of integers suitable for a radix 2 FFT.
 * For quick access the resulting mapping tables are internally cached.
 *
 * @author nwulff
 * @version $Id: BitReversal.java,v 1.7 2014/02/05 13:22:34 nwulff Exp $
 * @since 25.02.2011
 */

public final class BitReversal {
    private static byte[] rBytes;
    private static short[] rShorts;
    private static char[] rChars;
    private static int[] rInts;
    private static Hashtable<Integer, int[]> tables = new Hashtable<Integer, int[]>();

    /**
     * No instances of this class are allowed.
     */
    private BitReversal() {
    }

    /**
     * Calculate the reverse bit order of byte x.
     *
     * @param x byte to reverse
     * @return reverse bit field of x
     */
    public static byte revers(final byte x) {
        if (null == rBytes) {
            rBytes = reversBytes(4);
        }
        return rBytes[x];
    }

    /**
     * Calculate the reverse bit order of short x.
     *
     * @param x short to reverse
     * @return reverse bit field of x
     */
    public static short revers(final short x) {
        if (null == rShorts) {
            rShorts = reversShorts(8);
        }
        return rShorts[x];
    }

    /**
     * Calculate the reverse bit order of char x.
     *
     * @param x char to reverse
     * @return reverse bit field of x
     */
    public static char revers(final char x) {
        if (null == rChars) {
            rChars = reversChars(8);
        }
        return rChars[x];
    }

    /**
     * Calculate the reverse bit order of int x.
     *
     * @param x int to reverse
     * @return reverse bit field of x
     */
    public static int revers(final int x) {
        if (null == rInts) {
            rInts = reversInts(16);
        }
        return rInts[x];
    }

    /**
     * Calculate the reverse bit order of long x.
     *
     * @param x long to reverse
     * @return reverse bit field of x
     */
    public static long revers(final long x) {
        long reverse = x;
        //   0xFF00 = 1111111100000  0x00FF = 0000000011111111
        reverse = ((reverse & 0xFFFF0000FFFF0000L) >> 16) | ((reverse & 0x0000FFFF0000FFFFL) << 16);
        //   0xFF00 = 1111111100000  0x00FF = 0000000011111111
        reverse = ((reverse & 0xFF00FF00FF00FF00L) >> 8) | ((reverse & 0x00FF00FF00FF00FFL) << 8);
        //   0xF0 = 11110000    0x0F = 00001111
        reverse = ((reverse & 0xF0F0F0F0F0F0F0F0L) >> 4) | ((reverse & 0x0F0F0F0F0F0F0F0FL) << 4);
        //   0xCC = 11001100    0x33 = 00110011
        reverse = ((reverse & 0xCCCCCCCCCCCCCCCCL) >> 2) | ((reverse & 0x3333333333333333L) << 2);
        //   0xAA = 10101010    0x55 = 01010101
        reverse = ((reverse & 0xAAAAAAAAAAAAAAAAL) >> 1) | ((reverse & 0x5555555555555555L) << 1);
        return reverse;
    }

    /**
     * Calculate the reverse bit ordered array of size m=2^q.
     *
     * @param q the exponent
     * @return reverse ordered bit field
     */
    public static byte[] reversBytes(final int q) {
        int[] x = reversInts(q);
        byte[] y = new byte[x.length];
        for (int i = 0; i < x.length; y[i] = (byte) x[i], i++) ;
        return y;
    }

    /**
     * Calculate the reverse bit ordered array of size m=2^q.
     *
     * @param q the exponent
     * @return reverse ordered bit field
     */
    public static short[] reversShorts(final int q) {
        int[] x = reversInts(q);
        short[] y = new short[x.length];
        for (int i = 0; i < x.length; y[i] = (short) x[i], i++) ;
        return y;
    }

    /**
     * Calculate the reverse bit ordered array of size m=2^q.
     *
     * @param q the exponent
     * @return reverse ordered bit field
     */
    public static char[] reversChars(final int q) {
        int[] x = reversInts(q);
        char[] y = new char[x.length];
        for (int i = 0; i < x.length; y[i] = (char) x[i], i++) ;
        return y;
    }

    /**
     * Get the reverse bit ordered array of size m=2^q.
     *
     * @param q the exponent
     * @return reverse ordered bit field
     */
    public static int[] reversInts(final int q) {
        // see if we have already cached the mapping
        if (!tables.containsKey(q)) {
            tables.put(q, calculateInts(q));
        }
        return tables.get(q);
    }

    /**
     * Calculate the reverse bit ordered array of size m=2^q.
     *
     * @param q the exponent
     * @return reverse ordered bit field
     */
    private static int[] calculateInts(final int q) {
        int i, j = 0, k, n = 1 << q;
        int[] y = new int[n];
        for (i = 0; i < n; y[i] = i, i++) ;
        for (i = 0; i < n - 1; i++) {
            if (i < j) {
                y[i] = j;
                y[j] = i;
            }
            k = n >> 1;
            while (k <= j) {
                j = j - k;
                k >>= 1;
            }
            j += k;
        }
        return y;
    }
}
 