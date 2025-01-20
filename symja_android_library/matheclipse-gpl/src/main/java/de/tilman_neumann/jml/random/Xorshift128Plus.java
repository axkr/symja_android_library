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
 * xorshift128+ is a 64 bit random number generator using 128 bit state.
 * Adapted from https://nullprogram.com/blog/2017/09/21/.
. * 
 * @author Tilman Neumann
 */
public class Xorshift128Plus {
	
	private long state0, state1;
	
	/**
	 * Standard constructor.
	 * 
	 * Note https://nullprogram.com/blog/2017/09/21/: There’s one important caveat: That 16-byte state must be well-seeded.
	 * Having lots of zero bytes will lead terrible initial output until the generator mixes it all up. Having all zero bytes will completely break the generator.
	 * If you’re going to seed from, say, the unix epoch, then XOR it with 16 static random bytes.
	 * 
	 * And https://en.wikipedia.org/wiki/Xorshift#Initialization: In the xoshiro paper, it is recommended to initialize the state of the generators using a generator
	 * which is radically different from the initialized generators, as well as one which will never give the "all-zero" state; for shift-register generators,
	 * this state is impossible to escape from. The authors specifically recommend using the SplitMix64 generator, from a 64-bit seed.
	 */
	public Xorshift128Plus() {
		SplitMix64 splitMix = new SplitMix64();
		long seed = System.currentTimeMillis();
		state0 = seed ^ splitMix.nextLong();
		state1 = seed ^ splitMix.nextLong();
	}
	
	public long nextLong() {
	    long x = state0;
	    long y = state1;
	    state0 = y;
	    x ^= x << 23;
	    state1 = x ^ y ^ (x >>> 17) ^ (y >>> 26);
	    return state1 + y;
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
