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
package de.tilman_neumann.jml.factor.tdiv;

import java.math.BigInteger;

import de.tilman_neumann.jml.factor.FactorAlgorithmBase;
import de.tilman_neumann.jml.primes.exact.AutoExpandingPrimesArray;

/**
 * Trial division factor algorithm using the safe AutoExpandingPrimesArray class.
 * 
 * @author Tilman Neumann
 */
public class TDiv63 extends FactorAlgorithmBase {
	
	private static AutoExpandingPrimesArray SMALL_PRIMES = AutoExpandingPrimesArray.get().ensurePrimeCount(NUM_PRIMES_FOR_31_BIT_TDIV);

	private int pLimit = Integer.MAX_VALUE;

	@Override
	public String getName() {
		return "TDiv63";
	}

	/**
	 * Set the upper limit of primes to be tested in the next findSingleFactor() run.
	 * 
	 * @param pLimit
	 */
	public void setTestLimit(int pLimit) {
		this.pLimit = pLimit;
	}

	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		return BigInteger.valueOf(findSingleFactor(N.longValue()));
	}
	
	public int findSingleFactor(long N) {
		int i=0, p;
		while ((p = SMALL_PRIMES.getPrime(i++)) <= pLimit) { // upper bound avoids positive int overflow
			if (N%p==0) return p;
		}
		
		// nothing found up to pLimit
		return 0;
	}
}
