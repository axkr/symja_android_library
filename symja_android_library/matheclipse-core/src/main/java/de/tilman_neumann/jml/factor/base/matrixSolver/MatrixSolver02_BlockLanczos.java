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

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import de.tilman_neumann.jml.factor.FactorException;
import de.tilman_neumann.jml.factor.base.congruence.AQPair;
import de.tilman_neumann.jml.factor.base.congruence.Congruence;

/**
 * An adapter for Dario Alpern's Block-Lanczos solver.
 * 
 * Appears to be faster than the Gaussian solver starting at N>200 bit.
 * 
 * @author Tilman Neumann
 */
public class MatrixSolver02_BlockLanczos extends MatrixSolver {
//	private static final boolean DEBUG = false;

	private BlockLanczos blockLanczosSolver = new BlockLanczos();
	
	@Override
	public String getName() {
		return "solver02_BL";
	}
	
	@Override
	protected void solve(List<Congruence> congruences, Map<Integer, Integer> factors_2_columnIndices) throws FactorException {
		// create the matrix:
		// * the rows are in the same order as in the congruences list
		// * we fill a row with the column indices where the congruence has a factor with odd exponent
		int matrixBlength = congruences.size();
		int[][] matrixB = new int[matrixBlength][];
		int i=0;
		for (Congruence congruence : congruences) {
			// row entry = set of column indices where the congruence has a factor with odd exponent
			Integer[] oddExpFactors = congruence.getMatrixElements();
			int[] matrixRow = new int[oddExpFactors.length];
			int j=0;
			for (Integer oddExpFactor : oddExpFactors) {
				// columnIndex should not be bigger than the number of congruences
				int columnIndex = factors_2_columnIndices.get(oddExpFactor);
//				if (DEBUG) assertTrue(columnIndex <= matrixBlength);
				matrixRow[j++] = columnIndex;
			}
			matrixB[i++] = matrixRow;
		}
		//LOG.debug("constructed matrix with " + matrixBlength + " rows and " + factors_2_columnIndices.size() + " columns");
		
		// invoke Alperns Block Lanczos solver
		int[] matrixV = blockLanczosSolver.computeBlockLanczos(matrixB, matrixBlength);
		//LOG.debug("BlockLanzcos returned matrixV = " + Arrays.toString(matrixV));
		
		// See Siqs.LinearAlgebraPhase() for how to interprete matrixV:
		// There can be up to 32 potential solutions encoded in matrixV!
		// One in bit 0 of all ints, the next in bit 1 of all ints, and so on.
  		for (int mask = 1; mask != 0; mask *= 2) {
  			HashSet<AQPair> totalAQPairs = new HashSet<AQPair>(); // Set required for the "xor"-operation below
  			for (int row = matrixBlength - 1; row >= 0; row--) {
  				if ((matrixV[row] & mask) != 0) {
  					// the current row belongs to the solution encoded in matrixV by the bit addressed by mask.
  					// the row indices are the same as in my congruences list.
  					Congruence congruence = congruences.get(row);
  					//LOG.info("mask=" + mask + ": add congruence " + congruence);
					// add the new AQ-pairs via "xor"
					congruence.addMyAQPairsViaXor(totalAQPairs);
  				}
  			}
  			
  			if (!totalAQPairs.isEmpty()) {
  				// Sometimes the BlockLanczos() method returns non-null-vectors (having q-factors with odd exponent),
  				// but it did not seem beneficial to test the exponents before calling processNullVector(.)
	  			// So just "return" the AQ-pairs of the null vector:
	  			nullVectorProcessor.processNullVector(totalAQPairs);
  			}
  		}
	}
}
