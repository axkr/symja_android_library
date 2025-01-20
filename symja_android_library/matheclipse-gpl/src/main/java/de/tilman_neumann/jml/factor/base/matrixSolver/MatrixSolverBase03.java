/*
 * java-math-library is a Java library focused on number theory, but not necessarily limited to it. It is based on the PSIQS 4.0 factoring project.
 * Copyright (C) 2018 Tilman Neumann - tilman.neumann@web.de
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

import java.util.Arrays;
import java.util.List;

import de.tilman_neumann.jml.factor.base.congruence.Smooth;
import de.tilman_neumann.jml.factor.base.matrixSolver.util.CompareCongruence;

/**
 * Base implementation for a congruence equation system (the "LinAlg phase matrix") solver.
 * Much faster than the first version due to great improvements by Dave McGuigan.
 * 
 * @author Tilman Neumann, Dave McGuigan
 */
abstract public class MatrixSolverBase03 extends MatrixSolverBase02 {
	
	/**
	 * Sort smooths be smallest number of column bits set
	 */
	@Override
	protected void sortSmooths(List<Smooth> list) {
		Object[] ns = list.toArray();
		Arrays.sort(ns, new CompareCongruence());
		list.clear();
		for (Object o : ns) {
			Smooth s = (Smooth)o;
			list.add(s);
		}
	}
}
