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

//import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.siqs.data.BaseArrays;
import de.tilman_neumann.jml.factor.siqs.data.SolutionArrays;

/**
 * Alternative implementation of a BaseFilter that removes the q-values of the a-parameter and their powers from the base to sieve with.
 * @author Tilman Neumann
 */
public class BaseFilter_q2 implements BaseFilter {
//	private static final Logger LOG = Logger.getLogger(BaseFilter_q2.class);
//	private static final boolean DEBUG = false;

	@Override
	public Result filter(SolutionArrays solutionArrays, BaseArrays baseArrays, int mergedBaseSize, int[] qArray, int qCount, int k) {
		int[] mergedPrimes = baseArrays.primes;
		int[] mergedExponents = baseArrays.exponents;
		int[] mergedPowers = baseArrays.powers;
		int[] mergedTArray = baseArrays.tArray;
		byte[] mergedlogPArray = baseArrays.logPArray;
		
		int[] filteredPrimes = solutionArrays.primes;
		int[] filteredExponents = solutionArrays.exponents;
		int[] filteredPowers = solutionArrays.powers;
		int[] filteredTArray = solutionArrays.tArray;
		byte[] filteredLogPArray = solutionArrays.logPArray;
		
		// Collect q in a hash set to permit fast filtering of powers of q
		HashSet<Integer> qSet = new HashSet<Integer>();
		for (int q : qArray) {
			qSet.add(q);
		}
		
		int filteredBaseSize = 0;
		for (int i=0; i<mergedBaseSize; i++) {
			int p = mergedPrimes[i];
			if (!qSet.contains(p)) {
				filteredPrimes[filteredBaseSize] = mergedPrimes[i];
				filteredExponents[filteredBaseSize] = mergedExponents[i];
				filteredPowers[filteredBaseSize] = mergedPowers[i];
				filteredTArray[filteredBaseSize] = mergedTArray[i];
				filteredLogPArray[filteredBaseSize] = mergedlogPArray[i];
				filteredBaseSize++;
			}
		}
		
//		if (DEBUG) {
//			// qArray[] entries must be sorted bottom up
//			LOG.debug("qArray = " + Arrays.toString(qArray));
//			LOG.debug("mergedPrimes = " + Arrays.toString(mergedPrimes));
//			LOG.debug("mergedPowers = " + Arrays.toString(mergedPowers));
//			LOG.debug("filteredPrimes = " + Arrays.toString(filteredPrimes));
//		}
		
		return new Result(solutionArrays, filteredBaseSize, qArray);
	}
	
	@Override
	public String getName() {
		return "qFilter2";
	}
}
