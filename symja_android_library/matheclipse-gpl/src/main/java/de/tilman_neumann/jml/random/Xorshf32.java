/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018-2025 Tilman Neumann - tilman.neumann@web.de
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
package de.tilman_neumann.jml.random;

/**
 * Java port of Marsaglia's xorshf 32 bit random number generator, adapted from https://stackoverflow.com/questions/1640258/need-a-fast-random-generator-for-c.
 * 
 * This version seems out-dated, the variant described in https://en.wikipedia.org/wiki/Xorshift uses other shifts...
 * 
 * @author Tilman Neumann
 */
public class Xorshf32 {
	
	private int x=123456789, y=362436069, z=521288629;
	
	/**
	 * @return a random long number N with Integer.MIN_VALUE <= N <= Integer.MAX_VALUE.
	 */
	public int nextInt() {
		int t;
	    x ^= x << 16;
	    x ^= x >> 5;
	    x ^= x << 1;

	    t = x;
	    x = y;
	    y = z;
	    z = t ^ x ^ y;

	    return z;
	}
	
	/**
	 * @param max
	 * @return a random int number N with 0 <= N <= max.
	 */
	public int nextInt(int max) {
		final long l = nextInt() & 0xFFFFFFFFL; // take it as unsigned
		final long prod = l * max;
	    return (int) (prod >>> 32);
	}
	
	public int nextInt(int min, int max) {
	    return min + nextInt(max - min);
	}
}
