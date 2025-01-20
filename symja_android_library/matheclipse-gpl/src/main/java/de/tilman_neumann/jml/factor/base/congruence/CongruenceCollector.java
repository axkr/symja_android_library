/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018-2021 Tilman Neumann - tilman.neumann@web.de
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
import java.util.Collection;
import java.util.List;

import de.tilman_neumann.jml.factor.base.matrixSolver.FactorTest;
import de.tilman_neumann.jml.factor.base.matrixSolver.MatrixSolver;

public interface CongruenceCollector {

	/**
	 * Initialize congruence collector for a new N.
	 * @param N
	 * @param primeBaseSize
	 * @param matrixSolver
	 * @param factorTest
	 */
	void initialize(BigInteger N, int primeBaseSize, MatrixSolver matrixSolver, FactorTest factorTest);

	/**
	 * Re-initialize the prime base size. This is useful only for algorithms that combine relations from different prime bases.
	 * @param newPrimeBaseSize
	 */
	void setPrimeBaseSize(int newPrimeBaseSize);

	/**
	 * Collect AQ pairs and run the matrix solver if appropriate.
	 * In a multi-threaded factoring algorithm, this method needs to be run in a block synchronized on this.
	 * This also speeds up single-threaded solvers like Block-Lanczos, because on modern CPUs single threads run at a higher clock rate.
	 * @param aqPairs
	 * @output this.factor
	 */
	void collectAndProcessAQPairs(List<AQPair> aqPairs);

	/**
	 * Collect a single AQ pair and run the matrix solver if appropriate.
	 * In a multi-threaded factoring algorithm, this method needs to be run in a block synchronized on this.
	 * This also speeds up single-threaded solvers like Block-Lanczos, because on modern CPUs single threads run at a higher clock rate.
	 * @param aqPair
	 * @output this.factor
	 */
	void collectAndProcessAQPair(AQPair aqPair);

	/**
	 * @return number of smooth congruences found so far.
	 */
	int getSmoothCongruenceCount();

	/**
	 * @return smooth congruences found so far.
	 */
	Collection<Smooth> getSmoothCongruences();

	/**
	 * @return number of partial congruences found so far.
	 */
	int getPartialCongruenceCount();

	/**
	 * @return the factor that was found or null
	 */
	BigInteger getFactor();
	
	CongruenceCollectorReport getReport();

	long getCollectDuration();

	long getSolverDuration();

	/**
	 * @return the number of solvers runs required to find a factor (should be 1)
	 */
	int getSolverRunCount();

	/**
	 * @return the number of null vector tests required to find a factor
	 */
	int getTestedNullVectorCount();

	/**
	 * Release memory after a factorization.
	 */
	void cleanUp();

}