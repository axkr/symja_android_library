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
package de.tilman_neumann.jml.factor.siqs.tdiv;

import java.math.BigInteger;
import java.util.List;

import de.tilman_neumann.jml.factor.base.congruence.AQPair;
import de.tilman_neumann.jml.factor.siqs.data.SolutionArrays;

/**
 * Interface for trial division engines to find the factorization of smooth Q(x) with given x.
 * 
 * @author Tilman Neumann
 */
public interface TDiv_QS {
	
	/**
	 * @return the name of this algorithm
	 */
	public String getName();

	/**
	 * Initialize this trial division engine for a new N.
	 * @param N_dbl
	 * @param kN multiplier k (typically Knuth-Schroeppel) * factor argument N
	 * @param maxQRest the biggest QRest admitted for a smooth relation
	 * @param profile if true then collect timings
	 */
	public void initializeForN(double N_dbl, BigInteger kN, double maxQRest, boolean profile);

	/**
	 * Set prime/power base, polynomial parameters and smallest x-solutions for a new a-parameter.
	 * @param da d*a, with d = 1 or 2 depending on kN % 8
	 * @param b the (first) b-parameter
	 * @param primeSolutions
	 * @param filteredBaseSize number of primes and powers use for sieving
	 * @param unsievedBaseElements Prime base elements that were excluded from sieving, like the q's that give the a-parameter in SIQS
	 */
	void initializeForAParameter(BigInteger da, BigInteger b, SolutionArrays primeSolutions, int filteredBaseSize, int[] unsievedBaseElements);
	
	/**
	 * Set a new b-parameter.
	 * @param b
	 */
	void setBParameter(BigInteger b);
	
	/**
	 * Test if Q(x) is smooth (factors completely over the prime base) or "sufficiently smooth" (factors almost over the prime base)
	 * for all x in the given list.
	 * @param xList
	 * @return the AQ-pairs where Q is at least "sufficiently smooth"
	 */
	List<AQPair> testList(List<Integer> xList);
	
	/**
	 * @return a description of the performed tests.
	 */
	TDivReport getReport();
	
	/**
	 * Release memory after a factorization.
	 */
	void cleanUp();
}
