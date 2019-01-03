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
package de.tilman_neumann.jml.factor.siqs.poly.baseFilter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeSet;

//import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.siqs.data.BaseArrays;
import de.tilman_neumann.jml.factor.siqs.data.SolutionArrays;

/**
 * BaseFilter that removes the q-values of the a-parameter and their powers from the base to sieve with,
 * plus the p that divide k and their powers.
 * @author Tilman Neumann
 */
public class BaseFilter_qk implements BaseFilter {
//	private static final Logger LOG = Logger.getLogger(BaseFilter_qk.class);
//	private static final boolean DEBUG = false;

	@Override
	public Result filter(SolutionArrays solutionArrays, BaseArrays baseArrays, int mergedBaseSize, int[] qArray, int qCount, int k) {
		int[] mergedPrimes = baseArrays.primes;
		int[] mergedExponents = baseArrays.exponents;
		
		int[] filteredPrimes = solutionArrays.primes;
		int[] filteredExponents = solutionArrays.exponents;
		int[] filteredPowers = solutionArrays.powers;
		int[] filteredTArray = solutionArrays.tArray;
		byte[] filteredLogPArray = solutionArrays.logPArray;
		
		int filteredOutCount = 0;
		int lastqIndex = -1;
		
		// Collect q in a hash set to permit fast filtering of powers of q
		HashSet<Integer> qSet = new HashSet<Integer>();
		for (int q : qArray) {
			qSet.add(q);
		}
		
		TreeSet<Integer> filteredOutBaseElementsSet = new TreeSet<Integer>();
		for (int i=0; i<mergedBaseSize; i++) {
			int p = mergedPrimes[i];
			if (qSet.contains(p) || k%p==0) {
				// power is a power of some q or of a p that divides k -> exclude
				int srcPos = lastqIndex + 1;
				int destPos = srcPos - filteredOutCount;
				int length = i - lastqIndex - 1;
				System.arraycopy(mergedPrimes, srcPos, filteredPrimes, destPos, length);
				System.arraycopy(mergedExponents, srcPos, filteredExponents, destPos, length);
				System.arraycopy(baseArrays.powers, srcPos, filteredPowers, destPos, length);
				System.arraycopy(baseArrays.tArray, srcPos, filteredTArray, destPos, length);
				System.arraycopy(baseArrays.logPArray, srcPos, filteredLogPArray, destPos, length);
				lastqIndex = i;
				if (mergedExponents[i] == 1) {
					filteredOutBaseElementsSet.add(p); // XXX also add powers ?
				}
				filteredOutCount++;
			}
		}
		// last piece
		if (lastqIndex < mergedBaseSize-1) {
			int srcPos = lastqIndex + 1;
			int destPos = srcPos - filteredOutCount;
			int length = mergedBaseSize-lastqIndex-1;
			System.arraycopy(mergedPrimes, srcPos, filteredPrimes, destPos, length);
			System.arraycopy(mergedExponents, srcPos, filteredExponents, destPos, length);
			System.arraycopy(baseArrays.powers, srcPos, filteredPowers, destPos, length);
			System.arraycopy(baseArrays.tArray, srcPos, filteredTArray, destPos, length);
			System.arraycopy(baseArrays.logPArray, srcPos, filteredLogPArray, destPos, length);
		}
//		if (DEBUG) {
//			// qArray[] entries must be sorted bottom up
//			LOG.debug("qArray = " + Arrays.toString(qArray));
//			LOG.debug("mergedPrimes = " + Arrays.toString(mergedPrimes));
//			LOG.debug("mergedPowers = " + Arrays.toString(baseArrays.powers));
//			LOG.debug("filteredPrimes = " + Arrays.toString(filteredPrimes));
//		}
		
		// convert Set to int[]
		int[] filteredOutBaseElements = new int[filteredOutBaseElementsSet.size()];
		int i=0;
		for (Integer filteredOutBaseElement : filteredOutBaseElementsSet) {
			filteredOutBaseElements[i++] = filteredOutBaseElement.intValue();
		}
		
		// return results
		int filteredBaseSize = mergedBaseSize - filteredOutCount;
		return new Result(solutionArrays, filteredBaseSize, filteredOutBaseElements);
	}
	
	@Override
	public String getName() {
		return "qkFilter";
	}
}
