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
package de.tilman_neumann.jml.factor.pollardRho;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.base.Uint128;
import de.tilman_neumann.jml.factor.FactorAlgorithmBase;
import de.tilman_neumann.jml.gcd.Gcd63;
//import de.tilman_neumann.util.ConfigUtil;
import de.tilman_neumann.util.SortedMultiset;

/**
 * Brents's improvement of Pollard's Rho algorithm using Montgomery multiplication.
 * 
 * The main reason why Montgomery multiplication is helpful for Pollard-Rho is that
 * no conversions to/from Montgomery form are required.
 * 
 * In this implementation I managed to use the Montgomery reducer R=2^64, which simplifies
 * the Montgomery multiplication a good deal.
 * 
 * @see [Richard P. Brent: An improved Monte Carlo Factorization Algorithm, 1980]
 * @see [http://projecteuler.chat/viewtopic.php?t=3776]
 * @see [http://coliru.stacked-crooked.com/a/f57f11426d06acd8]
 * 
 * @author Tilman Neumann
 */
public class PollardRhoBrentMontgomery64 extends FactorAlgorithmBase {
	private static final Logger LOG = Logger.getLogger(PollardRhoBrentMontgomery64.class);
	private static final boolean DEBUG = false;

	private static final SecureRandom RNG = new SecureRandom();

	// The reducer R is 2^64, but the only constant still required is the half of it.
	private static final long R_HALF = 1L << 63;

	private long N;

	private long minusNInvModR;	// (-1/N) mod R, required for Montgomery multiplication
	
	private Gcd63 gcd = new Gcd63();

	@Override
	public String getName() {
		return "PollardRhoBrentMontgomery64";
	}
	
	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		return BigInteger.valueOf(findSingleFactor(N.longValue()));
	}
	
	public long findSingleFactor(long N) {
		this.N = N;
        long G, x, ys;
        
		setUpMontgomeryMult();

		// number of iterations before gcd tests.
        // Brent: "The probability of the algorithm failing because q_i=0 increases, so it is best not to choose m too large"
    	final int m = 100;

        do {
	        // start with random x0, c from [0, N-1]
        	long c = Math.abs(RNG.nextLong()) % N;
            long x0 = Math.abs(RNG.nextLong()) % N;
            long y = x0;

        	int r = 1;
        	long q = 1;
        	do {
	    	    x = y;
	    	    for (int i=1; i<=r; i++) {
	    	        y = addModN(montgomeryMult(y, y), c);
	    	    }
	    	    int k = 0;
	    	    do {
	    	        ys = y;
	    	        final int iMax = Math.min(m, r-k);
	    	        for (int i=1; i<=iMax; i++) {
	    	            y = addModN(montgomeryMult(y, y), c);
	    	            final long diff = x<y ? y-x : x-y;
	    	            q = montgomeryMult(diff, q);
	    	        }
	    	        G = gcd.gcd(q, N);
	    	        // if q==0 then G==N -> the loop will be left and restarted with new x0, c
	    	        k += m;
		    	    //LOG.info("r = " + r + ", k = " + k);
	    	    } while (k<r && G==1);
	    	    r <<= 1;
	    	    //LOG.info("r = " + r + ", G = " + G);
	    	} while (G==1);
	    	if (G==N) {
	    	    do {
	    	        ys = addModN(montgomeryMult(ys, ys), c);
    	            final long diff = x<ys ? ys-x : x-ys;
	    	        G = gcd.gcd(diff, N);
	    	    } while (G==1);
	    	    //LOG.info("G = " + G);
	    	}
        } while (G==N);
		//LOG.debug("Found factor of " + N + " = " + factor);
        return G;
	}
	
	/**
	 * Finds (1/R) mod N and (-1/N) mod R for odd N and R=2^64.
	 * 
	 * As before, EEA63 would not work for R=2^64, but with a minor modification
	 * the algorithm from http://coliru.stacked-crooked.com/a/f57f11426d06acd8
	 * still works for R=2^64.
	 */
	private void setUpMontgomeryMult() {
		// initialization
	    long a = R_HALF;
	    long u = 1;
	    long v = 0;
	    
	    while (a != 0) { // modification
	        a >>>= 1;
	        if ((u & 1) == 0) {
	            u >>>= 1;
	    	    v >>>= 1;
	        } else {
	            u = ((u ^ N) >>> 1) + (u & N);
	            v = (v >>> 1) + R_HALF;
	        }
	    }

	    // u = (1/R) mod N and v = (-1/N) mod R. We only need the latter.
	    minusNInvModR = v;
	}

	/**
	 * Montgomery multiplication modulo N, using reducer R=2^64.
	 * Inputs and output are in Montgomery form and in the range [0, N).
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private long montgomeryMult(final long a, final long b) {
		// Step 1: Compute a*b
		Uint128 ab = Uint128.mul64(a, b);
		// Step 2: Compute t = ab * (-1/N) mod R
		// Since R=2^64, "x mod R" just means to get the low part of x.
		// That would give t = Uint128.mul64(ab.getLow(), minusNInvModR).getLow();
		// but even better, the long product just gives the low part -> we can get rid of one expensive mul64().
		long t = ab.getLow() * minusNInvModR;
		// Step 3: Compute reduced = (a*b + t*N) / R
		// Since R=2^64, "x / R" just means to get the high part of x.
		long reduced = ab.add(Uint128.mul64(t, N)).getHigh();
		long result = reduced<N ? reduced : reduced-N;
		
//		if (DEBUG) {
//			//LOG.debug(a + " * " + b + " = " + result);
//			assertTrue(a >= 0 && a<N);
//			assertTrue(b >= 0 && b<N);
//			assertTrue(result >= 0 && result < N);
//		}
		
		return result;
	}

	/**
	 * Addition modulo N, with <code>a, b < N</code>.
	 * @param a
	 * @param b
	 * @return (a+b) mod N
	 */
	private long addModN(long a, long b) {
		long sum = a+b;
		return sum<N ? sum : sum-N;
	}
	
	/**
	 * Test.
	 * Test numbers:
	 * 3225275494496681 (52 bits) = 56791489 * 56791529
	 * 322527333642009919 (59 bits) = 567914891 * 567914909
	 * 3225273260887418687 (62 bits) = 567914891 * 5679148957
	 * 
	 * @param args ignored
	 */
//	public static void main(String[] args) {
//    	ConfigUtil.initProject();
//    	
//		while(true) {
//			BigInteger n;
//			try {
//				LOG.info("Please insert the integer to factor:");
//				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//				String line = in.readLine();
//				String input = line.trim();
//				n = new BigInteger(input);
//				LOG.debug("factoring " + input + " (" + n.bitLength() + " bits) ...");
//			} catch (IOException ioe) {
//				LOG.error("io-error occuring on input: " + ioe.getMessage());
//				continue;
//			}
//			
//			long start = System.currentTimeMillis();
//			SortedMultiset<BigInteger> result = new PollardRhoBrentMontgomery64().factor(n);
//			LOG.info("Factored " + n + " = " + result.toString() + " in " + (System.currentTimeMillis()-start) + " ms");
//
//		} // next input...
//	}
}
