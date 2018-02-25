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

import java.util.SortedMap;
import java.util.TreeMap;

import static java.lang.Math.abs;

/**
 * Utility class for string handling.
 *
 * @author nwulff
 * @version $Id: Strings.java,v 1.7 2012/01/19 10:30:57 nwulff Exp $
 * @since 13.09.2010
 */
public final class Strings {
    private static final char[] SUBSCRIPT = {'\u2080', '\u2081', '\u2082',
            '\u2083', '\u2084', '\u2085', '\u2086', '\u2087', '\u2088',
            '\u2089'};
    private static final char[] SUPERSCRIPT = {'\u2070', '\u00B9', '\u00B2',
            '\u00B3', '\u2074', '\u2075', '\u2076', '\u2077', '\u2078',
            '\u2079'};

    /**
     * Hidden constructor as utility class has no instances.
     */
    private Strings() {
    }

    /**
     * Return a lower subscript representation of the given number as string.
     *
     * @param x the number
     * @return lower unicode string.
     */
    public static String toLowerScript(final int x) {
        StringBuffer str = new StringBuffer();
        int r, j = abs(x);
        do {
            r = j % 10;
            str.append(SUBSCRIPT[r]);
            j /= 10;
        } while (j > 0);

        if (x < 0) {
            str.append('\u208B');
        }
        return str.reverse().toString();
    }

    /**
     * Return a upper superscript representation of the given number as string.
     *
     * @param x the number
     * @return upper unicode string.
     */
    public static String toUpperScript(final int x) {
        StringBuffer str = new StringBuffer();
        int r, j = abs(x);
        do {
            r = j % 10;
            str.append(SUPERSCRIPT[r]);
            j /= 10;
        } while (j > 0);

        if (x < 0) {
            str.append('\u207B');
        }
        return str.reverse().toString();
    }

    /**
     * Reverse the given string.
     *
     * @param s string to reverse
     * @return s in opposite order
     */
    public static String reverse(final String s) {
        char[] chars = s.toCharArray();
        char tmp;
        int j = chars.length;
        for (int i = 0; i < j / 2; i++) {
            tmp = chars[i];
            chars[i] = chars[j - i - 1];
            chars[j - i - 1] = tmp;
        }
        return new String(chars);
    }

    /**
     * Helper method to calculate the length of a string without trailing blancs.
     *
     * @param s string to check
     * @return length(s)
     */
    public static int length(final String s) {
        if (null != s) {
            return s.trim().length();
        }
        return 0;
    }

    /**
     * Helper method to check if a string is empty or null.
     *
     * @param s string to check
     * @return null check
     */
    public static boolean isNullOrEmpty(final String s) {
        return length(s) == 0;
    }

    /**
     * Helper method to check if a char array is empty or null.
     *
     * @param s char array to check
     * @return null check
     */
    public static boolean isNullOrEmpty(final char[] s) {
        if (null != s) {
            for (int i = 0; i < s.length; i++) {
                if (s[i] != ' ')
                    return false;
            }
        }
        return true;
    }

    /**
     * Helper method to calculate the minimum of some integers.
     *
     * @param x int array
     * @return min(x)
     */
    public static int minimum(final int... x) {
        int m = x[0];
        for (int i = 1; i < x.length; i++) {
            if (x[i] < m) {
                m = x[i];
            }
        }
        return m;
    }

    /**
     * Calculate the Damerau-Levenshtein distance between the two strings.
     *
     * @param source first array
     * @param target second array
     * @return dldistance(source, target)
     */
    public static int dlDistance(final String source, final String target) {
        int dist = 0;
        if (isNullOrEmpty(source)) {
            if (!isNullOrEmpty(target)) {
                dist = target.length();
            }
        } else if (isNullOrEmpty(target)) {
            dist = source.length();
        } else {
            dist = dameraulevenshtein(source.toCharArray(), target.toCharArray());
        }
        return dist;
    }

    /**
     * Calculate the Damerau-Levenshtein distance between the two char arrays.
     *
     * @param source first array
     * @param target second array
     * @return dldistance(source, target)
     */
    public static int dlDistance(final char[] source,
                                 final char[] target) {
        int dist = 0;
        if (isNullOrEmpty(source)) {
            if (!isNullOrEmpty(target)) {
                dist = target.length;
            }
        } else if (isNullOrEmpty(target)) {
            dist = source.length;
        } else {
            dist = dameraulevenshtein(source, target);
        }
        return dist;
    }

    /**
     * Internal calculation of the Damerau-Levenshtein distance.
     *
     * @param source first array
     * @param target second array
     * @return distance(source, target)
     */
    private static int dameraulevenshtein(final char[] source,
                                          final char[] target) {
        SortedMap<Character, Integer> sd = new TreeMap<Character, Integer>();
        int i, j, hc, ic, jc, m = source.length, n = target.length;
        // initialize
        int[][] d = new int[m + 2][n + 2];
        for (char c : source) if (!sd.containsKey(c)) sd.put(c, 0);
        for (char c : target) if (!sd.containsKey(c)) sd.put(c, 0);
        d[0][0] = m + n;
        for (i = 0; i <= m; d[i + 1][1] = i, d[i + 1][0] = m + n, i++) ;
        for (j = 0; j <= n; d[1][j + 1] = j, d[0][j + 1] = m + n, j++) ;
        // compare
        for (i = 1; i <= m; i++) {
            for (hc = 0, j = 1; j <= n; j++) {
                ic = sd.get(target[j - 1]);
                jc = hc;
                if (source[i - 1] == target[j - 1]) {
                    d[i + 1][j + 1] = d[i][j];
                    hc = j;
                } else {
                    d[i + 1][j + 1] = minimum(d[i][j], d[i + 1][j], d[i][j + 1]) + 1;
                }
                d[i + 1][j + 1] = minimum(d[i + 1][j + 1], d[ic][jc] + i - ic
                        + j - jc - 1);
            }
            sd.put(source[i - 1], i);
        }
        return d[m + 1][n + 1];
    }
}
 