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
package de.tilman_neumann.jml.factor.siqs.data;

/**
 * Passive data structure bundling primes/powers and their smallest x-solutions.
 * 
 * Having a structure with several arrays of the same size is faster than having an array of a structure,
 * because the former permits to exploit AVX/SSE mechanisms in Java 8.
 * 
 * @author Tilman Neumann
 */
public class SolutionArrays extends BaseArrays {
	public int[] x1Array;
	public int[] x2Array;
	public int[][] Bainv2Array;
	
	/**
	 * Full constructor, allocates all arrays.
	 * @param solutionsCount
	 * @param qCount
	 */
	public SolutionArrays(int solutionsCount, int qCount) {
		super(solutionsCount);
		x1Array = new int[solutionsCount];
		x2Array = new int[solutionsCount];
		// Bainv2: full initialization.
		// The sub-arrays are in reverse order compared to [Contini], which almost doubles the speed of nextXArrays().
		// The maximum v value is qCount-1 -> allocation with qCount-1 is sufficient.
		Bainv2Array = new int[qCount-1][solutionsCount];
	}
}
