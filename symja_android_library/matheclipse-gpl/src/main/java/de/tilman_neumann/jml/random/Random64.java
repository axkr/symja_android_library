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

import java.util.Random;

import de.tilman_neumann.util.Ensure;

/**
 * 64 bit random number generator using Random.nextLong() and the strategy from Java17 for Random.nextLong(long maxValue).
 */
public class Random64 {

	private static final boolean DEBUG = false;
	
	private Random random;
	
	public Random64() {
		random = new Random();
	}
	
	public Random64(long seed) {
		random = new Random(seed);
	}
	
	public long nextLong() {
		return random.nextLong();
	}
	
	/**
	 * Creates a random long from the uniform distribution U[0, maxValue-1], with maxValue > 0.
	 * This is similar to what Java 17 does in RandomGenerator.nextLong(long).
	 * 
	 * @param maxValue
	 * @return random long in the desired range
	 * 
	 * @see https://github.com/openjdk/jdk17/blob/master/src/java.base/share/classes/java/util/random/RandomGenerator.java
	 */
	public long nextLong(long maxValue) {
	   	if (Long.bitCount(maxValue) == 1) {
    		// maxValue is a power of 2
    		return nextLong() >>> Long.numberOfLeadingZeros(maxValue);
    	}

        long r = nextLong();
        long u = r >>> 1;
        while (true) {
        	r = u % maxValue; // now 0 <= r <= u and r < maxValue
        	if (DEBUG) {
	        	Ensure.ensureGreaterEquals(u, 0L);
	        	Ensure.ensureGreaterEquals(r, 0);
	        	Ensure.ensureSmallerEquals(r, u);
	        	Ensure.ensureSmaller(r, maxValue);
        	}
        	// Using the modulus r = u % maxValue to obtain random numbers < maxValue has its caveats...
            // If u is close to 2^63, then r much smaller than maxValue are over-represented.
        	// We only accept r that are close to maxValue then
        	if (u + maxValue - r >= 0) break;
        	// Otherwise reject the last random number and try another one
        	u = nextLong() >>> 1;
        }

        return r;
	}

	/**
	 * Creates a random long from the uniform distribution U[minValue, maxValue-1].
	 * Works also for negative arguments; the only requirement is maxValue > minValue.
	 * 
	 * @param minValue
	 * @param maxValue
	 * @return random long in the desired range
	 */
	public long nextLong(long minValue, long maxValue) {
		return minValue + nextLong(maxValue - minValue);
	}
}
