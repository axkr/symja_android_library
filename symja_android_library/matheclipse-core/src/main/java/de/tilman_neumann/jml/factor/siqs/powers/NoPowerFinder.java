/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann (www.tilman-neumann.de)
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
package de.tilman_neumann.jml.factor.siqs.powers;

import java.math.BigInteger;

import de.tilman_neumann.jml.factor.siqs.data.BaseArrays;
import de.tilman_neumann.jml.factor.siqs.sieve.SieveParams;

/**
 * Dummy implementation of PowerFinder that ignores powers.
 * @author Tilman Neumann
 */
public class NoPowerFinder implements PowerFinder {

	@Override
	public String getName() {
		return "noPowers";
	}
	
	public BaseArrays addPowers(BigInteger kN, int[] primes, int[] tArray, byte[] logPArray, int primeBaseSize, SieveParams sieveParams) {
		//all exponents are 1
		int [] exponents = new int[primeBaseSize];
		for (int i=primeBaseSize-1; i >= 0; i--) {
			exponents[i] = 1;
		}
		
		return new BaseArrays(primes, exponents, primes, tArray, logPArray);
	}
}
