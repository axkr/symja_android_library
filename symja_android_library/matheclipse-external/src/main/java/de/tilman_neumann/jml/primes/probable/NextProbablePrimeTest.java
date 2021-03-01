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
package de.tilman_neumann.jml.primes.probable;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.primes.probable.BPSWTest;
import de.tilman_neumann.util.ConfigUtil;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

/**
 * Performance test of nextProbablePrime() implementations.
 * 
 * @author Tilman Neumann
 */
public class NextProbablePrimeTest {
	private static final Logger LOG = Logger.getLogger(NextProbablePrimeTest.class);
	private static final SecureRandom RNG = new SecureRandom();
	
	/** Number of test numbers for the performance test. */
	private static final int NCOUNT = 1000;

	private static final BPSWTest bpsw = new BPSWTest();
	
	/**
	 * Verify that the corrected lower/upper integers of sqrt(N) are computed.
	 */
	@SuppressWarnings("unused")
	private static void testCorrectness() {
		for (int nBits = 20; ; nBits+=10) {
			LOG.info("Test correctness of " + NCOUNT + " N with " + nBits + " bits:");
			int i = 0;
			while (i < NCOUNT) {
				BigInteger n = new BigInteger(nBits, RNG);
				if (n.equals(I_0)) continue; // exclude 0 from test set
				
				// supposed correct value:
				BigInteger nextProbablePrime = n.nextProbablePrime();
				
				// check others
				try {
					BigInteger nextProbablePrime_bpsw = bpsw.nextProbablePrime(n);
//					assertEquals(nextProbablePrime, nextProbablePrime_bpsw);
				} catch (AssertionError ae) {
					LOG.error("Failure at n=" + n + ": " + ae, ae);
				}
				i++;
			}
			LOG.info("    Tested " + NCOUNT + " next probable primes...");
		}
	}

	/**
	 * Performance test.
	 * Each algorithm is test with the same NCOUNT test numbers in random order.
	 */
	private static void testPerformance() {
		for (int nBits = 20; ; nBits+=10) {
			LOG.info("Test performance of " + NCOUNT + " N with " + nBits + " bits:");
			// create test set with NCOUNT n having nBits bits.
			int i = 0;
			BigInteger[] testSet = new BigInteger[NCOUNT];
			while (i < NCOUNT) {
				testSet[i] = new BigInteger(nBits, RNG);
				if (testSet[i].signum()>0 && testSet[i].bitLength()==nBits) i++; // exclude 0 from test set, assure correct bit size
			}

			long startMillis, duration;
			TreeMap<Long, List<String>> duration_2_algLists = new TreeMap<Long, List<String>>();

			// test BPSW
			startMillis = System.currentTimeMillis();
			for (BigInteger n : testSet) {
				bpsw.nextProbablePrime(n);
			}
			duration = System.currentTimeMillis() - startMillis;
			addToMap(duration_2_algLists, duration, "BPSW");

			// test built-in method
			startMillis = System.currentTimeMillis();
			for (BigInteger n : testSet) {
				@SuppressWarnings("unused")
				BigInteger result = n.nextProbablePrime();
			}
			duration = System.currentTimeMillis() - startMillis;
			addToMap(duration_2_algLists, duration, "Java");

			// results for nBits
			logMap(duration_2_algLists);
		}
	}
	
	private static void addToMap(TreeMap<Long, List<String>> duration_2_algLists, Long duration, String algStr) {
		List<String> algList = duration_2_algLists.get(duration);
		if (algList==null) algList = new ArrayList<String>();
		algList.add(algStr);
		duration_2_algLists.put(duration, algList);
	}
	
	private static void logMap(TreeMap<Long, List<String>> duration_2_algLists) {
		int rank = 1;
		for (long duration : duration_2_algLists.keySet()) {
			int count=0;
			List<String> algList = duration_2_algLists.get(duration);
			for (String algStr : algList) {
				LOG.info("#"+rank + ": " + algStr + " took " + duration + "ms");
				count++;
			}
			rank += count;
		}
	}
	
	/**
	 * Stand-alone test.
	 * @param args ignored
	 */
	public static void main(String[] args) {
		ConfigUtil.initProject(); // set up logger
		//testCorrectness();
		testPerformance();
	}
}
