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
import java.util.Iterator;
import java.util.TreeSet;

import de.tilman_neumann.jml.factor.siqs.data.BaseArrays;
import de.tilman_neumann.jml.factor.siqs.sieve.SieveParams;

/**
 * Base class for PowerFinders that do indeed find some powers.
 * @author Tilman Neumann
 */
abstract public class SomePowerFinder implements PowerFinder {
//	private static final Logger LOG = Logger.getLogger(SomePowerFinder.class);
//	private static final boolean DEBUG = false;

	@Override
	public BaseArrays addPowers(BigInteger kN, int[] primes, int[] tArray, byte[] logPArray, int primeBaseSize, SieveParams sieveParams) {
		TreeSet<PowerEntry> powers = findPowers(kN, primes, tArray, primeBaseSize, sieveParams);
		return mergePrimesAndPowers(primes, tArray, logPArray, primeBaseSize, powers);
	}

	/**
	 * Find some powers > pMin.
	 * 
	 * @param kN
	 * @param primes
	 * @param tArray
	 * @param primeBaseSize
	 * @param sieveParams basic sieve parameters
	 * @return powers sorted bottom-up by p
	 */
	abstract TreeSet<PowerEntry> findPowers(BigInteger kN, int[] primes, int[] tArray, int primeBaseSize, SieveParams sieveParams);

	/**
	 * Merge primes and powers.
	 * @param primesArray
	 * @param tArray
	 * @param logPArray
	 * @param primeBaseSize
	 * @param powerEntries
	 * @return
	 */
	private BaseArrays mergePrimesAndPowers(int[] primesArray, int[] tArray, byte[] logPArray, int primeBaseSize, TreeSet<PowerEntry> powerEntries) {
		int powerCount = powerEntries.size();
		BaseArrays baseArrays = new BaseArrays(primeBaseSize + powerCount);
		int[] mergedPrimes = baseArrays.primes;
		int[] mergedExponents = baseArrays.exponents;
		int[] mergedPowers = baseArrays.powers;
		int[] mergedTArray = baseArrays.tArray;
		byte[] mergedlogPArray = baseArrays.logPArray;
		
		int mergedIndex = 0;
		int pIndex = 0;
		int p = primesArray[0];
		Iterator<PowerEntry> powerIter = powerEntries.iterator();
		if (powerIter.hasNext()) {
			PowerEntry powerEntry = powerIter.next();
			while (true) {
				if (p < powerEntry.power) {
					// add p
					mergedPrimes[mergedIndex] = primesArray[pIndex];
					mergedExponents[mergedIndex] = 1;
					mergedPowers[mergedIndex] = primesArray[pIndex];
					mergedTArray[mergedIndex] = tArray[pIndex];
					mergedlogPArray[mergedIndex] = logPArray[pIndex];
					mergedIndex++;
					// get next p
					pIndex++;
					if (pIndex < primeBaseSize) {
						p = primesArray[pIndex];
					} else {
						// there are no powers > pMax -> we are done
						break;
					}
				} else {
					// add power
					mergedPrimes[mergedIndex] = powerEntry.p;
					mergedExponents[mergedIndex] = powerEntry.exponent;
					mergedPowers[mergedIndex] = powerEntry.power;
					mergedTArray[mergedIndex] = powerEntry.t;
					mergedlogPArray[mergedIndex] = powerEntry.logPower;
					mergedIndex++;
					// get next power
					if (powerIter.hasNext()) {
						powerEntry = powerIter.next();
					} else {
						// exit; the last primes are added below
						break;
					}
				}
			}
		}
		// add last primes
		for (; pIndex<primeBaseSize; pIndex++, mergedIndex++) {
			mergedPrimes[mergedIndex] = primesArray[pIndex];
			mergedExponents[mergedIndex] = 1;
			mergedPowers[mergedIndex] = primesArray[pIndex];
			mergedTArray[mergedIndex] = tArray[pIndex];
			mergedlogPArray[mergedIndex] = logPArray[pIndex];
		}
//		if (DEBUG) {
//			LOG.debug("#primes = " + primeBaseSize + ", #powers = " + powerCount);
//			assertEquals(primeBaseSize + powerCount, mergedIndex);
//		}
		return baseArrays;
	}
}
