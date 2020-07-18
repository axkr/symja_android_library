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
package de.tilman_neumann.jml;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Map;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.util.ConfigUtil;
import de.tilman_neumann.util.SortedMultiset;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * Implementations of the Moebius function.
 * 
 * @author Tilman Neumann
 */
public class MoebiusFunction {
	private static final Logger LOG = Logger.getLogger(MoebiusFunction.class);
	
	private MoebiusFunction() {
		// static class, not instantiable
	}
	
	/**
	 * Computes the value of the Moebius function at n.
	 * 
	 * Returns
	 * 1       if n=1,
	 * (-1)^k, if k is the number of distinct prime factors,
	 * 0,      if k has repeated prime factors.
	 * 
	 * @param n Argument
	 * @return moebius(n)
	 */
	public static int moebius(BigInteger n) {
		if (n.signum()<1) {
			throw new IllegalArgumentException("n=" + n + ", but the moebius function supports n>=1 only");
		}
		if (n.equals(I_1)) {
			return 1;
		}
		
		// factorize n:
		SortedMultiset<BigInteger> factors = FactorAlgorithm.DEFAULT.factor(n);
		//LOG.debug("factors of " + n + " = " + factors);
		
		if (factors==null || factors.keyCount()==0) {
			// n is prime
			return -1;
		}
		
		// accumulate number of different primes in k:
		int k = 1;
		for (Map.Entry<BigInteger, Integer> factorAndMultiplicity : factors.entrySet()) {
			final int e = factorAndMultiplicity.getValue().intValue();
			if (e > 1) {
				return 0;
			}
			if (e == 1) {
				k = -k;
			}
		}
		// return (-1)^k:
		return k;
	}

	/**
	 * Tests.
	 * 
	 * @param args Ignored
	 */
	public static void main(String[] args) {
    	ConfigUtil.initProject();
    	int nMax = 100;
		ArrayList<Integer> results = new ArrayList<>();
		for (int n=1; n<nMax; n++) {
			BigInteger bigN = BigInteger.valueOf(n);
			results.add(moebius(bigN));
		}
		LOG.info("Moebius function = " + results);
		// Result: 1, -1, -1, 0, -1, 1, -1, 0, 0, 1, -1, 0, -1, 1, 1, 0, -1, 0, -1, 0, 1, 1, -1, 0, 0, ...
	}
}
