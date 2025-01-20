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
 * xoroshiro128+ is a 64 bit random number generator using 128 bit state.
 * 
 * @see https://nullprogram.com/blog/2017/09/21/
. * 
 * @author Tilman Neumann
 */
public class Xoroshiro128Plus {
	
	private long state0, state1;
	
	public Xoroshiro128Plus() {
		SplitMix64 splitMix = new SplitMix64();
		long seed = System.currentTimeMillis();
		state0 = seed ^ splitMix.nextLong();
		state1 = seed ^ splitMix.nextLong();
	}
	
	public long nextLong() {
	    long s0 = state0;
	    long s1 = state1;
	    long result = s0 + s1;
	    s1 ^= s0;
	    state0 = ((s0 << 55) | (s0 >>> 9)) ^ s1 ^ (s1 << 14);
	    state1 = (s1 << 36) | (s1 >>> 28);
	    return result;
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
