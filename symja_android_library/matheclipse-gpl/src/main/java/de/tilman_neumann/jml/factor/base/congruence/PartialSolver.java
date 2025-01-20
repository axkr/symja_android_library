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
package de.tilman_neumann.jml.factor.base.congruence;

import java.util.Collection;

/**
 * Interface for solvers used to find smooth from partial relations.
 * 
 * @author Tilman Neumann
 */
public interface PartialSolver {

	/**
	 * @return the name of the algorithm
	 */
	String getName();

	/** Initialize this partial solver for a new factor argument. */
	void initializeForN();
	
	/**
	 * Solve a partial congruence equation system.
	 * @param congruences the partial congruence equation system
	 * @return list of smooth congruences found
	 */
	Smooth solve(Collection<? extends Partial> congruences);
	
	/**
	 * @return the size of the biggest partial matrix found for some N
	 */
	int getMaxMatrixSize();
	
	/**
	 * Release memory after a factorization.
	 */
	void cleanUp();
}
