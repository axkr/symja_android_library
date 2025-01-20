/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018-2025 Tilman Neumann - tilman.neumann@web.de
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
package de.tilman_neumann.jml.factor.pollardRho;

import java.math.BigInteger;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.jml.gcd.Gcd31;
import de.tilman_neumann.jml.random.Xorshf32;
import de.tilman_neumann.util.Ensure;

/**
 * Brents's improvement of Pollard's Rho algorithm using Montgomery multiplication.
 * 
 * The main reason why Montgomery multiplication is helpful for Pollard-Rho is that
 * no conversions to/from Montgomery form are required.
 * 
 * In this implementation I managed to use the Montgomery reducer R=2^32, which simplifies
 * the Montgomery multiplication a good deal.
 * 
 * Another small performance improvement stems from using the polynomial x*(x+1) instead of x^2+c,
 * which saves us the addition modulo N after each Montgomery multiplication.
 * 
 * @see [Richard P. Brent: An improved Monte Carlo Factorization Algorithm, 1980]
 * @see [http://projecteuler.chat/viewtopic.php?t=3776]
 * @see [http://coliru.stacked-crooked.com/a/f57f11426d06acd8]
 * 
 * 31/32 bit version.
 * 
 * @author Tilman Neumann
 */
public class PollardRhoBrentMontgomery32 extends FactorAlgorithm {
	private static final Logger LOG = LogManager.getLogger(PollardRhoBrentMontgomery32.class);
	private static final boolean DEBUG = false;

	private static final Xorshf32 RNG = new Xorshf32(); // looks slightly faster than SpRand32 here

	// The reducer R is 2^32, but the only constant still required is the half of it.
	private static final int R_HALF = 1 << 31;

	private int n;

	private int minusNInvModR;	// (-1/N) mod R, required for Montgomery multiplication
	
	private Gcd31 gcd = new Gcd31();

	@Override
	public String getName() {
		return "PollardRhoBrentMontgomery32";
	}
	
	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		if (N.bitLength() > 31) { // this check should be negligible in terms of performance
			throw new IllegalArgumentException("N = " + N + " has " + N.bitLength() + " bit, but " + getName() + " only supports arguments <= 31 bit");
		}
		int factorInt = findSingleFactor(N.intValue());
        return BigInteger.valueOf(factorInt);
	}
	
	public int findSingleFactor(int nOriginal) {
		this.n = nOriginal<0 ? -nOriginal : nOriginal; // RNG.nextInt(n) below would crash for negative arguments
		
		// n==9 would require to check if the gcd is 1 < gcd < n before returning it as a factor
		if (n==9) return 3;
		
        int G, x, ys;
        
		setUpMontgomeryMult();

		// number of iterations before gcd tests.
        // Brent: "The probability of the algorithm failing because q_i=0 increases, so it is best not to choose m too large"
		final int Nbits = 32 - Integer.numberOfLeadingZeros(n);
    	final int m = 2*Nbits;

        do {
	        // start with random y from [0, n)
            int y = RNG.nextInt(n);
            if (DEBUG) Ensure.ensureGreaterEquals(y, 0);
        	int r = 1;
        	int q = 1;
        	do {
	    	    x = y;
	    	    for (int i=r; i>0; i--) {
	    	        y = montMul32(y, y+1, n, minusNInvModR);
	    	    }
	    	    int k = 0;
	    	    do {
	    	        ys = y;
	    	        final int iMax = Math.min(m, r-k);
	    	        for (int i=iMax; i>0; i--) {
	    	            y = montMul32(y, y+1, n, minusNInvModR);
	    	            q = montMul32(y-x, q, n, minusNInvModR);
	    	        }
	    	        G = gcd.gcd(q, n);
	    	        // if q==0 then G==n -> the loop will be left and restarted with new y
	    	        k += m;
	    	        if (DEBUG) LOG.debug("r = " + r + ", k = " + k);
	    	    } while (k<r && G==1);
	    	    r <<= 1;
	    	    if (DEBUG) LOG.debug("r = " + r + ", G = " + G);
	    	} while (G==1);
	    	if (G==n) {
	    	    do {
	    	        ys = montMul32(ys, ys+1, n, minusNInvModR);
	    	        G = gcd.gcd(ys-x, n);
	    	    } while (G==1);
	    	    if (DEBUG) LOG.debug("G = " + G);
	    	}
        } while (G==n);
        if (DEBUG) LOG.debug("Found factor " + G + " of N=" + nOriginal);
        return G;
	}
	
	/**
	 * Finds (1/R) mod N and (-1/N) mod R for odd N and R=2^32.
	 * 
	 * As before, EEA31 would not work for R=2^32, but with a minor modification
	 * the algorithm from http://coliru.stacked-crooked.com/a/f57f11426d06acd8
	 * still works for R=2^32.
	 */
	private void setUpMontgomeryMult() {
		// initialization
	    int a = R_HALF;
	    int u = 1;
	    int v = 0;
	    
	    while (a != 0) { // modification
	        a >>>= 1;
	        if ((u & 1) == 0) {
	            u >>>= 1;
	    	    v >>>= 1;
	        } else {
	            u = ((u ^ n) >>> 1) + (u & n);
	            v = (v >>> 1) + R_HALF;
	        }
	    }

	    // u = (1/R) mod N and v = (-1/N) mod R. We only need the latter.
	    minusNInvModR = v;
	}

	/**
	 * Montgomery multiplication of a*b mod n. ("mulredcx" in Yafu)
	 * @param a
	 * @param b
	 * @param N
	 * @param Nhat complement of N mod 2^32
	 * @return Montgomery multiplication of a*b mod n
	 */
	public static int montMul32(int a, int b, int N, int Nhat) {
		// Step 1: Compute a*b
		long ab = (long)a*b;
		// Step 2: Compute t = ab * (-1/N) mod R
		// Since R=2^32, "x mod R" just means to get the lower int of x.
		// That would give t = (int)((ab & 0xFFFFFFFFL) * Nhat);
		// but even better, the long product just gives the lower int.
		int t = (int)(ab * Nhat);
		// Step 3: Compute r = (a*b + t*N) / R
		// Since R=2^32, "x / R" just means to get the high int of x.
		long tN = (long)t*N;
		int r = (int) ((ab+tN) >>> 32);
		// If the correct result is c, then now r==c or r==c+N.
		// This is fine for this factoring algorithm, because r will 
		// * either be subjected to another Montgomery multiplication mod N,
		// * or to a gcd(r, N), where it doesn't matter if we test gcd(c, N) or gcd(c+N, N).
		
		if (DEBUG) {
			LOG.debug(a + " * " + b + " = " + r);
			// 0 <= a < N
			Ensure.ensureSmallerEquals(0, a);
			Ensure.ensureSmaller(a, N);
			// 0 <= b < N
			Ensure.ensureSmallerEquals(0, b);
			Ensure.ensureSmaller(b, N);
			
			// In a general Montgomery multiplication we would still have to check
			r = r<N ? r : r-N;
			// to satisfy 0 <= r < N
			Ensure.ensureSmallerEquals(0, r);
			Ensure.ensureSmaller(r, N);
		}
		
		return r;
	}
}
