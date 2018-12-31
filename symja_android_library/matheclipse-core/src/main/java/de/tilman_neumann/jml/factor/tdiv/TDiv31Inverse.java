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
package de.tilman_neumann.jml.factor.tdiv;

import java.math.BigInteger;

import de.tilman_neumann.jml.factor.FactorAlgorithmBase;

/**
 * Trial division factor algorithm replacing division by multiplications.
 * 
 * Instead of dividing N by consecutive primes, we store the reciprocals of those primes, too,
 * and multiply N by those reciprocals. Only if such a result is near to an integer we need
 * to do a division.
 * 
 * Following an idea of Thilo Harich (https://github.com/ThiloHarich/factoring.git).
 * 
 * @author Tilman Neumann
 */
public class TDiv31Inverse extends FactorAlgorithmBase {

	// The allowed discriminator bit size is d <= 53 - bitLength(N/p), thus d<=23 would be safe
	// for any integer N and p>=2. d=10 is the value that performs best, determined by experiment.
	private static final double DISCRIMINATOR = 1.0/(1<<10);

	private static int[] primes;
	private static double[] reciprocals;
	
	public TDiv31Inverse() {
		primes = new int[NUM_PRIMES_FOR_31_BIT_TDIV];
		reciprocals = new double[NUM_PRIMES_FOR_31_BIT_TDIV];
		for (int i=0; i<NUM_PRIMES_FOR_31_BIT_TDIV; i++) {
			int p = SMALL_PRIMES.getPrime(i);
			primes[i] = p;
			reciprocals[i] = 1.0/p;
		}
	}
	
	@Override
	public String getName() {
		return "TDiv31Inverse";
	}

	@Override
	// TODO will not work for N > 31 bit having smallest factor > 15 bit
	public BigInteger findSingleFactor(BigInteger N) {
		return BigInteger.valueOf(findSingleFactor(N.intValue()));
	}
	
	public int findSingleFactor(int N) {
		// if N is odd and composite then the loop runs maximally up to prime = floor(sqrt(N))
		for (int i=0; i<NUM_PRIMES_FOR_31_BIT_TDIV; i++) {
			double prod = N*reciprocals[i];
			if (((int)(prod+DISCRIMINATOR)) - ((int)(prod-DISCRIMINATOR)) == 1) {
				// prod is very near to an integer
				if (N%primes[i]==0) {
					return primes[i];
				}
			}
		}
		// otherwise N is prime!
		throw new IllegalArgumentException("N = " + N + " is prime!");
	}
}
