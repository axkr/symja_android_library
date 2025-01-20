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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Java port of the 32-bit pseudo-random number generator from tinyEcm.c by Ben Buhrow.
 * 
 * This generator is special in that nextInt() creates strictly non-negative random numbers.
 * 
 * @author Tilman Neumann
 */
public class SpRand32 {
	private static final Logger LOG = LogManager.getLogger(SpRand32.class);
	
	private static final boolean DEBUG = false;
	
	private static final long MAX_INT_AS_LONG = Integer.MAX_VALUE;
	
	// seed
	private long LCGSTATE = 4295098403L; // rng comparable with C version of TinyEcm

	/**
	 * @return a random int number N with 0 <= N <= Integer.MAX_VALUE.
	 */
	public int nextInt() {
		if (DEBUG) LOG.debug("LCGSTATE=" + LCGSTATE);
		
		// advance the state of the LCG and return the appropriate result
		LCGSTATE = 6364136223846793005L * LCGSTATE + 1442695040888963407L;
		long LCGSTATE_shifted = LCGSTATE >>> 32;
		if (DEBUG) LOG.debug("LCGSTATE=" + LCGSTATE + ", LCGSTATE_shifted=" + LCGSTATE_shifted);
		
		double quot = (double)LCGSTATE_shifted / 4294967296.0; // dividend is 2^32
		double prod = MAX_INT_AS_LONG * quot;
		int rand = (int)(0xFFFFFFFF & (long)prod); // (int)prod does not work for prod >= 2^31
		int result = rand;
		if (DEBUG) LOG.debug("quot=" + quot + ", prod=" + prod + ", rand=" + rand + ", result=" + result);
		return result;
	}

	/**
	 * @param max
	 * @return a random int number N with 0 <= N <= max.
	 */
	public int nextInt(int max) {
		if (DEBUG) LOG.debug("LCGSTATE=" + LCGSTATE);
		
		// fix rng for negative upper values
		long maxLong = (long) max;
		if (maxLong<0) maxLong += (1L<<32);
		if (DEBUG) LOG.debug("max=" + maxLong);
		
		// advance the state of the LCG and return the appropriate result
		LCGSTATE = 6364136223846793005L * LCGSTATE + 1442695040888963407L;
		long LCGSTATE_shifted = LCGSTATE >>> 32;
		if (DEBUG) LOG.debug("LCGSTATE=" + LCGSTATE + ", LCGSTATE_shifted=" + LCGSTATE_shifted);
		
		double quot = (double)LCGSTATE_shifted / 4294967296.0; // dividend is 2^32
		double prod = maxLong * quot;
		int rand = (int)(0xFFFFFFFF & (long)prod); // (int)prod does not work for prod >= 2^31
		int result = rand;
		if (DEBUG) LOG.debug("quot=" + quot + ", prod=" + prod + ", rand=" + rand + ", result=" + result);
		return result;
	}

	/**
	 * @param min
	 * @param max
	 * @return a random int number N with lower <= N <= max.
	 */
	public int nextInt(int min, int max) {
		return min + nextInt(max - min);
	}
}
