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
package de.tilman_neumann.jml.partitions;

import java.math.BigInteger;
import java.util.Map;
import java.util.SortedSet;

import org.apache.log4j.Logger;

import de.tilman_neumann.jml.Divisors;
import de.tilman_neumann.jml.factor.FactorAlgorithm;
import de.tilman_neumann.util.ConfigUtil;
import de.tilman_neumann.util.SortedMultiset;

import static de.tilman_neumann.jml.base.BigIntConstants.*;

public class PrimePowers_DefaultImpl extends Mpi_IntegerArrayImpl implements PrimePowers {
	
	private static final Logger LOG = Logger.getLogger(PrimePowers_DefaultImpl.class);
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
		SortedMultiset<BigInteger> factors = FactorAlgorithm.DEFAULT.factor(n);
		return PrimePowers_DefaultImpl.createFrom(factors);
	}

	public BigInteger getPrime(int index) {
		return primes[index];
	}
	
	/**
	 * Check relationship between set of divisors and powermap.
	 * Hypothesis confirmed from 0..203846.
	 * 
	 * @param args ignored
	 */
	public static void main(String[] args) {
		ConfigUtil.initProject();
		for (int n=0; n<1000; n++) {
			BigInteger bigN = BigInteger.valueOf(n);
			SortedSet<BigInteger> divisors = Divisors.getDivisors(bigN);
			int numberOfDivisors = divisors.size();
			PrimePowers primePowers = PrimePowers_DefaultImpl.valueOf(bigN);
			MpiPowerMap powerMap = MpiPowerMap.create(primePowers);
			int powerMapSize = powerMap.size();
			LOG.info("n=" + n + " has " + numberOfDivisors + " divisors, and power map has " + powerMapSize + " entries");
			int correctedPowerMapSize = n>0 ? powerMapSize + primePowers.getDim() + 1 : 0;
			LOG.info("correctedPowerMapSize = " + correctedPowerMapSize);
			// the power map is missing the unit entries (only one prime) and the empty entry!
			if (numberOfDivisors!=correctedPowerMapSize) {
				LOG.info("n = " + n);
				LOG.info("divisors = " + divisors);
				LOG.info("powerMap = " + powerMap);
				throw new IllegalStateException("my hypothesis is wrong for n=" + n);
			}
		}
	}
}
