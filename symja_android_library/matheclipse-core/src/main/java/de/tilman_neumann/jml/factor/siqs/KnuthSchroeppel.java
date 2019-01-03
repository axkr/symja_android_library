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
package de.tilman_neumann.jml.factor.siqs;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;

//import org.apache.log4j.Logger;

import de.tilman_neumann.jml.modular.JacobiSymbol;
import de.tilman_neumann.jml.primes.exact.AutoExpandingPrimesArray;
import de.tilman_neumann.jml.sequence.SquarefreeSequence63;

/**
 * Computation of the Knuth-Schroeppel multiplier k for the quadratic sieve.
 * 
 * Most often the algorithm finds k with kN == 1 (mod 8), but kN == 3,5,7 (mod 8) are possible, too.
 * Most "best k" are prime, but we also find composite and 1.
 *
 * @see [Pomerance 1985: "The Quadratic Sieve Factoring Algorithm"].
 * @see [Silverman 1987: "The Multiple Polynomial Quadratic Sieve", page 335].
 * 
 * @author Tilman Neumann
 */
public class KnuthSchroeppel {
	
//	private static final Logger LOG = Logger.getLogger(KnuthSchroeppel.class);
//	private final static boolean DEBUG = false;
	
	// The original "benalty" for kN == 1 (mod 8) is 2*ln(2), according to Pomerance.
	// Here it is adjusted for using polynomial Q2(x)=(2*a*x + b)^2 - kN when kN == 1 (mod 8).
	// The additional 0.2 was estimated from a scatter plot of f(k1)-f(k*) vs. runtime(k1)-runtime(k*),
	// where k1 is the best k with kN == 1 (mod 8) and k* is the best k with kN == 3, 5, 7 (mod 8).
	// I don't know what the theoretically optimal value should be, though.
	private static final double C_1   = 2.0 * Math.log(2) + 0.2;
	private static final double C_5   = 1.0 * Math.log(2);
	private static final double C_3_7 = 0.5 * Math.log(2);
	// Penalty weight: 0.35 is better than 0.5 proposed by literature
	private static final double PENALTY_WEIGHT = 0.35;

	// 10000 primes would always be enough, but using the auto-expanding sieve facade is nicer
	private AutoExpandingPrimesArray primesArray = AutoExpandingPrimesArray.get();
	private SquarefreeSequence63 squareFreeSequence = new SquarefreeSequence63(1);

	private int[] kArray = new int[10000];
	private double[] fArray = new double[10000];
	private int[] primeCountArray = new int[10000];

	private JacobiSymbol jacobiEngine = new JacobiSymbol();
	
	/**
	 * Compute Knuth-Schroeppel multiplier k for N.
	 * @param N
	 * @return k
	 */
	public int computeMultiplier(BigInteger N) {
		// Parameters have been chosen experimentally as a tradeoff between the runtime
		// of the Knuth-Schroeppel algorithm and the quality of the k-evaluation.
		// 195 bits is the transition point between the small N and large N optimizations.
		int NBits = N.bitLength();
		int wantedPrimeCount = NBits < 195 ? (int) (Math.pow(2, 3.39+0.036*(NBits-50))) : 2*NBits; // the number of primes that would be part of the prime base to be tested
		int kMax = wantedPrimeCount; // the maximum value of k to consider
		
		// f(k, N) is the score for k that we wish to maximize.
		// initialize f(k, N) with the p=2 case and a bad penalty for big k:
		squareFreeSequence.reset(); // start at k=1
		int kCount = 0;		
		while (true) {
			int k = squareFreeSequence.next().intValue();
			if ((k&1) == 0) continue;
			if (k > kMax) break;
			// now k is odd and square-free and not too big
			BigInteger kN = N.multiply(BigInteger.valueOf(k));
			int kNMod8 = kN.intValue() & 7; // for the modulus we need only the lowest three bits
			
			double f;
			if (kNMod8==1) {
				f = C_1   - PENALTY_WEIGHT * Math.log(k);
			} else if (kNMod8==5) {
				f = C_5   - PENALTY_WEIGHT * Math.log(k);
			} else { // kN == 3, 7 (mod 8)
				f = C_3_7 - PENALTY_WEIGHT * Math.log(k);
			}
			// store
			kArray[kCount] = k;
			fArray[kCount] = f;
			primeCountArray[kCount++] = 1; // p=2
		}
		// Analysis: kCount ~ 0.406 * kMax
		
		// From the following list we will remove the indices of any k that have been completely evaluated.
		// ArrayList performs better than LinkedList, even with that many remove() operations.
		ArrayList<Integer> kIndexList = new ArrayList<Integer>(kCount);
		for (int kIndex=0; kIndex<kCount; kIndex++) kIndexList.add(kIndex);
		
		// add odd primes contribution
		int i=1;
		for (; ; i++) {
			int p = primesArray.getPrime(i);
			double lnP = Math.log(p);
			double lnPTerm1 = lnP / (double) p;
			double lnPTerm2 = 2*lnP / (double) (p-1);
			int Legendre_N_p = jacobiEngine.jacobiSymbol(N, p);
			Iterator<Integer> kIndexIter = kIndexList.iterator();
			while (kIndexIter.hasNext()) {
				int kIndex = kIndexIter.next();
				int k = kArray[kIndex];
				if (k % p == 0) {
            		// p divides k -> only one x-solution.
            		fArray[kIndex] += lnPTerm1;
            		primeCountArray[kIndex]++;
				} else if (jacobiEngine.jacobiSymbol(k, p) * Legendre_N_p == 1) {
            		// kN is quadratic residue (mod p) -> 2 x-solutions.
            		// The cost of computing Legendre(kN|p) dominates the algorithm.
            		// Here we use Legendre(kN|p) = Legendre(k|p)*Legendre(N|p) with precomputed Legendre(N|p) to improve performance
            		fArray[kIndex] += lnPTerm2;
            		primeCountArray[kIndex]++;
            	} // else: p is not part of the resulting prime base -> skip
            	
            	// if we are done with some k then remove its kIndex from the list
				if (primeCountArray[kIndex] == wantedPrimeCount) kIndexIter.remove();
	        } // end for (k)
			
			// are we done with all k ?
			if (kIndexList.isEmpty()) break;
		} // end for (odd primes)
//		if (DEBUG) LOG.debug("kCount = " + kCount + ": required number of primes = " + (i+1));
		// Analysis: required number of primes < 6*kCount
		
		// now we have evaluated f(k, N) for all k. pick the one that maximizes f [Pomerance 1985]
		int best_k = 1;
		double best_f = Double.MIN_VALUE;
		for (int kIndex=0; kIndex<kCount; kIndex++) {
			if (fArray[kIndex] > best_f) {
				best_f = fArray[kIndex];
				best_k = kArray[kIndex];
	        }
			//LOG.debug("f(k=" + k + ") = " + f + ", best f = " + best_f + " at k=" + best_k);
	    }
		//LOG.debug("Knuth-Schroeppel multiplier k = " + best_k);
		return best_k;
	}
}
