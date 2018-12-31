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
package de.tilman_neumann.jml.factor.siqs.poly;

import java.math.BigInteger;

import de.tilman_neumann.jml.factor.siqs.data.BaseArrays;
import de.tilman_neumann.jml.factor.siqs.sieve.Sieve;
import de.tilman_neumann.jml.factor.siqs.sieve.SieveParams;
import de.tilman_neumann.jml.factor.siqs.tdiv.TDiv_QS;

/**
 * Interface for polynomial generators.
 * @author Tilman Neumann
 */
public interface PolyGenerator {

	/**
	 * @return the name of this polynomial generator
	 */
	String getName();

	/**
	 * Initialize the polynomial generator for a new N.
	 * Inside this method we require aParamGenerator.qCount -> aParamGenerator must have been initialized before.
	 * 
	 * @param k multiplier
	 * @param N
	 * @param kN
	 * @param d the d-parameter of quadratic polynomials Q(x) = (d*a*x + b)^2 - kN
	 * @param sieveParams basic sieve parameters for a new N
	 * @param baseArrays primes, power arrays after adding powers
	 * @param aParamGenerator generator for a-parameters
	 * @param sieveEngine
	 * @param tDivEngine
	 * @param profile
	 */
	void initializeForN(
			int k, BigInteger N, BigInteger kN, int d, SieveParams sieveParams, BaseArrays baseArrays, 
			AParamGenerator aParamGenerator, Sieve sieveEngine, TDiv_QS tDivEngine, boolean profile);

	/**
	 * Compute a new polynomial.
	 * @return
	 */
	void nextPolynomial();

	/**
	 * @return description of the durations of the individual sub-phases
	 */
	PolyReport getReport();

	/**
	 * Release memory after a factorization.
	 */
	void cleanUp();

}