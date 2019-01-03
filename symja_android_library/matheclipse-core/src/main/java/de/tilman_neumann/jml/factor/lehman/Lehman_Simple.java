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
package de.tilman_neumann.jml.factor.lehman;

import java.math.BigInteger;

import de.tilman_neumann.jml.gcd.Gcd63;
import de.tilman_neumann.jml.factor.FactorAlgorithmBase;

/**
 * Simple implementation of Lehmans factor algorithm following https://programmingpraxis.com/2017/08/22/lehmans-factoring-algorithm/.
 * 
 * This implementation is pretty slow, but useful for illustrating the basic algorithm.
 * It may fail for N>=47 bit where 4*k*N produces a long-overflow.
 *
 * @author Tilman Neumann
 */
public class Lehman_Simple extends FactorAlgorithmBase {
	private final Gcd63 gcdEngine = new Gcd63();
	
	@Override
	public String getName() {
		return "Lehman_Simple";
	}

	@Override
	public BigInteger findSingleFactor(BigInteger N) {
		return BigInteger.valueOf(findSingleFactor(N.longValue()));
	}
	
	public long findSingleFactor(long N) {
		// 1. Check via trial division whether N has a nontrivial divisor d <= cbrt(N), and if so, return d.
		int cbrt = (int) Math.ceil(Math.cbrt(N));
		int i=0, p;
		while ((p = SMALL_PRIMES.getPrime(i++)) <= cbrt) {
			if (N%p==0) return p;
		}
		
		// 2. Main loop
		double sixthRoot = Math.pow(N, 1/6.0); // double precision is required for stability
		for (int k=1; k <= cbrt; k++) {
			long fourKN = k*N<<2; // long overflow possible for N>=47 bit
			double fourSqrtK = Math.sqrt(k<<4);
			long sqrt4kN = (long) Math.ceil(Math.sqrt(fourKN)); // ceil() is required for stability
			long limit = (long) (sqrt4kN + sixthRoot / fourSqrtK);
			for (long a = sqrt4kN; a <= limit; a++) {
				long test = a*a - fourKN;
				long b = (long) Math.sqrt(test);
				if (b*b == test) {
					long gcd = gcdEngine.gcd(a+b, N);
					if (gcd>1 && gcd<N) return gcd;
				}
			}
	    }
		
		return 0; // Fail
	}
}
