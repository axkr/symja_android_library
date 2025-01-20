/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann - tilman.neumann@web.de
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program;
 * if not, see <http://www.gnu.org/licenses/>.
 */
package de.tilman_neumann.util;

public class StringUtil {
	private StringUtil() {
		// static class
	}
	
    /**
     * Concatenates string s n times.
     *
     * @param s string to repeat
     * @param n number of repetitions
     * @return a string resulting from n repetitions of s, or null if n <= 0
     */
    public static String repeat(String s, int n) {
    	if (s == null || n <= 0) return null;
    	int sLen = s.length();
    	if (sLen == 0) return "";

        StringBuffer buffer = new StringBuffer(sLen * n);
        for (int i = n; i > 0; i--) {
        	buffer.append(s);
        }
        return buffer.toString();
    }

    /**
     * Inserts a string s left-aligned into a mask, without truncation.<br><br>
     *
     * Examples:<br>formatLeft("abc", "123456") -> "abc456"<br>
     *              formatLeft("abcdef", "123") -> "abcdef"<br>
     *
     * @param s
     * @param mask
     * @return s left-aligned in mask
     */
    public static String formatLeft(String s, String mask) {
        // start with s
        String ret = (s != null) ? s : "";
        if (mask != null) {
            int sLen = ret.length();
            int maskLen = mask.length();
            if (sLen < maskLen) {
                // add last chars from the mask
                ret += mask.substring(sLen, maskLen);
            }
        }
        return ret;
    }

    /**
     * Inserts a string s right-aligned into a mask, without truncation.<br><br>
     *
     * Examples:<br>formatRight("abc", "123456") -> "123abc"<br>
     *              formatRight("abcdef", "123") -> "abcdef"<br>
     *
     * @param s
     * @param mask
     * @return s right-aligned in mask
     */
    public static String formatRight(String s, String mask) {
        // start with s
        String ret = (s != null) ? s : "";
        if (mask != null) {
            int sLen = ret.length();
            int maskLen = mask.length();
            if (sLen < maskLen) {
                // add first chars from the mask
                ret = mask.substring(0, maskLen - sLen) + ret;
            }
        }
        return ret;
    }
}
