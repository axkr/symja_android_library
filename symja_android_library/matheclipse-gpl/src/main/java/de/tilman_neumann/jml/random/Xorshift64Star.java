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

import de.tilman_neumann.jml.base.Uint128;

/**
 * xorshift64* is a 64 bit random number generator using 64 bit state.
 * Adapted from https://nullprogram.com/blog/2017/09/21/.
. * 
 * @author Tilman Neumann
 */
public class Xorshift64Star {
	
	private static final long multiplier = 0x2545f4914f6cdd1dL;
	
	private long state0;
	
	public Xorshift64Star() {
		SplitMix64 splitMix = new SplitMix64();
		long seed = System.currentTimeMillis();
		state0 = seed ^ splitMix.nextLong();
	}
	
	public long nextLong() {
	    long x = state0;
	    x ^= x >>> 12;
	    x ^= x << 25;
	    x ^= x >>> 27;
	    state0 = x;
	    return x * multiplier;
	}
	
	/**
	 * @param max
	 * @return a random long number N with 0 <= N <= max.
	 */
	public long nextLong(long max) {
		final long l = nextLong(); // take it as unsigned
		final Uint128 prod = Uint128.mul64_MH(l, max);
	    return prod.getHigh();
	}
	
	public long nextLong(long min, long max) {
	    return min + nextLong(max - min);
	}
}
