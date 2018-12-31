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

import java.util.Set;

import de.tilman_neumann.jml.factor.FactorException;
import de.tilman_neumann.jml.factor.base.congruence.AQPair;

/**
 * Interface for final factor tests when a square congruence A^2 == Q (mod kN) has been found.
 * The congruence may be improper though...
 * 
 * @author Tilman Neumann
 */
public interface FactorTest {

	/** 
	 * @return the algorithm name 
	 */
	String getName();

	/**
	 * Test if a square congruence A^2 == Q (mod kN) gives a factor of N.
	 *
	 * @param aqPairs
	 * @throws FactorException
	 */
	void testForFactor(Set<AQPair> aqPairs) throws FactorException;
}