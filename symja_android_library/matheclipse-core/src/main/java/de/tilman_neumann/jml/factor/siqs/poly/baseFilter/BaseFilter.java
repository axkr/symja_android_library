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
package de.tilman_neumann.jml.factor.siqs.poly.baseFilter;

import de.tilman_neumann.jml.factor.siqs.data.BaseArrays;
import de.tilman_neumann.jml.factor.siqs.data.SolutionArrays;

/**
 * Interface for the step filtering some elements out off the (prime/power) base.
 * 
 * We must filter out at least the q-values that give the a-parameter when multiplied (and their powers),
 * because the special treatment for q-values has been removed from SiqsPolyGenerator.computeFirstXArrays()
 * for performance reasons.
 * 
 * @author Tilman Neumann
 */
public interface BaseFilter {
	/**
	 * Filtering results.
	 */
	public static class Result {
		public SolutionArrays solutionArrays;
		public int filteredBaseSize;
		public int[] filteredOutBaseElements;
		
		public Result(SolutionArrays solutionArrays, int filteredBaseSize, int[] filteredOutBaseElements) {
			this.solutionArrays = solutionArrays;
			this.filteredBaseSize = filteredBaseSize;
			this.filteredOutBaseElements = filteredOutBaseElements;
		}
	}
	
	/**
	 * Filter base arrays, fill solutionArrays with the result.
	 * @param solutionArrays
	 * @param baseArrays
	 * @param mergedBaseSize
	 * @param qArray
	 * @param qCount
	 * @param k
	 * @return
	 */
	public Result filter(SolutionArrays solutionArrays, BaseArrays baseArrays, int mergedBaseSize, int[] qArray, int qCount, int k);
	
	/**
	 * @return algorithm name
	 */
	public String getName();
}
