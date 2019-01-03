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
package de.tilman_neumann.jml.factor.base.congruence;

import java.math.BigInteger;

import de.tilman_neumann.jml.factor.base.SortedIntegerArray;
import de.tilman_neumann.util.SortedMultiset;
import de.tilman_neumann.util.SortedMultiset_BottomUp;

/**
 * An <em>elementary</em> smooth or partially smooth congruence A^2 == Q (mod N).
 * Elementary means that only one (A,Q) pair is involved.
 * 
 * Factoring large numbers will produce millions of partials, and their memory demands dominate the memory consumption
 * of the whole quadratic sieve algorithm. To store them as efficiently as possible, large factors are added by subclasses.
 * 
 * The estimated memory requirement of the fields in this class is 240 byte; adding 16 bytes for its own object header gives around 256 byte.
 * 
 * @author Tilman Neumann
 */
abstract public class AQPair {
	
	private BigInteger A; // needs about 112 byte for a 350 bit factor argument on a 64-bit machine
	
	/** small factors of Q */
	int[] smallFactors; // needs about 32+4*n = 72 byte for n=10 small factors
	short[] smallFactorExponents; // needs about 32+2*n = 52 byte for n=10 small factors
	
	/** AQ-pairs never change -> compute hashCode only once */
	private int hashCode;
	
	/**
	 * Full constructor.
	 * @param A
	 * @param smallFactors small factors of Q
	 */
	public AQPair(BigInteger A, SortedIntegerArray smallFactors) {
		// The congruence A^2 == Q (mod kN) does not distinguish between +A and -A.
		// But avoiding such duplicates is asymptotically unfavourable because their likelihood decreases quickly.
		this.A = A;
		// Precompute hashCode
		this.hashCode = A.hashCode();
		// Copy small factors of Q
		this.smallFactors = smallFactors.copyFactors();
		this.smallFactorExponents = smallFactors.copyExponents();
	}

	public BigInteger getA() {
		return A;
	}

	/**
	 * @return all Q-factors with exponents.
	 * This method is only called in the final test of null vectors found by the smooth solver;
	 * but then it is needed for a whole bunch of AQPairs.
	 */
	abstract public SortedMultiset<Integer> getAllQFactors();

	/**
	 * Building block to implement the method above.
	 * @return SortedMultiset containing all small factors of Q
	 */
	protected SortedMultiset<Integer> getSmallQFactors() {
		SortedMultiset<Integer> allFactors = new SortedMultiset_BottomUp<Integer>();
		for (int i=0; i<smallFactors.length; i++) {
			allFactors.add(smallFactors[i], smallFactorExponents[i]);
		}
		return allFactors;
	}

	/**
	 * @return the total number of large factors of Q in this AQPair.
	 */
	abstract public int getNumberOfLargeQFactors();
	
	/**
	 * hashCode() and equals() must be based on A to avoid duplicates.
	 * Q is not required, not even in CFrac.
	 */
	@Override
	public int hashCode() {
		// used in BlockLanczos solver and (Partial/Smooth)Congruence constructors
		//LOG.debug("hashCode()", new Throwable());
		return hashCode;
	}

	/**
	 * hashCode() and equals() must be based on A to avoid duplicates.
	 * Q is not required, not even in CFrac.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		AQPair other = (AQPair) obj;
		// equal objects must have the same hashCode
		if (hashCode != other.hashCode) return false;
		// since Q=A^2-kN is a function of A, we only need A
		return this.A.equals(other.A);
	}

	@Override
	public String toString() {
		return "A = {" + A + "}, Q = {" + getAllQFactors().toString("*", "^") + "}";
	}
}
