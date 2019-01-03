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
package de.tilman_neumann.jml.primes.exact;

import java.math.BigInteger;
import java.util.HashSet;

//import org.apache.log4j.Logger;

/**
 * Singleton implementation of a set containing a first few small primes.
 * @author Tilman Neumann
 */
public class SmallPrimesSet extends HashSet<Integer> {

	private static final long serialVersionUID = -2700398407077930815L;
//	private static final Logger LOG = Logger.getLogger(SmallPrimesSet.class);
//	private static final boolean DEBUG = false;
		
	private int limit;

	private static final AutoExpandingPrimesArray THE_PRIMES_ARRAY = AutoExpandingPrimesArray.get();

	private static final SmallPrimesSet THE_SET = new SmallPrimesSet();
	
	/**
	 * Not public, use get() method to access the singleton.
	 */
	private SmallPrimesSet() {
		this.add(2);
		limit = 2;
	}

	/**
	 * @return the small primes set containing all primes <= bound
	 */
	public static SmallPrimesSet get() {
		return THE_SET;
	}
	
	/**
	 * Ensures that this set contains at least the first desiredCount primes.
	 * @param desiredCount
	 * @return SmallPrimesSet containing at least desiredCount primes
	 */
	public SmallPrimesSet ensurePrimeCount(int desiredCount) {
		int oldSize = this.size();
		if (oldSize < desiredCount) {
//			if (DEBUG) LOG.debug("old size = " + oldSize  + ",  desired size = " + desiredCount);
			AutoExpandingPrimesArray primes = THE_PRIMES_ARRAY.ensurePrimeCount(desiredCount);
			
			for (int i = oldSize; i<desiredCount; i++) {
				this.add(primes.getPrime(i));
			}
			int newSize = this.size();
			limit = primes.getPrime(newSize-1);
//			if (DEBUG) LOG.debug("Enhanced small primes set has " + newSize + " elements.");
		}
		return this;
	}

	/**
	 * Ensures that this set contains at least all primes <= desiredLimit.
	 * @param desiredLimit
	 * @return SmallPrimesSet containing at least all primes <= desiredLimit
	 */
	public SmallPrimesSet ensureLimit(int desiredLimit) {
		if (limit < desiredLimit) {
			int oldSize = this.size();
//			if (DEBUG) LOG.debug("old limit = " + limit  + ", desired limit = " + desiredLimit);
			AutoExpandingPrimesArray primes = THE_PRIMES_ARRAY.ensureLimit(desiredLimit);
			
			int i, p;
			for (i = oldSize; (p=primes.getPrime(i))<desiredLimit; i++) {
				this.add(p);
			}
			int newSize = this.size();
			limit = primes.getPrime(newSize-1);
//			if (DEBUG) {
//				LOG.debug("old size = " + oldSize + ", new size = " + newSize);
//				assertTrue(limit >= desiredLimit);
//			}
		}
		return this;
	}

	// for debugging only
	//	@Override
	//	public boolean add(Integer p) {
	//		LOG.info("add p = " + p);
	//		return super.add(p);
	//	}
	
	/**
	 * @return the biggest integer tested for being prime covered by this set.
	 */
	public int getLimit() {
		return limit;
	}
	
	/**
	 * @param arg
	 * @return true if the small primes set contains an integer with the value of 'arg'
	 */
	public boolean contains(BigInteger arg) {
		if (arg.bitLength() >= 32) return false;
		int arg_int = arg.intValue();
		return arg_int<=limit && super.contains(Integer.valueOf(arg_int));
	}

	/**
	 * @param arg
	 * @return true if the small primes set contains an integer with the value of 'arg'
	 */
	public boolean contains(long arg) {
		return arg<=limit && super.contains(Integer.valueOf((int)arg));
	}
	
	/**
	 * @param arg int
	 * @return true if the small primes set contains 'arg'
	 */
	public boolean contains(int arg) {
		return arg<=limit && super.contains(Integer.valueOf(arg));
	}
}
