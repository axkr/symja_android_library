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
package de.tilman_neumann.jml.factor.cfrac.tdiv;

import java.math.BigInteger;

import de.tilman_neumann.jml.factor.FactorException;
import de.tilman_neumann.jml.factor.base.congruence.AQPair;

/**
 * Interface for auxiliary factor algorithms to find smooth decompositions of Q's.
 * @author Tilman Neumann
 */
public interface TDiv_CF63 {
	
	/**
	 * @return the name of this algorithm
	 */
	public String getName();

	/**
	 * Initialize for a new N.
	 * @param N
	 * @param maxQRest
	 */
	public void initialize(BigInteger N, double maxQRest);

	/**
	 * Initialize this factorizer for a new k; in particular set the prime base to be used for trial division.
	 * @param kN
	 * @param primeBaseSize the true prime base size (the arrays are preallocated with a bigger length)
	 * @param primesArray prime base in ints
	 * @throws FactorException 
	 */
	public void initialize(BigInteger kN, int primeBaseSize, int[] primesArray) throws FactorException;
	
	/**
	 * Check if Q is smooth (factors completely over the prime base) or "sufficiently smooth" (factors almost over the prime base).
	 *
	 * @param A
	 * @param Q
	 * @return an AQ-pair if Q is at least "sufficiently smooth", null else
	 */
	public AQPair test(BigInteger A, long Q);
}
