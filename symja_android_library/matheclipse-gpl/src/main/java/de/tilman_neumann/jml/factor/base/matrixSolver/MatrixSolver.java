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
package de.tilman_neumann.jml.factor.base.matrixSolver;

import static de.tilman_neumann.jml.factor.base.GlobalFactoringOptions.ANALYZE;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import de.tilman_neumann.jml.factor.FactorException;
import de.tilman_neumann.jml.factor.base.congruence.AQPair;
import de.tilman_neumann.jml.factor.base.congruence.Smooth;

/**
 * Base implementation for a congruence equation system (the "LinAlg phase matrix") solver.
 * This is mostly an interface, the true work is happening elsewhere.
 * 
 * @author Tilman Neumann
 */
abstract public class MatrixSolver {
	@SuppressWarnings("unused")
	private static final Logger LOG = LogManager.getLogger(MatrixSolver.class);

	/** factor tester */
	private FactorTest factorTest;

	// for debugging only
	private int testedNullVectorCount;
	
	public abstract String getName();
	
	/**
	 * Initialize for a new N.
	 * @param N
	 * @param factorTest
	 */
	public void initialize(BigInteger N, FactorTest factorTest) {
		this.factorTest = factorTest;
		if (ANALYZE) this.testedNullVectorCount = 0;
	}

	/**
	 * Main method to solve a congruence equation system.
	 * @param congruences the congruences forming the equation system
	 * @throws FactorException if a factor of N was found
	 */
	abstract public void solve(Collection<? extends Smooth> congruences) throws FactorException;

	public void processNullVector(Set<AQPair> aqPairs) throws FactorException {
		// found square congruence -> check for factor
		if (ANALYZE) testedNullVectorCount++;
		factorTest.testForFactor(aqPairs);
		// no factor exception -> drop improper square congruence
	}
	
	/**
	 * @return the number of solver runs needed (so far). Is not populated (i.e. 0) if ANALYZE_SOLVER_RUNS==false.
	 */
	public int getTestedNullVectorCount() {
		return testedNullVectorCount;
	}
	
	/**
	 * Release memory after a factorization.
	 */
	public void cleanUp() {
		factorTest = null;
	}
}
