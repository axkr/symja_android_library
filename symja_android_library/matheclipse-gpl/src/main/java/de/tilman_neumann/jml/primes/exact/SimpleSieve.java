/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2019-2024 Tilman Neumann - tilman.neumann@web.de
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
package de.tilman_neumann.jml.primes.exact;

/**
 * Monolithic sieve of Eratosthenes, working only for limits < Integer.MAX_VALUE = 2^31 - 1.
 * Used for quality tests only.
 * 
 * @author Tilman Neumann
 */
public class SimpleSieve {
	private SieveCallback clientCallback;

	public SimpleSieve(SieveCallback clientCallback) {
		this.clientCallback = clientCallback;
	}
	
	/**
	 * Generate primes.
	 * @param limit0 biggest number to test for prime
	 */
	public void sieve(long limit0) {
		// small primes not delivered by the sieve below
		clientCallback.processPrime(2);
		
		if (limit0 > Integer.MAX_VALUE) throw new IllegalArgumentException("limit " + limit0 + " exceeds Integer.MAX_VALUE = " + Integer.MAX_VALUE);
		int limit = (int) limit0;

		// Sieve
		boolean[] isComposite = new boolean[limit+1]; // initialized with false
		for (int i=2; i*i <= limit; i++) {
			if (!isComposite[i]) {
				for (int j = i*i; j <= limit; j+=i) {
					isComposite[j] = true;
				}
			}
		}
			
		// Collect
		int n = 3;
		for ( ; n<=limit; n+=2) {
			if (!isComposite[n]) {
				clientCallback.processPrime(n);
			}
		}
	}
}
