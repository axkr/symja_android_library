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
 * Lehmer's random number generator for 64 bit numbers; requires 128 bit multiplication internally.
 * 
 * This variant is using Math.multiplyHigh().
 * 
 * @see https://en.wikipedia.org/wiki/Lehmer_random_number_generator
 */
public class LehmerRng64MH {
	private static final Uint128 mult = new Uint128(0x12e15e35b500f16eL, 0x2e714eb2b37916a5L);

	private Uint128 state = mult;
	
	/**
	 * @return a random long number N with Long.MIN_VALUE <= N <= Long.MAX_VALUE.
	 */
	public long nextLong() {
		long result = state.getHigh();
		state = Uint128.mul128MH_getLow(state, mult);
		return result;
	}
}
