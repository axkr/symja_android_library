/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018-2024 Tilman Neumann - tilman.neumann@web.de
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
package de.tilman_neumann.jml.factor.base;

import java.math.BigInteger;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.modular.JacobiSymbol;
import de.tilman_neumann.jml.primes.exact.AutoExpandingPrimesArray;
import de.tilman_neumann.util.Ensure;

/**
 * Prime base generator.
 * Creates prime bases consisting of the 2 and odd primes with Legendre(kN|p)>=0, i.e. such that kN is not a non-residue (mod p).
 * Instead of the Legendre symbol, the faster Jacobi symbol is computed.
 * 
 * @author Tilman Neumann
 */
public class PrimeBaseGenerator {
	private static final Logger LOG = LogManager.getLogger(PrimeBaseGenerator.class);
	private static final boolean DEBUG = false;
	
	private AutoExpandingPrimesArray rawPrimesArray = AutoExpandingPrimesArray.get();
	private JacobiSymbol jacobiEngine = new JacobiSymbol();

	/**
	 * Compute a reduced prime base containing the 2 and odd primes p with Jacobi(kN|p)>=0
	 * 
	 * @param kN has to be a quadratic residue modulo all p
	 * @param primeBaseSize the wanted number of primes
	 * @param primesArray is filled with the primes p satisfying Jacobi(kN|p)>=0
	 */
	public void computeReducedPrimeBase(BigInteger kN, int primeBaseSize, int[] primesArray) {
		// the 2 is always added
		primesArray[0] = 2;
		
		// odd primes
		int count = 1;
		for (int i=1; ; i++) {
			int p = rawPrimesArray.getPrime(i);
			int jacobi = jacobiEngine.jacobiSymbol(kN, p);
			if (DEBUG) {
				// ensure correctness of prime generator
				Ensure.ensureTrue(BigInteger.valueOf(p).isProbablePrime(20));
				// ensure that Jacobi symbol values are in the expected range -1 ... +1
				if (jacobi<-1 || jacobi>1) LOG.warn("kN=" + kN + ", p=" + p + " -> jacobi=" + jacobi);
			}
			// Q(x) = A(x)^2 - kN can only be divisible by p with Legendre(kN|p) >= 0.
			// It is important to add p with Legendre(kN|p) == 0, too! Otherwise we would not find such p during trial division.
			// On the other hand, doing a factor test here in that case makes no sense,
			// because it is typically caused by k, and we will find such factors during trial division anyway.
			if (jacobi>=0) {
				// kN is a quadratic residue mod p (or not coprime)
				primesArray[count] = p;
				// if not null, then fill primesArray_big, too
				if (++count == primeBaseSize) break;
			}
		}
	}
}
