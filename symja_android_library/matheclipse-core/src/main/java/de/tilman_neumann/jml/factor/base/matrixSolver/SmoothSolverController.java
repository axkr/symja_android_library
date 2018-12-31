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
package de.tilman_neumann.jml.factor.base.matrixSolver;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Set;

//import org.apache.log4j.Logger;

import de.tilman_neumann.jml.factor.FactorException;
import de.tilman_neumann.jml.factor.base.congruence.AQPair;
import de.tilman_neumann.jml.factor.base.congruence.Congruence;

/**
 * A controller for the matrix solver used for smooth congruence equations systems.
 * Solving such a system (mod N) may give square congruences, and a factor of N if such a square congruence is proper.
 * The controller pattern allows to have distinct matrix solver implementations, and a fallback procedure
 * that applies when a solver run gives only improper solutions.
 * 
 * @author Tilman Neumann
 */
public class SmoothSolverController implements NullVectorProcessor {
	@SuppressWarnings("unused")
//	private static final Logger LOG = Logger.getLogger(SmoothSolverController.class);

	private MatrixSolver matrixSolver;

	/** factor tester */
	private FactorTest factorTest;

	// for debugging only
	private int testedNullVectorCount;

	public SmoothSolverController(MatrixSolver matrixSolver) {
		// this hook would be a memory leak if we'ld create many pairs of controller and solver objects;
		// but we have only 1 object pair in the whole application run time
		matrixSolver.setNullVectorProcessor(this);
		this.matrixSolver = matrixSolver;
	}
	
	public String getName() {
		return matrixSolver.getName();
	}
	
	public void initialize(BigInteger N, FactorTest factorTest) {
		this.factorTest = factorTest;
		this.testedNullVectorCount = 0;
	}
	
	/**
	 * Solve a smooth congruence equation system.
	 * @param rawCongruences the smooth congruence equation system
	 * @throws FactorException if a factor of N was found
	 */
	public void solve(Collection<? extends Congruence> rawCongruences) throws FactorException {
		matrixSolver.solve(rawCongruences);
	}

	@Override
	public void processNullVector(Set<AQPair> aqPairs) throws FactorException {
		// found square congruence -> check for factor
		testedNullVectorCount++;
		factorTest.testForFactor(aqPairs);
		// no factor exception -> drop improper square congruence
	}
	
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
