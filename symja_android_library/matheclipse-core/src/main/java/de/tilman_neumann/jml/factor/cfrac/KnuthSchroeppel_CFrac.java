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
package de.tilman_neumann.jml.factor.cfrac;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;

//import org.apache.log4j.Logger;

import de.tilman_neumann.jml.modular.JacobiSymbol;
import de.tilman_neumann.jml.primes.exact.AutoExpandingPrimesArray;
import de.tilman_neumann.jml.sequence.SquarefreeSequence;

/**
 * Computation of Knuth-Schroeppel multipliers for CFrac following
 * [Pomerance 1983: "Implementation of the continued fraction integer factoring algorithm"].
 * 
 * @author Tilman Neumann
 */
public class KnuthSchroeppel_CFrac {
	
//	private static final Logger LOG = Logger.getLogger(KnuthSchroeppel_CFrac.class);
//	private final static boolean DEBUG = false;

	private static final double ONE_BY_3 = 1.0/3.0 * Math.log(2);
	private static final double TWO_BY_3 = 2.0/3.0 * Math.log(2);
	private static final double FOUR_BY_3 = 4.0/3.0 * Math.log(2);

	// 10000 primes would always be enough, but using the expandable sieve is nicer
	private AutoExpandingPrimesArray primesArray = AutoExpandingPrimesArray.get();
	private SquarefreeSequence squareFreeSequence = new SquarefreeSequence(1);

	private int[] kArray = new int[10000];
	private BigInteger[] kNArray = new BigInteger[10000];
	private double[] fArray = new double[10000];
	private int[] primeCountArray = new int[10000];

	private JacobiSymbol jacobiEngine = new JacobiSymbol();

	public TreeMap<Double, Integer> computeMultiplier(BigInteger N, int ks_adjust) {
		// The parameters have been chosen experimentally as a tradeoff between the runtime
		// of the Knuth-Schroeppel algorithm and the quality of the k-evaluation.
		int NBits = N.bitLength();
		float logNBits = (float) Math.log(NBits);
		int wantedPrimeCount = (int) (NBits*logNBits/ks_adjust);
		int kMax = wantedPrimeCount;
//		if (DEBUG) LOG.debug("wantedPrimeCount = " + wantedPrimeCount + ", kMax = " + kMax);
		double penaltyMult = 0.35; // 0.35 is better than 0.5 proposed by literature

		// f(k, N) is the score for k that we wish to maximize.
		// initialize f(k, N) with the p=2 case and a bad penalty for big k:
		squareFreeSequence.reset(); // start at k=1
		int kCount = 0;		
		while (true) {
			BigInteger k_big = squareFreeSequence.next();
			int k = k_big.intValue();
			if ((k&1) == 0) continue;
			if (k > kMax) break;
			// now k is odd and square-free and not too big
			BigInteger kN = N.multiply(k_big);
			int kNMod8 = kN.intValue() & 7; // we need only the lowest three bits // (m.intValue() & 7) != 1

			double f;
			if (kNMod8==1) {
				f = FOUR_BY_3 - penaltyMult * Math.log(k);
			} else if (kNMod8==5) {
				f = TWO_BY_3  - penaltyMult * Math.log(k);
			} else { // kN == 3, 7 (mod 8)
				f = ONE_BY_3  - penaltyMult * Math.log(k);
			}
			// store
			kArray[kCount] = k;
			kNArray[kCount] = kN;
			fArray[kCount] = f;
			primeCountArray[kCount++] = 1; // p=2
		}
		// Analysis: kCount ~ 0.406 * kMax
		
		// From the following list we will remove the indices of any k that have been completely evaluated.
		// ArrayList performs better than LinkedList, even with that many remove() operations.
		ArrayList<Integer> kIndexList = new ArrayList<Integer>(kCount);
		for (int kIndex=0; kIndex<kCount; kIndex++) kIndexList.add(kIndex);
		
		// target: map of f-values to k, sorted by f-values top-down
		TreeMap<Double, Integer> fValues2k = new TreeMap<>(Collections.reverseOrder());
		
		// odd primes
		int i=1;
		for (; ; i++) {
			int p = primesArray.getPrime(i);
			double lnPTerm1 = Math.log(p) / (double) (p+1);
			double lnPTerm2 = (p<<1)*lnPTerm1 / (double) (p-1); // 2*p*ln(p) / (p^2-1)
			int Legendre_N_p = jacobiEngine.jacobiSymbol(N, p);
			Iterator<Integer> kIndexIter = kIndexList.iterator();
			while (kIndexIter.hasNext()) {
				int kIndex = kIndexIter.next();
				int k = kArray[kIndex];
            	if (k % p == 0) {
            		// p divides k
            		fArray[kIndex] += lnPTerm1;
            		primeCountArray[kIndex]++;
            	} else if (jacobiEngine.jacobiSymbol(k, p) * Legendre_N_p == 1) {
            		// kN is quadratic residue (mod p)
            		// The cost of computing Legendre(kN|p) dominates the algorithm.
            		// Here we use Legendre(kN|p) = Legendre(k|p)*Legendre(N|p) with precomputed Legendre(N|p) to improve performance
            		fArray[kIndex] += lnPTerm2;
            		primeCountArray[kIndex]++;
            	} // else: p is not part of the resulting prime base -> skip
            	
            	// if we are done with some k then remove its kIndex from the list
				if (primeCountArray[kIndex] == wantedPrimeCount) {
					fValues2k.put(fArray[kIndex], k);
					kIndexIter.remove();
				}
	        } // end for (k)
			
			// are we done with all k ?
			if (kIndexList.isEmpty()) break;
		} // end for (odd primes)
		
//		if (DEBUG) {
//			LOG.debug("kCount = " + kCount + ": required number of primes = " + (i+1));
//			// Analysis: required number of primes < 6*kCount
//			LOG.debug("best k = " + fValues2k.firstEntry().getValue());
//		}
		
		return fValues2k;
	}
}
