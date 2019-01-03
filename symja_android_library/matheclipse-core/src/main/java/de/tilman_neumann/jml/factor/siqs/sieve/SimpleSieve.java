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
package de.tilman_neumann.jml.factor.siqs.sieve;

import java.util.ArrayList;
import java.util.List;

import de.tilman_neumann.jml.factor.siqs.data.SolutionArrays;
import de.tilman_neumann.util.Timer;

/**
 * Simple non-segmented sieve.
 * 
 * @author Tilman Neumann
 */
public class SimpleSieve implements Sieve {
	/** basic building block for fast zero-initialization of sieve array */
	private static final byte[] ZERO_ARRAY_256 = new byte[256];

	// prime base
	private int primeBaseSize;
	
	private SolutionArrays solutionArrays;

	// sieve
	private int sieveArraySize;
	/** the arrays holding logP sums for all x */
	private byte[] sieveArray_pos = null; // null indicates that allocation is required
	private byte[] sieveArray_neg;
	
	// timings
	private boolean profile;
	private Timer timer = new Timer();
	private long initDuration, sieveDuration, collectDuration;
	
	
	@Override
	public String getName() {
		return "simpleSieve";
	}
	
	@Override
	public void initializeForN(SieveParams sieveParams, int mergedBaseSize, boolean profile) {
		this.sieveArraySize = sieveParams.sieveArraySize;

		// profiling
		this.profile = profile;
		initDuration = sieveDuration = collectDuration = 0;
	}

	@Override
	public void initializeForAParameter(SolutionArrays solutionArrays, int filteredBaseSize) {
		this.solutionArrays = solutionArrays;
		this.primeBaseSize = filteredBaseSize;
	}

	@Override
	public List<Integer> sieve() {
		if (profile) timer.capture();
		this.initializeSieveArray(sieveArraySize);
		if (profile) initDuration += timer.capture();

		final int[] powers = solutionArrays.powers;
		final int[] x1Array = solutionArrays.x1Array;
		final int[] x2Array = solutionArrays.x2Array;
		final byte[] logPArray = solutionArrays.logPArray;

		// sieve with p[0]=2: here we use only solution x1
		//assertEquals(1, ldPArray[0]);
		int x1min = x1Array[0];
		byte logP = logPArray[0];
		for (int x1=x1min; x1<sieveArraySize; x1+=2) {
			sieveArray_pos[x1] += logP;
		}
		// same for the neg.x array
		for (int x1_neg=2-x1min; x1_neg<sieveArraySize; x1_neg+=2) {
			sieveArray_neg[x1_neg] += logP;
		}
		// sieve with odd primes
		for (int i=1; i<primeBaseSize; i++) {
			// solution x1
			x1min = x1Array[i];
			int p = powers[i];
			logP = logPArray[i];
			for (int x1=x1min; x1<sieveArraySize; x1+=p) {
				sieveArray_pos[x1] += logP;
			}
			for (int x1_neg=p-x1min; x1_neg<sieveArraySize; x1_neg+=p) {
				sieveArray_neg[x1_neg] += logP;
			}
			// solution x2: x1 == x2 happens in any of (basic QS, MPQS, SIQS) if p divides k, which implies t=0
			int x2min = x2Array[i];
			if (x2min != x1min) {
				for (int x2=x2min; x2<sieveArraySize; x2+=p) {
					sieveArray_pos[x2] += logP;
				}
				for (int x2_neg=p-x2min; x2_neg<sieveArraySize; x2_neg+=p) {
					sieveArray_neg[x2_neg] += logP;
				}
			} // else x2min==x1min -> do not sieve with the same x twice
		}
		if (profile) sieveDuration += timer.capture();

		// collect results
		List<Integer> smoothXList = new ArrayList<Integer>();
		// let the sieve entry counter x run down to 0 is much faster because of the simpler exit condition
		for (int x=sieveArraySize-1; x>=0; x--) {
			// collect positive x
			if ((sieveArray_pos[x] & 0xFF) > 128) {
				// Q is sufficiently smooth to be passed to trial division!
				smoothXList.add(x);
			}
			// collect negative x
			if ((sieveArray_neg[x] & 0xFF) > 128) {
				// Q(-x) is sufficiently smooth to be passed to trial division!
				smoothXList.add(-x);
			}
		} // end for (x)
		if (profile) collectDuration += timer.capture();
		return smoothXList;
	}

	/**
	 * Initialize the sieve array(s) with zeros.
	 * @param sieveArraySize
	 */
	private void initializeSieveArray(int sieveArraySize) {
		// On the first initialization the array is allocated.
		// The next initializations use System.arraycopy(), which is much faster.
		// The copying procedure requires that sieveArraySize is a multiple of 256.
		if (sieveArray_pos==null) {
			sieveArray_pos = new byte[sieveArraySize];
			sieveArray_neg = new byte[sieveArraySize];
		} else {
			// overwrite existing arrays with zeros. we know that sieve array size is a multiple of 256
			System.arraycopy(ZERO_ARRAY_256, 0, sieveArray_pos, 0, 256);
			int filled = 256;
			int unfilled = sieveArraySize-filled;
			while (unfilled>0) {
				int fillNext = Math.min(unfilled, filled);
				System.arraycopy(sieveArray_pos, 0, sieveArray_pos, filled, fillNext);
				filled += fillNext;
				unfilled = sieveArraySize-filled;
			}
			System.arraycopy(sieveArray_pos, 0, sieveArray_neg, 0, sieveArraySize);
		}
	}

	@Override
	public SieveReport getReport() {
		return new SieveReport(initDuration, sieveDuration, collectDuration);
	}
	
	@Override
	public void cleanUp() {
		sieveArray_pos = null;
		sieveArray_neg = null;
	}
}
