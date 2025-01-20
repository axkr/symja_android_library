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
package de.tilman_neumann.jml.partitions;

import java.math.BigInteger;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.util.SortedMultiset;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

public class PrimePowers_DefaultImpl extends Mpi_IntegerArrayImpl implements PrimePowers {
	
	private static final Logger LOG = LogManager.getLogger(PrimePowers_DefaultImpl.class);
	private static final boolean DEBUG = false;
	
	private BigInteger[] primes;
	
	/**
	 * Private constructor, call factory methods...
	 * @param dim
	 */
	private PrimePowers_DefaultImpl(int dim) {
		super(dim);
		primes = new BigInteger[dim];
	}
	
	/**
	 * Constructor from a multiset of primes.
	 * @param primepowersMultiset
	 * @return PrimePowers
	 */
	public static PrimePowers_DefaultImpl createFrom(SortedMultiset<BigInteger> primepowersMultiset) {
		// check real size
		int size = 0;
		for (Map.Entry<BigInteger, Integer> primeAndPower : primepowersMultiset.entrySet()) {
			BigInteger prime = primeAndPower.getKey();
			int mult = primeAndPower.getValue();
			if (prime.compareTo(I_1)>0 && mult>0) {
			// TODO: Check with isProbablePrime() ?
				size++;
			}
		}
		// allocate with correct dimension
		PrimePowers_DefaultImpl primePowers = new PrimePowers_DefaultImpl(size);
		// copy values
		int i=0;
		for (Map.Entry<BigInteger, Integer> primeAndPower : primepowersMultiset.entrySet()) {
			if (DEBUG) LOG.debug("primeAndPower = " + primeAndPower);
			BigInteger prime = primeAndPower.getKey();
			int mult = primeAndPower.getValue();
			if (prime.compareTo(I_1)>0 && mult>0) {
				primePowers.primes[i] = prime;
				primePowers.values[i] = mult;
				i++;
			} // else don't need to copy entries with multiplicity 0
		}
		return primePowers;
	}

	/**
	 * Factory method to create Mpi from the prime powers of n.
	 * @param n 
	 * @return PrimePowers
	 */
	public static PrimePowers valueOf(BigInteger n) {
		SortedMultiset<BigInteger> factors = FactorAlgorithm.getDefault().factor(n);
		return PrimePowers_DefaultImpl.createFrom(factors);
	}

	public BigInteger getPrime(int index) {
		return primes[index];
	}
}
